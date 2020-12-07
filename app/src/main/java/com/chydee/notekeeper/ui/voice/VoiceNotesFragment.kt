package com.chydee.notekeeper.ui.voice

import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.takeText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber
import java.io.IOException

class VoiceNotesFragment : BaseFragment(), RecordNoteBottomSheet.OnClickListener {

    private lateinit var viewModel: VoiceNotesViewModel
    private lateinit var binding: VoiceNotesFragmentBinding
    private lateinit var dialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialogView: View
    private lateinit var noteTitleField: TextInputEditText

    private var output: String? = null
    private var noteTitle: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = VoiceNotesFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(VoiceNotesViewModel::class.java)
        binding.recordNewVoiceNote.setOnClickListener {
            RecordNoteBottomSheet.instanceOfThis(this).show(childFragmentManager, "RecordNotes")
        }
        noteTitle = getString(R.string.new_voice_note)
        output = "${getOutputDirectory(requireContext())}/$noteTitleField.mp3"
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        mediaRecorder = MediaRecorder()
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
        }
        val folder = getOutputDirectory(requireContext())
        if (folder.exists()) {
            val files = folder.listFiles()
            files.forEach {
                Timber.tag("Voice Note: ").d(it.name)
            }
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Timber.d("Recording started!")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
        } else {
            Timber.d("You are not recording right now!")
        }
    }


    private fun pauseRecording() {
        if (state) {
            if (!recordingStopped) {
                Timber.d("Stopped!")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mediaRecorder?.pause()
                } else {
                    stopRecording()
                }
                recordingStopped = true
            } else {
                resumeRecording()
            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    private fun resumeRecording() {
        Timber.d("Resume!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
        } else {
            startRecording()
        }
        recordingStopped = false
    }

    private fun launchDialog() {
        noteTitleField = dialogView.findViewById(R.id.noteTitle)
        dialogBuilder.setView(dialogView)
                .setTitle(getString(R.string.save_note))
                .setMessage(getString(R.string.save_note_hint))
                .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                    noteTitle = noteTitleField.takeText()
                    stopRecording()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    showSnackBar(getString(R.string.recording_cancelled))
                    dialog.dismiss()
                }
                .show()
    }

    override fun onStartRecording() {
        //Start Recording Note
        startRecording()
    }

    override fun onPauseRecording() {
        //Pause Recording  and Release Media
        pauseRecording()
    }

    override fun onStopRecording() {
        //Stop Recording and Release Media
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_note_name, null, false)
        pauseRecording()
        launchDialog()
    }

}