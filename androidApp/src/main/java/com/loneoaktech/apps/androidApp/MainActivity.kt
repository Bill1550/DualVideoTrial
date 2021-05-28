package com.loneoaktech.apps.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.loneoaktech.apps.shared.Greeting
import android.widget.TextView
import com.loneoaktech.apps.androidApp.databinding.ActivityMainBinding
import com.loneoaktech.utilities.ui.lazyViewBinding
import com.loneoaktech.utilities.ui.withViews

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
        }

//        val tv: TextView = findViewById(R.id.text_view)
//        tv.text = greet()
    }
}
