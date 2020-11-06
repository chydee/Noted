package com.chydee.notekeeper.ui.voice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment

class VoiceNotesFragment : BaseFragment(), RecordNoteBottomSheet.OnClickListener {

    companion object {
        fun newInstance() = VoiceNotesFragment()
    }

    private lateinit var viewModel: VoiceNotesViewModel

    private lateinit var binding: VoiceNotesFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = VoiceNotesFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VoiceNotesViewModel::class.java)
        binding.recordNewVoiceNote.setOnClickListener {
            RecordNoteBottomSheet.instanceOfThis(this).show(childFragmentManager, "RecordNotes")
        }
    }

    override fun onStartRecording() {
        //Start Recording Note
    }

    override fun onPauseRecording() {
        //Pause Recording  and Release Media
    }

    override fun onStopRecording() {
        //Stop Recording and Release Media
    }

}