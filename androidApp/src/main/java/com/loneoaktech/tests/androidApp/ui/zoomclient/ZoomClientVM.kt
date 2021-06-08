package com.loneoaktech.tests.androidApp.ui.zoomclient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.loneoaktech.tests.androidApp.BuildConfig
import timber.log.Timber
import us.zoom.sdk.*

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

                if ( errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
                    Timber.e("SDK Initialization failure: errorCode=$errorCode, internalCode=$internalErrorCode")
                } else {
                    if ( login() ) {

                        // logged in, try to create a meeting
                        sdk.meetingService?.let { ms ->
                            ms.addListener { status, errorCode, internalErrorCode ->
                                Timber.i("Meeting status change: status=$status, errorCode=$errorCode, internalCode=$internalErrorCode")
                            }

                            val opts = InstantMeetingOptions().apply {

                            }

                            Timber.i("Starting meeting...")
                            ms.startInstantMeeting( getApplication(), opts ).also { error ->
                                Timber.i("startInstantMeeting returned $error")
                            }

                        }?: Timber.e("Meeting service not available")

                    }
                }
            }

            override fun onZoomAuthIdentityExpired() {
                Timber.i("Zoom Client auth identity expired")
            }
        }

        Timber.i("initializing zoom client sdk...")
        sdk.initialize(getApplication(), listener, params)
    }

    private fun login(): Boolean {
        val sdk = ZoomSDK.getInstance()

        return if (sdk.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            Timber.i("Auto login successful")
            true
        } else {
            sdk.loginWithZoom("hartwg@gmail.com", "n2875c01").also {
                Timber.i("Email login result=$it Ok=${it == ZoomApiError.ZOOM_API_ERROR_SUCCESS}")
            } == ZoomApiError.ZOOM_API_ERROR_SUCCESS
        }
    }
}