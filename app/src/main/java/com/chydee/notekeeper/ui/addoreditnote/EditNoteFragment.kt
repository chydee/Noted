package com.chydee.notekeeper.ui.addoreditnote

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.chydee.notekeeper.ui.EditorBottomSheet
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class EditNoteFragment : BaseFragment(), EditorBottomSheet.EditorBottomSheetClickListener {

    private lateinit var binding: EditNoteFragmentBinding

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private var isEncrypted: Boolean = false

    private val args: EditNoteFragmentArgs by navArgs()

    private var selectedColor: Int = 0

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
        setLastEditedTime()
        setDisplay()

        handleClickListeners()
    }

    private fun handleClickListeners() {
        binding.optionsBtn.setOnClickListener {
            EditorBottomSheet.instance(this).show(childFragmentManager, "Options")
        }
    }

    private fun addOrUpdate() {

        if (args.selectedNoteProperty != null) {
            val updateNote = Note(
                    noteId = args.selectedNoteProperty?.noteId!!,
                    noteTitle = binding.noteTitle.text.toString(),
                    noteDetail = binding.noteContent.text.toString(),
                    lastEdit = binding.lastEdited.text.toString(),
                    isEncrypted = isEncrypted,
                    color = selectedColor
            )
            viewModel.updateNote(updateNote)
        } else {
            val newNote = Note(
                    noteTitle = binding.noteTitle.text.toString(),
                    noteDetail = binding.noteContent.text.toString(),
                    lastEdit = binding.lastEdited.text.toString(),
                    isEncrypted = isEncrypted,
                    color = selectedColor
            )
            viewModel.insertNote(newNote)
        }
    }

    private fun setLastEditedTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(getString(R.string.date_time_string_format))
            binding.lastEdited.text = current.format(formatter)
        } else {
            val date = Date()
            val formatter = SimpleDateFormat(getString(R.string.date_time_string_format), Locale.ROOT)
            binding.lastEdited.text = getString(R.string.edited, formatter.format(date))
        }
    }

    private fun setDisplay() {
        if (args.selectedNoteProperty != null) {
            binding.noteTitle.setText(args.selectedNoteProperty?.noteTitle)
            binding.noteContent.setText(args.selectedNoteProperty?.noteDetail)
            binding.lastEdited.text = args.selectedNoteProperty?.lastEdit
        }
    }


    override fun onDeleteClick() {
        args.selectedNoteProperty?.let { viewModel.deleteNote(it) }
        findNavController().popBackStack()
    }

    override fun onCopyClick() {
    }

    override fun onSendClick() {
    }

    override fun onEncryptClicked() {
    }

    override fun onColorSelected(color: Color) {
        selectedColor = color.colorRes
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            addOrUpdate()
            findNavController().popBackStack()
            return true
        }
        return false
    }
}
