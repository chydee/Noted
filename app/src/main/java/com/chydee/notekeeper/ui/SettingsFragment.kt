package com.chydee.notekeeper.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.chydee.notekeeper.R
import com.google.android.material.appbar.MaterialToolbar

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.setNavigationIcon(R.drawable.ic_up)

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
}
