package com.walnuttube

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WalnutTubeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}