package com.stylingandroid.biometrics

import android.app.Application
import timber.log.Timber

class BiometricsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
