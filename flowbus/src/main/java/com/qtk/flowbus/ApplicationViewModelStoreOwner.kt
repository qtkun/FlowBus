package com.qtk.flowbus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object ApplicationViewModelStoreOwner: ViewModelStoreOwner {
    private val mViewModelStore = ViewModelStore()

    private val mApplicationProvider by lazy {
        ViewModelProvider(ApplicationViewModelStoreOwner,
            ViewModelProvider.AndroidViewModelFactory.getInstance(FlowBus.application))
    }

    fun <VM: ViewModel>getApplicationViewModel(clazz: Class<VM>): VM {
        return mApplicationProvider[clazz]
    }

    override val viewModelStore: ViewModelStore
        get() = mViewModelStore
}