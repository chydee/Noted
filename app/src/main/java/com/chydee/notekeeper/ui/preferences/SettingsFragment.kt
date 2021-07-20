package com.chydee.notekeeper.ui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.chydee.notekeeper.R
import com.google.android.material.appbar.MaterialToolbar
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var appBar: MaterialToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val sharedPreferences = preferenceScreen.sharedPreferences
        val prefScreen: PreferenceScreen = preferenceScreen

        val count: Int = prefScreen.preferenceCount
        // Go through all of the preferences, and set up their preference summary.
        for (i in 0 until count) {
            val p: Preference = prefScreen.getPreference(i)
            // No need to set up preference summaries for checkbox preferences because
            // they are already set up in xml using summaryOff and summary On
            if (p !is CheckBoxPreference) {
                val value = sharedPreferences.getString(p.key, "")
                if (value != null) {
                    setPreferenceSummary(p, value)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val callback = object : OnBackPressedCallback(
            true
            /** when set to true it means that the callback is enabled */
        ) {
            override fun handleOnBackPressed() {
                // Show  dialog and handle navigation
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
    }

    private fun setupAppBar() {
        appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar?.setNavigationIcon(R.drawable.ic_up)
    }

    // sets the Preference Summary as per selected.
    private fun setPreferenceSummary(preference: Preference, value: String) {
        if (preference is ListPreference) {
            // For list preferences, figure out the label of the selected value
            val listPreference: ListPreference = preference
            val prefIndex: Int = listPreference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                // Set the summary to that label
                Timber.d(listPreference.entries[prefIndex] as String)
            }
        } else {
            // For an invalid Preference type, set the summary to null
            preference.summary = null
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Figure out which preference was changed
        val preference: Preference? = findPreference(key!!)

        if (preference != null) {
            // update the summary for the preference
            if (preference !is CheckBoxPreference) {
                val value: String? = sharedPreferences?.getString(preference.key, "")
                value?.let { setPreferenceSummary(preference, it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupAppBar()
    }

    override fun onStop() {
        super.onStop()
        appBar = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the preferenceScreen.sharedPreferences
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}
