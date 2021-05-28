package com.loneoaktech.tests.androidApp.ui.twilio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.loneoaktech.tests.androidApp.databinding.FragmentTwilioClientBinding
import com.loneoaktech.tests.androidApp.ui.BaseFragment
import com.loneoaktech.utilities.extensions.summary
import com.loneoaktech.utilities.ui.lazyViewBinding
import com.twilio.video.Room
import timber.log.Timber

class TwilioClientFragment : BaseFragment() {

    private val holder = lazyViewBinding { FragmentTwilioClientBinding.inflate(layoutInflater) }
    private val model: TwilioClientVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return holder.root
    }

    private var room: Room? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            room = model.connectToRoom("LOT-Test")
        } catch (t: Throwable) {
            Timber.e(t, "Error when connecting to room: ${t.summary()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        room?.disconnect()
    }
}