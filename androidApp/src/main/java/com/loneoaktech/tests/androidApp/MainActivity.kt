package com.loneoaktech.tests.androidApp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.loneoaktech.tests.shared.Greeting
import com.loneoaktech.tests.androidApp.databinding.ActivityMeetingBinding
import com.loneoaktech.tests.androidApp.ui.twilio.TwilioClientFragment
import com.loneoaktech.tests.androidApp.ui.zoomclient.ZoomClientFragment
import com.loneoaktech.utilities.ui.lazyViewBinding

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {

    private val holder = lazyViewBinding { ActivityMeetingBinding.inflate(layoutInflater) }

    @SuppressLint("MissingSuperCall") // Lint bug
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(holder.root)

        if ( savedInstanceState == null ) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.zoomContainer, ZoomClientFragment())
                replace(R.id.twilioContainer, TwilioClientFragment() )
            }.commit()
        }

        this.activityResultRegistry
    }
}
