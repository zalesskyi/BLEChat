package com.zalesskyi.android.blechat

import android.app.Application

class BleChatApp : Application() {

    companion object {

        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}