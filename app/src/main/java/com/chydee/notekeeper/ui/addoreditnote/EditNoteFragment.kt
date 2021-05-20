package com.chydee.notekeeper.ui.addoreditnote

import android.content.*
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.EditNoteFragmentBinding
import com.chydee.notekeeper.ui.bottomsheets.EditorBottomSheet
import com.chydee.notekeeper.ui.bottomsheets.LockNoteBottomSheet
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.ext.takeText
import com.chydee.notekeeper.utils.toTrash
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class EditNoteFragment : BaseFragment(), EditorBottomSheet.EditorBottomSheetClickListener, LockNoteBottomSheet.OnClickListener {

    private var binding: EditNoteFragmentBinding? = null

    private val viewModel: EditNoteViewModel by viewModels()

    private var isLocked: Boolean = false

    private val args: EditNoteFragmentArgs by navArgs()

    private var selectedColor: Int = -1

    private var password: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditNoteFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.lifecycleOwner = this
        showNavigationIcon()
        setLastEditedTime()
        setUpDisplay()
        setUpClickListeners()
        onBackPressed()
    }

    private fun setUpClickListeners() {
        binding?.optionsBtn?.setOnClickListener {
            EditorBottomSheet.instance(this).show(childFragmentManager, "Options")
        }
    }

    /**
     *  first checks if the NavArgs is not null and the note id is not -1
     *  if true: update note
     *  if false create new note and add to the Note Table
     */
    private fun addOrUpdateNote() {

        if (args.selectedNoteProperty != null && args.selectedNoteProperty?.noteId != -1) {
            val updateNote = Note(
                noteId = args.selectedNoteProperty?.noteId ?: -1,
                noteTitle = binding?.noteTitle?.takeText() ?: "",
                noteDetail = binding?.noteContent?.takeText() ?: "",
                lastEdit = binding?.lastEdited?.text.toString(),
                isLocked = isLocked,
                color = selectedColor,
                password = password
            )
            viewModel.updateNote(updateNote)
        } else {
            val newNote = Note(
                noteTitle = binding?.noteTitle?.takeText() ?: "",
                noteDetail = binding?.noteContent?.takeText() ?: "",
                lastEdit = binding?.lastEdited?.text.toString(),
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
            binding?.lastEdited?.text = getString(R.string.edited, current.format(formatter))
        } else {
            val date = Date()
            val formatter = SimpleDateFormat(getString(R.string.date_time_string_format), Locale.ROOT)
            binding?.lastEdited?.text = getString(R.string.edited, formatter.format(date))
        }
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(
            true
            /** true means that the callback is enabled */
        ) {
            override fun handleOnBackPressed() {
                // update note and handle navigation
                addOrUpdateNote()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setUpDisplay() {
        binding?.noteTitle?.imeOptions = EditorInfo.IME_ACTION_NEXT
        binding?.noteTitle?.setRawInputType(InputType.TYPE_CLASS_TEXT)
        if (args.selectedNoteProperty != null) {
            binding?.noteTitle?.setText(args.selectedNoteProperty?.noteTitle)
            binding?.noteContent?.setText(args.selectedNoteProperty?.noteDetail)
            binding?.lastEdited?.text = args.selectedNoteProperty?.lastEdit
        }

        binding?.noteContent?.movementMethod = LinkMovementMethod.getInstance()
        binding?.noteContent?.let { Linkify.addLinks(it, Linkify.ALL) }

        binding?.noteTitle?.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding?.noteContent?.requestFocus()
                    true
                }
                EditorInfo.IME_ACTION_SEND -> {
                    binding?.noteContent?.requestFocus()
                    true
                }
                EditorInfo.IME_ACTION_UNSPECIFIED -> {
                    binding?.noteContent?.requestFocus()
                    true
                }
                else -> false
            }
        }
    }

    /**
     *  Share note with other apps
     */
    private fun sendNote() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${binding?.noteTitle?.takeText()}\n ${binding?.noteContent?.takeText()}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, binding?.noteTitle?.takeText())
        // Try to invoke the intent.
        try {
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Activity Not Found", Toast.LENGTH_SHORT).show()
        }
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
        val clip: ClipData =
            ClipData.newPlainText(binding?.noteTitle?.takeText(), "${binding?.noteTitle?.takeText()} \n ${binding?.noteContent?.takeText()}")
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
        addOrUpdateNote()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
