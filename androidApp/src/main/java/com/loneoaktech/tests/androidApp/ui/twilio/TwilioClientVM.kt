package com.loneoaktech.tests.androidApp.ui.twilio

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.loneoaktech.tests.androidApp.BuildConfig
import com.loneoaktech.utilities.extensions.summary
import com.twilio.jwt.accesstoken.AccessToken
import com.twilio.jwt.accesstoken.VideoGrant
import com.twilio.video.*
import timber.log.Timber
import tvi.webrtc.Camera2Enumerator

class TwilioClientVM(application: Application) : AndroidViewModel(application) {

//    var accessToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzBiNDEwMGMyMDIyZDZlOWZmYzc0ODNhODJhNmY1ZTM0LTE2MjIyMjA4NzkiLCJpc3MiOiJTSzBiNDEwMGMyMDIyZDZlOWZmYzc0ODNhODJhNmY1ZTM0Iiwic3ViIjoiQUNhM2NkM2MxMjI3ZTYyMTQ2MDdiYWY1ZDU2ZDc0MGEwOCIsImV4cCI6MTYyMjIyNDQ3OSwiZ3JhbnRzIjp7ImlkZW50aXR5IjoiYmlsbGhAbG9uZW9ha3RlY2guY29tIiwidmlkZW8iOnsicm9vbSI6IkxPVC1UZXN0In19fQ.tTddrptQ-qke2G1zPDKNgUFmMX-1uz_VwtB-3jLLSdU"    // TODO get

    fun connectToRoom( roomName: String, accessToken: String): Room {
        val options = ConnectOptions.Builder(accessToken).apply {
            roomName(roomName)
        }.build()

        return Video.connect( getApplication(), options, roomListener)
    }

    fun createToken(): String {
        return AccessToken.Builder(
            BuildConfig.twilioAccountSid,
            BuildConfig.twilioSID,
            BuildConfig.twilioSecret
        ).apply {
            this.ttl(3600)
            this.identity("billh@loneoaktech.com")
            this.grant( VideoGrant().apply { room = "LOT-test" })
        }.build().toJwt()
    }



    private fun findFrontCameraId(context: Context): String? {
        return Camera2Enumerator(context).run {
            deviceNames.firstOrNull { isFrontFacing(it) }
        }
    }

    private val roomListener = object: Room.Listener {
        override fun onConnected(room: Room) {
            Timber.i("Room Listener: onConnected, room=$room")
        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            Timber.i("Room Listener: onConnectFailure: room=$room error=${twilioException.summary()}")
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            Timber.i("Room Listener: ")
        }

        override fun onReconnected(room: Room) {
            Timber.i("Room Listener: ")
        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {
            Timber.i("Room Listener: ")
        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
            Timber.i("Room Listener: ")
        }

        override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
            Timber.i("Room Listener: ")
        }

        override fun onRecordingStarted(room: Room) {
            Timber.i("Room Listener: ")
        }

        override fun onRecordingStopped(room: Room) {
            Timber.i("Room Listener: ")
        }

    }



}