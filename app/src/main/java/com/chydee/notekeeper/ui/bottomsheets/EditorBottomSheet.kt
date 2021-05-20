package com.chydee.notekeeper.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.databinding.BottomSheetLayoutBinding
import com.chydee.notekeeper.ui.ColorsAdapter
import com.chydee.notekeeper.utils.Items
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditorBottomSheet : BottomSheetDialogFragment() {
    private lateinit var mListener: EditorBottomSheetClickListener
    private var binding: BottomSheetLayoutBinding? = null

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
        fun onLockNoteClicked()
        fun onColorSelected(color: Color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLayoutBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadColors()
        handleBtnClicks()
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     *  Handle Button Clicks
     */
    private fun handleBtnClicks() {
        with(binding) {
            this?.deleteBtn?.setOnClickListener {
                mListener.onDeleteClick()
                dismiss()
            }
            this?.copyBtn?.setOnClickListener {
                mListener.onCopyClick()
                dismiss()
            }
            this?.sendBtn?.setOnClickListener {
                mListener.onSendClick()
                dismiss()
            }
            this?.lockeNoteBtn?.setOnClickListener {
                mListener.onLockNoteClicked()
                dismiss()
            }
        }
    }

    /**
     * Set up ColorsAdapter and load them to screen
     */
    private fun loadColors() {
        binding?.coloursRV?.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        colorsAdapter = ColorsAdapter()

        with(binding) {
            this?.coloursRV?.adapter = colorsAdapter
            colorsAdapter.submitList(Items.colors)
            this?.coloursRV?.setHasFixedSize(true)
            this?.coloursRV?.isNestedScrollingEnabled = false
        }

        colorsAdapter.setOnItemClickListener(object : ColorsAdapter.OnItemClickListener {
            override fun onColorSelected(color: Color) {
                mListener.onColorSelected(color)
            }
        })
    }
}
