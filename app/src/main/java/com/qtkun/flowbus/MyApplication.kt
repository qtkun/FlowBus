package com.qtkun.flowbus

import android.app.Application
import com.qtk.flowbus.FlowBus

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FlowBus.init(this)
    }
}