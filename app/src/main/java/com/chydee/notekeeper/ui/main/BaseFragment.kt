package com.chydee.notekeeper.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chydee.notekeeper.R
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFragmentBackPressed()
    }

    private fun onFragmentBackPressed() {
        val callback = object : OnBackPressedCallback(true
                /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                // Show your dialog and handle navigation
                // Toast.makeText(context, "Discard Note", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun showNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.setNavigationIcon(R.drawable.ic_up)
    }

    fun hideNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.navigationIcon = null
    }
}