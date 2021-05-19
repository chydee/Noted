package com.chydee.notekeeper.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.LockSheetLayoutBinding
import com.chydee.notekeeper.utils.takeText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.lock_sheet_layout.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LockNoteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: OnClickListener
    private lateinit var binding: LockSheetLayoutBinding

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.secretKeyField.doOnTextChanged { _, _, _, count ->
            when (count) {
                in 0..8 -> secretKeyField.error = "Your SecretKey must be at least 8 characters"
                8 -> secretKeyField.error = null
                else -> secretKeyField.error = null
            }
        }
        binding.continueBtn.setOnClickListener {
            if (isKeyStrong(binding.secretKeyField.takeText())) {
                mListener.onPasswordCreated(binding.secretKeyField.takeText())
                dismiss()
            } else {
                val error = """
                    Key length must be at least 8.
                    It must contain at least one digit.
                    It must contains at least one lowercase English character.
                    It must contains at least one uppercase English character.
                    It must contains at least one special character. The special characters are: !@#${'$'}%^&*()-+
                """.trimIndent()
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
