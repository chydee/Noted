package com.chydee.notekeeper.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.UnlockSheetLayoutBinding
import com.chydee.notekeeper.utils.takeText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.unlock_sheet_layout.*

class UnlockNoteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: OnClickListener
    private lateinit var binding: UnlockSheetLayoutBinding

    private var note: Note? = null

    companion object {
        fun instance(listener: OnClickListener) =
            UnlockNoteBottomSheet()
                .apply {
                    mListener = listener
                }
    }

    interface OnClickListener {
        fun onNoteUnlocked(note: Note)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnlockSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        note = requireArguments().getParcelable("Note")
        binding.continueBtn.setOnClickListener {
            if (note != null) {
                if (binding.secretKeyField.takeText().contentEquals(note?.password!!)) {
                    val updatedNote = Note(
                        noteId = note!!.noteId,
                        noteTitle = note!!.noteTitle,
                        noteDetail = note!!.noteDetail,
                        lastEdit = note!!.lastEdit,
                        isLocked = false,
                        color = note!!.color,
                        password = note!!.password
                    )
                    mListener.onNoteUnlocked(updatedNote)
                    dismiss()
                } else {
                    val error = "Password does not match. Try again"
                    secretKeyField.error = error
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}
