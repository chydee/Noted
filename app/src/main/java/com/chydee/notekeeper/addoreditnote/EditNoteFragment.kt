package com.chydee.notekeeper.addoreditnote

import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback


class EditNoteFragment : Fragment() {

    private var sheetBehavior: BottomSheetBehavior<View>? = null
    private var options_bottom_sheet: CoordinatorLayout? = null

    private lateinit var viewModel: EditNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: EditNoteFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.edit_note_fragment, container, false)
        setHasOptionsMenu(true)
        options_bottom_sheet = binding.root.findViewById(R.id.options_bottom_sheet_layout)
        sheetBehavior = BottomSheetBehavior.from(options_bottom_sheet!!)
        binding.bottomSheet.setOnClickListener { v ->
            (
                    if (sheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        sheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                    )
        }

        // set callback for changes

        // set callback for changes
        sheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
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
