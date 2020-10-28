package com.chydee.notekeeper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chydee.notekeeper.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditorBottomSheet : BottomSheetDialogFragment() {
    lateinit var mListener: EditorBottomSheetClickListener
    lateinit var binding: BottomSheetLayoutBinding

    companion object {
        fun instance(listener: EditorBottomSheetClickListener) =
                EditorBottomSheet()
                        .apply {
                            mListener = listener
                        }
    }

    interface EditorBottomSheetClickListener {
        fun onDeleteClick()
        fun onCopyClick()
        fun onSendClick()
        fun onEncryptClicked()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            deleteBtn.setOnClickListener {
                mListener.onDeleteClick()
            }
            copyBtn.setOnClickListener {
                mListener.onCopyClick()
            }
            sendBtn.setOnClickListener {
                mListener.onSendClick()
            }
            encryptionBtn.setOnClickListener {
                mListener.onEncryptClicked()
            }
        }
    }

}