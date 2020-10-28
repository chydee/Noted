package com.chydee.notekeeper.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.R
import com.chydee.notekeeper.ui.main.BaseFragment

class TrashFragment : BaseFragment() {


    private lateinit var viewModel: TrashViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.trash_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TrashViewModel::class.java)
        showNavigationIcon()
    }

}