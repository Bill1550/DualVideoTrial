package com.loneoaktech.apps.androidApp

import android.app.Application
import timber.log.Timber

@Suppress("unused") // lint bug, class is referenced in the manifest.
class DualVideoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant( Timber.DebugTree() )
        Timber.i("onCreate")
    }
}