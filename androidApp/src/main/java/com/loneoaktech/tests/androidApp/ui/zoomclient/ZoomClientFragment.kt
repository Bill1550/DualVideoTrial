package com.loneoaktech.tests.androidApp.ui.zoomclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.loneoaktech.tests.androidApp.databinding.FragmentZoomClientBinding
import com.loneoaktech.tests.androidApp.ui.BaseFragment
import com.loneoaktech.utilities.ui.lazyViewBinding

class ZoomClientFragment : BaseFragment() {

    private val model: ZoomClientVM by viewModels()

    private val holder = lazyViewBinding { FragmentZoomClientBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return holder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.init()
    }

    
}