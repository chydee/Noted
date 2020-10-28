package com.chydee.notekeeper.ui.addoreditnote

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.chydee.notekeeper.ui.EditorBottomSheet
import com.chydee.notekeeper.utils.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class EditNoteFragment : Fragment(), EditorBottomSheet.EditorBottomSheetClickListener {

    private lateinit var binding: EditNoteFragmentBinding

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private var isEncrypted: Boolean = false

    private val args: EditNoteFragmentArgs by navArgs()

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
        val callback = object : OnBackPressedCallback(true
                /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                // Show your dialog and handle navigation
                // Toast.makeText(context, "Discard Note", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun handleClickListeners() {
        binding.optionsBtn.setOnClickListener {
            EditorBottomSheet.instance(this).show(childFragmentManager, "Options")
        }
    }

    fun addOrUpdate() {

        if (args.selectedNoteProperty != null) {
            val note = Note(
                    noteId = args.selectedNoteProperty?.noteId!!,
                    noteTitle = binding.noteTitle.text.toString(),
                    noteDetail = binding.noteContent.text.toString(),
                    lastEdit = binding.lastEdited.text.toString(),
                    isEncrypted = isEncrypted
            )
            viewModel.updateNote(note)
        } else {
            val note = Note(
                    noteTitle = binding.noteTitle.text.toString(),
                    noteDetail = binding.noteContent.text.toString(),
                    lastEdit = binding.lastEdited.text.toString(),
                    isEncrypted = isEncrypted
            )
            viewModel.insertNote(note)
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
            binding.lastEdited.text = formatter.format(date)
        }
    }

    private fun setDisplay() {
        if (args.selectedNoteProperty != null) {
            binding.noteTitle.setText(args.selectedNoteProperty?.noteTitle)
            binding.noteContent.setText(args.selectedNoteProperty?.noteDetail)
            binding.lastEdited.text = args.selectedNoteProperty?.lastEdit
        }
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
        args.selectedNoteProperty?.let { viewModel.deleteNote(it) }
        findNavController().popBackStack()

    }

    override fun onCopyClick() {
    }

    override fun onSendClick() {
    }

    override fun onEncryptClicked() {
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
