package dev.testing.sampleandtest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SampleAndTestApp :Application() {



    companion object {
        lateinit var instance: SampleAndTestApp
            private set
     }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}