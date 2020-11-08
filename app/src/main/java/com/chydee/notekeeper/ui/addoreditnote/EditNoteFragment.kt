package com.chydee.notekeeper.ui.addoreditnote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.chydee.notekeeper.ui.bottomsheets.EditorBottomSheet
import com.chydee.notekeeper.ui.bottomsheets.LockNoteBottomSheet
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.ViewModelFactory
import com.chydee.notekeeper.utils.takeText
import com.chydee.notekeeper.utils.toTrash
import kotlinx.android.synthetic.main.note_item.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class EditNoteFragment : BaseFragment(), EditorBottomSheet.EditorBottomSheetClickListener, LockNoteBottomSheet.OnClickListener {

    private lateinit var binding: EditNoteFragmentBinding

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private var isLocked: Boolean = false

    private val args: EditNoteFragmentArgs by navArgs()

    private var selectedColor: Int = -1

    private var password: String = ""

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
        onBackPressed()
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
                    noteTitle = binding.noteTitle.takeText(),
                    noteDetail = binding.noteContent.takeText(),
                    lastEdit = binding.lastEdited.text.toString(),
                    isLocked = isLocked,
                    color = selectedColor,
                    password = password
            )
            viewModel.updateNote(updateNote)
        } else {
            val newNote = Note(
                    noteTitle = binding.noteTitle.takeText(),
                    noteDetail = binding.noteContent.takeText(),
                    lastEdit = binding.lastEdited.text.toString(),
                    isLocked = isLocked,
                    color = selectedColor,
                    password = password
            )
            viewModel.insertNote(newNote)
        }
    }

    private fun setLastEditedTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(getString(R.string.date_time_string_format))
            binding.lastEdited.text = getString(R.string.edited, current.format(formatter))
        } else {
            val date = Date()
            val formatter = SimpleDateFormat(getString(R.string.date_time_string_format), Locale.ROOT)
            binding.lastEdited.text = getString(R.string.edited, formatter.format(date))
        }
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true
                /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                // Show your dialog and handle navigation
                // Toast.makeText(context, "Discard Note", Toast.LENGTH_SHORT).show()
                addOrUpdate()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setDisplay() {
        binding.noteTitle.imeOptions = EditorInfo.IME_ACTION_NEXT
        binding.noteTitle.setRawInputType(InputType.TYPE_CLASS_TEXT)
        if (args.selectedNoteProperty != null) {
            binding.noteTitle.setText(args.selectedNoteProperty?.noteTitle)
            binding.noteContent.setText(args.selectedNoteProperty?.noteDetail)
            binding.lastEdited.text = args.selectedNoteProperty?.lastEdit
        }

        binding.noteContent.movementMethod = LinkMovementMethod.getInstance()
        Linkify.addLinks(binding.noteContent, Linkify.ALL)

        binding.noteTitle.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.noteContent.requestFocus()
                    true
                }
                EditorInfo.IME_ACTION_SEND -> {
                    binding.noteContent.requestFocus()
                    true
                }
                EditorInfo.IME_ACTION_UNSPECIFIED -> {
                    binding.noteContent.requestFocus()
                    true
                }
                else -> false
            }
        }
    }

    private fun sendNote() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${noteTitle.text}\n ${noteContent.text}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, noteTitle.text.toString())
        startActivity(shareIntent)
    }


    override fun onDeleteClick() {
        args.selectedNoteProperty?.toTrash()?.let { viewModel.addToTrash(it) }
        args.selectedNoteProperty.let {
            if (it != null) {
                viewModel.deleteNote(it)
            }
        }
        findNavController().popBackStack()
    }

    override fun onCopyClick() {
        // if the user selects copy
        // Gets a handle to the clipboard service.
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Creates a new text clip to put on the clipboard
        val clip: ClipData = ClipData.newPlainText(noteTitle.text.toString(), "${noteTitle.text} \n ${noteContent.text}")
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip)
    }

    override fun onSendClick() {
        sendNote()
    }

    override fun onLockNoteClicked() {
        LockNoteBottomSheet.instance(this).show(childFragmentManager, "Lock Note")
    }

    override fun onColorSelected(color: Color) {
        selectedColor = color.colorRes
    }

    override fun onPasswordCreated(password: String) {
        isLocked = true
        this.password = password
        showSnackBar(getString(R.string.note_locked))
        addOrUpdate()
        findNavController().popBackStack()
    }

    override fun onPause() {
        super.onPause()
        addOrUpdate()
    }

}
