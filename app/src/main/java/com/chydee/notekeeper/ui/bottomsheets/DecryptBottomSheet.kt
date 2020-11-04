package com.chydee.notekeeper.ui.bottomsheets


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.DecryptSheetLayoutBinding
import com.chydee.notekeeper.utils.Encrypto
import com.chydee.notekeeper.utils.takeText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.decrypt_sheet_layout.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class DecryptBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: OnClickListener
    private lateinit var binding: DecryptSheetLayoutBinding

    private lateinit var encrypto: Encrypto

    private lateinit var note: Note

    companion object {
        fun instance(listener: OnClickListener) =
                DecryptBottomSheet()
                        .apply {
                            mListener = listener
                        }
    }

    interface OnClickListener {
        fun onNoteDecrypted(note: Note)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DecryptSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        encrypto = Encrypto()
        note = requireArguments().getParcelable("Note")!!
        binding.secretKeyField.doOnTextChanged { _, _, _, count ->
            when (count) {
                in 0..8 -> secretKeyField.error = "Not a typical secret key"
                8 -> secretKeyField.error = null
                else -> secretKeyField.error = null
            }

        }
        binding.continueBtn.setOnClickListener {
            if (isKeyStrong(binding.secretKeyField.takeText())) {
                val decryptedString = encrypto.decrypt(binding.secretKeyField.takeText(), note.noteDetail)
                val title = decryptedString?.split("\n")?.get(0).toString()
                val content = decryptedString?.split("\n")?.get(1).toString()
                val updatedNote = Note(
                        noteId = note.noteId,
                        noteTitle = title,
                        noteDetail = content,
                        lastEdit = note.lastEdit,
                        isEncrypted = false,
                        color = note.color
                )
                mListener.onNoteDecrypted(updatedNote)
                dismiss()
            } else {
                val error = "Invalid Secret Key"
                secretKeyField.error = error
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun isKeyStrong(key: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(key)

        return key.length >= 8 && matcher.matches()
    }


}