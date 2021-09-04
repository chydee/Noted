package com.chydee.notekeeper.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.LockSheetLayoutBinding
import com.chydee.notekeeper.utils.ext.takeText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LockNoteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: OnClickListener
    private var binding: LockSheetLayoutBinding? = null

    companion object {
        fun instance(listener: OnClickListener) =
            LockNoteBottomSheet()
                .apply {
                    mListener = listener
                }
    }

    interface OnClickListener {
        fun onPasswordCreated(password: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LockSheetLayoutBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenToTextChange()
        setUpClickListeners()
    }

    private fun listenToTextChange() {
        binding?.secretKeyField?.doOnTextChanged { _, _, _, count ->
            when (count) {
                in 0..8 -> binding?.secretKeyField?.error = getString(R.string.least_keys)
                8 -> binding?.secretKeyField?.error = null
                else -> binding?.secretKeyField?.error = null
            }
        }
    }

    private fun setUpClickListeners() {
        binding?.continueBtn?.setOnClickListener {
            if (binding?.secretKeyField?.takeText().isNullOrEmpty()) {
                binding?.secretKeyField?.error = getString(R.string.least_keys)
            } else {
                mListener.onPasswordCreated(binding?.secretKeyField?.takeText() ?: "")
                dismiss()
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
