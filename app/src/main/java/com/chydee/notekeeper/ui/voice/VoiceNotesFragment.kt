package com.chydee.notekeeper.ui.voice

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VoiceNotesFragment : BaseFragment(), RecordNoteBottomSheet.OnClickListener {

    private lateinit var viewModel: VoiceNotesViewModel

    private lateinit var binding: VoiceNotesFragmentBinding

    private var output: String? = null
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
        requestPermission()
        binding.recordNewVoiceNote.setOnClickListener {
            RecordNoteBottomSheet.instanceOfThis(this).show(childFragmentManager, "RecordNotes")
        }
        output = "${getOutputDirectory(requireContext())}/${
            SimpleDateFormat(
                    getString(R.string.date_time_string_format), Locale.ROOT
            ).format(System.currentTimeMillis())
        }.mp3"
        mediaRecorder = MediaRecorder()
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        }
    }


    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(context, "Recording started!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if (state) {
            if (!recordingStopped) {
                Toast.makeText(context, "Stopped!", Toast.LENGTH_SHORT).show()
                mediaRecorder?.pause()
                recordingStopped = true
            } else {
                resumeRecording()
            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(context, "Resume!", Toast.LENGTH_SHORT).show()
        mediaRecorder?.resume()
        recordingStopped = false
    }

    fun getAppSpecificAudios(context: Context, albumName: String): File? {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(context.getExternalFilesDir(
                Environment.DIRECTORY_MUSIC), albumName)
        if (!file.mkdirs()) {
            Log.e("VoiceNotesFragment", "Directory not created")
        }
        return file
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
        stopRecording()
    }

}