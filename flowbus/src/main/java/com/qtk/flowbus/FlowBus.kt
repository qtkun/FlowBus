package com.qtk.flowbus

import android.app.Application

object FlowBus {
    lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }
}