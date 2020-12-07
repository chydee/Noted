package com.chydee.notekeeper.utils.app

import android.app.Application
import androidx.databinding.library.BuildConfig
import timber.log.Timber

class NotedApp : Application() {


    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}