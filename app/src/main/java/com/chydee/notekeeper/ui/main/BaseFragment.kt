package com.chydee.notekeeper.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.chydee.notekeeper.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import java.io.File

abstract class BaseFragment : Fragment() {

    private var appbar: MaterialToolbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appbar = requireActivity().findViewById(R.id.topAppBar)
    }

    fun showNavigationIcon() {
        appbar?.setNavigationIcon(R.drawable.ic_up)
    }

    fun hideNavigationIcon() {
        if (appbar?.navigationIcon != null) appbar?.navigationIcon = null
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

    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
    }
}
