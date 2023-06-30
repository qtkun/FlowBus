package com.qtk.flowbus.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.qtk.flowbus.observe.OnReceived
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class EventViewModel: ViewModel() {
    companion object {
        private const val REPLAY = 1
        private const val EXTRA_BUFFER_SIZE = 10
    }
    private val eventFlows = HashMap<String, MutableSharedFlow<Any>>()
    private val stickyEventFlows = HashMap<String, MutableSharedFlow<Any>>()

    private val mutex = Mutex()

    private suspend fun getEventFlow(eventName: String, isSticky: Boolean): MutableSharedFlow<Any> {
        return if (isSticky) {
            mutex.withLock {
                stickyEventFlows[eventName] ?: MutableSharedFlow<Any>(REPLAY, EXTRA_BUFFER_SIZE, BufferOverflow.DROP_OLDEST).also {
                    stickyEventFlows[eventName] = it
                }
            }
        } else {
            mutex.withLock {
                eventFlows[eventName] ?: MutableSharedFlow<Any>(REPLAY, EXTRA_BUFFER_SIZE, BufferOverflow.DROP_OLDEST).also {
                    eventFlows[eventName] = it
                }
            }
        }
    }

    fun postEventDelay(
        eventName: String,
        value: Any,
        delayTimeMillis: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val eventFlows = listOf(
                getEventFlow(eventName, true),
                getEventFlow(eventName, false)
            )
            delay(delayTimeMillis)
            eventFlows.forEach { eventFlow ->
                eventFlow.emit(value)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T>observeEvent(
        lifecycleOwner: LifecycleOwner,
        minActiveState: Lifecycle.State,
        dispatcher: CoroutineDispatcher,
        eventName: String,
        isSticky: Boolean,
        onReceived: OnReceived<T>
    ) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
                getEventFlow(eventName, isSticky)
                    .distinctUntilChanged()
                    .collect {
                        launch(dispatcher) {
                            invokeReceived(it, onReceived)
                            if (!isSticky) {
                                eventFlows[eventName]?.resetReplayCache()
                            }
                        }
                    }
            }
        }
    }

    fun <T> observeWithoutLifecycle(
        eventName: String,
        isSticky: Boolean,
        onReceived: OnReceived<T>
    ) = viewModelScope.launch {
        getEventFlow(eventName, isSticky)
            .distinctUntilChanged()
            .collect { value ->
                invokeReceived(value, onReceived)
            }
    }

    fun removeStickEvent(eventName: String) {
        stickyEventFlows.remove(eventName)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearStickEvent(eventName: String) {
        stickyEventFlows[eventName]?.resetReplayCache()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> invokeReceived(value: Any, onReceived: OnReceived<T>) {
        try {
            onReceived.invoke(value as T)
        } catch (e: ClassCastException) {
            Log.w("FlowBus","class cast error on message received: $value")
        } catch (e: Exception) {
            Log.w("FlowBus", "error on message received: $value")
        }
    }

}