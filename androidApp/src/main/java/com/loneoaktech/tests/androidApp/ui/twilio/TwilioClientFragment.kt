package com.loneoaktech.tests.androidApp.ui.twilio

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.addRepeatingJob
import androidx.lifecycle.lifecycleScope
import com.loneoaktech.tests.androidApp.databinding.FragmentTwilioClientBinding
import com.loneoaktech.tests.androidApp.ui.BaseFragment
import com.loneoaktech.utilities.extensions.summary
import com.loneoaktech.utilities.permissions.createPermissionManager
import com.loneoaktech.utilities.ui.lazyViewBinding
import com.twilio.video.Room
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class TwilioClientFragment : BaseFragment() {

    private val holder = lazyViewBinding { FragmentTwilioClientBinding.inflate(layoutInflater) }
    private val model: TwilioClientVM by viewModels()

    private val permissionManager = createPermissionManager(listOf(Manifest.permission.CAMERA), lifecycleScope)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return holder.root
    }

    private var room: Room? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            room = model.connectToRoom("LOT-Test", model.createToken())
        } catch (t: Throwable) {
            Timber.e(t, "Error when connecting to room: ${t.summary()}")
        }

        createVideoJob()
        model.init()
    }

    private fun createVideoJob() {

        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.RESUMED){
            try {
                if (!permissionManager.checkAndRequestPermission())
                    return@addRepeatingJob

                model.localVideoTrack.collect { localTrack ->
                    localTrack?.addSink( holder.binding.localVideoView )
                }
            } catch ( ce: CancellationException ) {
                Timber.i("Video job cancelled")
                model.localVideoTrack.value?.removeSink( holder.binding.localVideoView )
            } catch (t: Throwable ){
                Timber.e("Video job - unexpected exception: ${t.summary()}")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        room?.disconnect()
    }
}