package com.chydee.notekeeper.ui.addoreditnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.chydee.notekeeper.utils.ViewModelFactory


class EditNoteFragment : Fragment() {

    private lateinit var binding: EditNoteFragmentBinding

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = EditNoteFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = ViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this)[EditNoteViewModel::class.java]
        binding.lifecycleOwner = this
    }


}
