package com.chydee.notekeeper.ui.bottomsheets


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.SecuritySheetLayoutBinding
import com.chydee.notekeeper.utils.Encrypto
import com.chydee.notekeeper.utils.takeText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.security_sheet_layout.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class SecurityBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: OnClickListener
    private lateinit var binding: SecuritySheetLayoutBinding


    companion object {
        fun instance(listener: OnClickListener) =
                SecurityBottomSheet()
                        .apply {
                            mListener = listener
                        }
    }

    interface OnClickListener {
        fun onEncryptionComplete(content: String)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = SecuritySheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.secretKeyField.doOnTextChanged { text, start, before, count ->
            while (count < 16) {
                secretKeyField.error = "SecretKey length is less than 16 characters"
            }

        }
        binding.continueBtn.setOnClickListener {
            if (isKeyStrong(binding.secretKeyField.takeText())) {
                //Start encrypting
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    mListener.onEncryptionComplete(Encrypto.aesEncrypt("Desmond", binding.secretKeyField.takeText()))
                }
                dismiss()
            } else {
                val error = """
                    Key length must be at least 13.
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

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{32,}$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(key)

        return key.length >= 32 && matcher.matches()
    }


}