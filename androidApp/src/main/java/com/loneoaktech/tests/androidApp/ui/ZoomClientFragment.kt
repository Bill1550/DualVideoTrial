package com.loneoaktech.tests.androidApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loneoaktech.tests.androidApp.databinding.FragmentZoomClientBinding
import com.loneoaktech.utilities.ui.lazyViewBinding

class ZoomClientFragment : BaseFragment() {

    private val holder = lazyViewBinding { FragmentZoomClientBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return holder.root
    }
}