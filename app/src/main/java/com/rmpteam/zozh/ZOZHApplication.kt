package com.rmpteam.zozh

import android.app.Application
import com.rmpteam.zozh.di.AppContainer
import com.rmpteam.zozh.di.OfflineAppContainer

class ZOZHApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = OfflineAppContainer(this)
    }
}
