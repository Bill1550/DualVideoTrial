package com.loneoaktech.tests.androidApp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.loneoaktech.tests.shared.Greeting
import com.loneoaktech.tests.androidApp.databinding.ActivityMainBinding
import com.loneoaktech.tests.androidApp.databinding.ActivityMeetingBinding
import com.loneoaktech.tests.androidApp.ui.TwilioClientFragment
import com.loneoaktech.tests.androidApp.ui.ZoomClientFragment
import com.loneoaktech.utilities.ui.lazyViewBinding
import com.loneoaktech.utilities.ui.withViews
import kotlinx.coroutines.delay

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {

    private val holder = lazyViewBinding { ActivityMeetingBinding.inflate(layoutInflater) }

    @SuppressLint("MissingSuperCall") // Lint bug
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(holder.root)

//        holder.withViews {
//            textView.text = greet()
//
//            lifecycleScope.launchWhenResumed {
//                delay(4000)
//                textView.text = "Now to work!"
//            }
//        }

        if ( savedInstanceState == null ) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.zoomContainer, ZoomClientFragment())
                replace(R.id.twilioContainer, TwilioClientFragment() )
            }.commit()
        }


    }
}
