package com.chydee.notekeeper.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.UnlockSheetLayoutBinding
import com.chydee.notekeeper.utils.ext.takeText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UnlockNoteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: OnClickListener
    private var binding: UnlockSheetLayoutBinding? = null

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
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        note = requireArguments().getParcelable("Note")
        binding?.continueBtn?.setOnClickListener {
            if (note != null) {
                if (binding?.secretKeyField?.takeText()?.contentEquals(note?.password!!) == true) {
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
                    binding?.secretKeyField?.error = error
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
