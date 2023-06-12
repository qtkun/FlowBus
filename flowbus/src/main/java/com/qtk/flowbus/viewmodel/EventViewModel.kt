package com.qtk.flowbus.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.qtk.flowbus.observe.OnReceived
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class EventViewModel: ViewModel() {
    private val eventFlows = ConcurrentHashMap<String, MutableSharedFlow<Any>>()
    private val stickyEventFlows = ConcurrentHashMap<String, MutableSharedFlow<Any>>()

    private fun getEventFlow(name: String, isSticky: Boolean): MutableSharedFlow<Any> {
        return if (isSticky) {
            stickyEventFlows[name]
                ?: MutableSharedFlow<Any>(1, 1, BufferOverflow.DROP_OLDEST).also {
                stickyEventFlows[name] = it
            }
        } else {
            eventFlows[name] ?: MutableSharedFlow<Any>(0).also {
                eventFlows[name] = it
            }
        }
    }

    fun postEventDelay(
        eventName: String,
        value: Any,
        delayTimeMillis: Long
    ) {
        listOf(
            getEventFlow(eventName, true),
            getEventFlow(eventName, false)
        ).forEach { eventFlow ->
            viewModelScope.launch {
                delay(delayTimeMillis)
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
        lifecycleOwner.lifecycleScope.launch {
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