package com.chydee.notekeeper.addoreditnote

import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class EditNoteFragment : Fragment() {

    private lateinit var binding: EditNoteFragmentBinding

    private var sheetBehavior: BottomSheetBehavior<View>? = null
    private var options_bottom_sheet: CoordinatorLayout? = null

    private lateinit var viewModel: EditNoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = EditNoteFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_note_menu, menu)
    }


}
