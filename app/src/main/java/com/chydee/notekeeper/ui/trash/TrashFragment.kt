package com.chydee.notekeeper.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.databinding.TrashFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.ViewModelFactory

class TrashFragment : BaseFragment() {


    private lateinit var viewModel: TrashViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: TrashFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = TrashFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(TrashViewModel::class.java)
        binding.lifecycleOwner = this
        showNavigationIcon()
    }

}