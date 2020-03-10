package com.chydee.notekeeper.addoreditnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding


class EditNoteFragment : Fragment() {

    companion object {
        fun newInstance() = EditNoteFragment()
    }

    private lateinit var viewModel: EditNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: EditNoteFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.edit_note_fragment, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditNoteViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
