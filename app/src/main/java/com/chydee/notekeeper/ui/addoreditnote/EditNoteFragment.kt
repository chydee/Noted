package com.chydee.notekeeper.ui.addoreditnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.chydee.notekeeper.ui.EditorBottomSheet
import com.chydee.notekeeper.utils.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar


class EditNoteFragment : Fragment(), EditorBottomSheet.EditorBottomSheetClickListener {

    private lateinit var binding: EditNoteFragmentBinding

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private var isEncrypted: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = EditNoteFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[EditNoteViewModel::class.java]
        binding.lifecycleOwner = this

        showNavigationIcon()

        handleClickListeners()
    }

    private fun handleClickListeners() {
        /*addNote()
        findNavController().popBackStack()*/

        binding.optionsBtn.setOnClickListener {
            EditorBottomSheet.instance(this).show(childFragmentManager, "Options")
        }
    }

    private fun addNote() {
        val note = Note(
                noteTitle = binding.noteTitle.text.toString(),
                noteDetail = binding.noteContent.text.toString(),
                lastEdit = binding.lastEdited.text.toString(),
                isEncrypted = isEncrypted
        )
        viewModel.insertNote(note)
    }

    private fun noteIsEmpty(): Boolean {
        if (binding.noteTitle.text.toString().isEmpty() and binding.noteContent.text.toString().isEmpty()) {
            return false
        }
        return true
    }

    private fun showNavigationIcon() {
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.setNavigationIcon(R.drawable.ic_up)
    }


    override fun onDeleteClick() {
    }

    override fun onCopyClick() {
    }

    override fun onSendClick() {
    }

    override fun onEncryptClicked() {
    }
}
