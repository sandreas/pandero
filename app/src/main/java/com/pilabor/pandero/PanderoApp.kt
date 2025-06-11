package com.pilabor.pandero

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule

class PanderoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PanderoApp)
            modules(defaultModule)
        }
    }
}