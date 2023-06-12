package com.qtk.flowbus.observe

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.qtk.flowbus.ApplicationViewModelStoreOwner
import com.qtk.flowbus.viewmodel.EventViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@MainThread
inline fun <reified T> LifecycleOwner.observeEvent(
    isSticky: Boolean = false,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    onReceived: OnReceived<T>
) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .observeEvent(this, minActiveState, dispatcher, T::class.java.simpleName, isSticky, onReceived)
}

@MainThread
inline fun <reified T> observeEvent(
    isSticky: Boolean = false,
    onReceived: OnReceived<T>
) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .observeWithoutLifecycle(T::class.java.simpleName, isSticky, onReceived)
}

@MainThread
fun <T> LifecycleOwner.observeEvent(
    eventName: String,
    isSticky: Boolean = false,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    onReceived: OnReceived<T>
) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .observeEvent(this, minActiveState, dispatcher, eventName, isSticky, onReceived)
}

@MainThread
fun <T> observeEvent(
    eventName: String,
    isSticky: Boolean = false,
    onReceived: OnReceived<T>
) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .observeWithoutLifecycle(eventName, isSticky, onReceived)
}

fun interface OnReceived<T>: (T) -> Unit