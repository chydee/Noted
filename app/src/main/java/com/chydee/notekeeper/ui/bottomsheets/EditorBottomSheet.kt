package com.chydee.notekeeper.ui.bottomsheets


import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.databinding.BottomSheetLayoutBinding
import com.chydee.notekeeper.ui.ColorsAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditorBottomSheet : BottomSheetDialogFragment() {

    private var colors = arrayListOf(
            Color("Blue", BLUE),
            Color("Yellow", YELLOW),
            Color("Green", GREEN),
            Color("Red", RED),
            Color("Magenta", MAGENTA)
    )

    private lateinit var mListener: EditorBottomSheetClickListener
    private lateinit var binding: BottomSheetLayoutBinding

    private lateinit var colorsAdapter: ColorsAdapter

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
        fun onColorSelected(color: Color)
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
        //Before anything check to see the Device's API version
        // if the version code is greater than 26 then allow encryption if not hide the encryption button
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            binding.encryptionBtn.visibility = View.GONE
        }
        loadColors()
        handleBtnClicks()
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun handleBtnClicks() {
        with(binding) {
            deleteBtn.setOnClickListener {
                mListener.onDeleteClick()
                dismiss()
            }
            copyBtn.setOnClickListener {
                mListener.onCopyClick()
                dismiss()
            }
            sendBtn.setOnClickListener {
                mListener.onSendClick()
                dismiss()
            }
            encryptionBtn.setOnClickListener {
                mListener.onEncryptClicked()
                dismiss()
            }
        }
    }

    private fun loadColors() {
        val layoutManager = FlexboxLayoutManager(requireContext())

        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.coloursRV.layoutManager = layoutManager

        colorsAdapter = ColorsAdapter()

        with(binding) {
            coloursRV.adapter = colorsAdapter
            colorsAdapter.submitList(colors)
            coloursRV.setHasFixedSize(true)
            coloursRV.isNestedScrollingEnabled = false
        }

        colorsAdapter.setOnItemClickListener(object : ColorsAdapter.OnItemClickListener {
            override fun onColorSelected(color: Color) {
                mListener.onColorSelected(color)
            }
        })

    }

}