package com.pilabor.pandero

import android.app.Application
import com.pilabor.pandero.di.NetworkModule
import com.pilabor.pandero.utils.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

class PanderoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PanderoApp)
            // needs "build > assemble app" for new modules to work properly
            modules(defaultModule, NetworkModule().module)
        }
        NotificationHelper.createNotificationChannel(this)
    }
}