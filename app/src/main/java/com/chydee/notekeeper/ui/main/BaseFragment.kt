package com.chydee.notekeeper.ui.main

import androidx.fragment.app.Fragment
import com.chydee.notekeeper.R
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseFragment : Fragment() {



    fun showNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.setNavigationIcon(R.drawable.ic_up)
    }

    fun hideNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.navigationIcon = null
    }
}