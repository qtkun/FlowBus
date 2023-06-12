package com.qtk.flowbus.post

import com.qtk.flowbus.ApplicationViewModelStoreOwner
import com.qtk.flowbus.viewmodel.EventViewModel

inline fun <reified T> postEventDelay(value: T, delayTimeMillis: Long) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .postEventDelay(T::class.java.simpleName, value!!, delayTimeMillis)
}

inline fun <reified T> postEvent(value: T) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .postEventDelay(T::class.java.simpleName, value!!, 0L)
}

fun <T> postEventDelay(value: T, clazz: Class<T>, delayTimeMillis: Long) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .postEventDelay(clazz.simpleName, value!!, delayTimeMillis)
}

fun <T> postEvent(value: T, clazz: Class<T>) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .postEventDelay(clazz.simpleName, value!!, 0L)
}

fun <T> postEventDelay(eventName: String, value: T, delayTimeMillis: Long) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .postEventDelay(eventName, value!!, delayTimeMillis)
}

fun <T> postEvent(eventName: String, value: T) {
    ApplicationViewModelStoreOwner.getApplicationViewModel(EventViewModel::class.java)
        .postEventDelay(eventName, value!!, 0L)
}