package com.loneoaktech.tests.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.loneoaktech.tests.shared.Greeting
import com.loneoaktech.tests.androidApp.databinding.ActivityMainBinding
import com.loneoaktech.utilities.ui.lazyViewBinding
import com.loneoaktech.utilities.ui.withViews
import kotlinx.coroutines.delay

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {

    private val holder = lazyViewBinding { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(holder.root)

        holder.withViews {
            textView.text = greet()

            lifecycleScope.launchWhenResumed {
                delay(4000)
                textView.text = "Now to work!"
            }
        }


    }
}
