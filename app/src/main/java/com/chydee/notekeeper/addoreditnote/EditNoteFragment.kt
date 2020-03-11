package com.chydee.notekeeper.addoreditnote

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class EditNoteFragment : Fragment() {

    private val sheetBehavior: BottomSheetBehavior<*>? = null
    private val options_bottom_sheet: LinearLayout? = null

    private lateinit var viewModel: EditNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: EditNoteFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.edit_note_fragment, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_note_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditNoteViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
