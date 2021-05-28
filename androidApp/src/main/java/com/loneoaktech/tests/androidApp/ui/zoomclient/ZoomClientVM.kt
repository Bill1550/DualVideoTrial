package com.loneoaktech.tests.androidApp.ui.zoomclient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.loneoaktech.tests.androidApp.BuildConfig
import timber.log.Timber
import us.zoom.sdk.ZoomSDK
import us.zoom.sdk.ZoomSDKInitParams
import us.zoom.sdk.ZoomSDKInitializeListener

class ZoomClientVM(application: Application) : AndroidViewModel(application) {

    fun init() {
        Timber.i("Attempting to init Zoom client")

        val sdk = ZoomSDK.getInstance()

        val params = ZoomSDKInitParams().apply {
            appKey = BuildConfig.zoomClientAppKey
            appSecret = BuildConfig.zoomClientSecret
            domain = "zoom.us"
            enableLog = true
        }



        val listener = object: ZoomSDKInitializeListener {
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                Timber.i("Zoom Client SDK initialize result: errorCode=$errorCode internalCode=$internalErrorCode")
            }

            override fun onZoomAuthIdentityExpired() {
                Timber.i("Zoom Client auth identity expired")
            }
        }

        Timber.i("initializing zoom client sdk...")
        sdk.initialize(getApplication(), listener, params)
    }
}