package com.allinone.app

import android.app.Application
import com.allinone.app.data.AppContainer

class AllInOneApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
