package com.chydee.notekeeper.ui


import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.databinding.BottomSheetLayoutBinding
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
        loadColors()
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