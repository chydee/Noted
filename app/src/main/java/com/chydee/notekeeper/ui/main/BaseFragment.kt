package com.chydee.notekeeper.ui.main

import androidx.fragment.app.Fragment
import com.chydee.notekeeper.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {


    fun showNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.setNavigationIcon(R.drawable.ic_up)
    }

    fun hideNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.navigationIcon = null
    }

    fun showSnackBar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    fun snackBarWithAction(message: String, actionName: String, action: () -> Unit) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                    .setAction(actionName) {
                        action()
                    }
                    .show()
        }

    }
}