package com.chydee.notekeeper.ui.voice

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber
import java.io.File
import java.io.IOException

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

private const val REQUEST_ACCESS_FILES_PERMISSION = 300

class VoiceNotesFragment : BaseFragment(), RecordNoteBottomSheet.OnClickListener {

    private lateinit var viewModel: VoiceNotesViewModel
    private lateinit var binding: VoiceNotesFragmentBinding
    private lateinit var dialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialogView: View
    private lateinit var noteTitleField: TextInputEditText

    private var fileName: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var recordAudioPermission: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var accessFilesPermission: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var adapter: VoiceNotesAdapter

    private lateinit var deleteList: ArrayList<File>
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Record to the external cache directory for visibility
        fileName = "${getOutputDirectory(requireContext())}/${System.currentTimeMillis()}.mp3"
        ActivityCompat.requestPermissions(requireActivity(), accessFilesPermission, REQUEST_ACCESS_FILES_PERMISSION)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = VoiceNotesFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(VoiceNotesViewModel::class.java)
        binding.recordNewVoiceNote.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                RecordNoteBottomSheet.instanceOfThis(this).show(childFragmentManager, "RecordNotes")
            } else {
                ActivityCompat.requestPermissions(requireActivity(), recordAudioPermission, REQUEST_RECORD_AUDIO_PERMISSION)
            }
        }
        retrieveVoiceNotes()
        setupRV()
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }


    private fun retrieveVoiceNotes() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val dir = getOutputDirectory(requireContext())
            if (dir.exists()) {
                val files = dir.listFiles()
                files?.forEach {
                    Log.d("Voice Note: ", it.name)
                }
            } else {
                Timber.e("Folder no exist")
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), accessFilesPermission, REQUEST_ACCESS_FILES_PERMISSION)
        }

    }

    /**
     *  Prepare and start recording
     */
    private fun startRecording() {

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("VoiceNoteFragment", "prepare() failed")
            }
            start()
            state = true
        }
    }

    /**
     *  Stop Recording and release recorder
     */
    private fun stopRecording() {
        if (state) {
            mediaRecorder?.apply {
                stop()
                release()
            }
            state = false
        } else {
            Timber.d("You are not recording right now!")
        }
        mediaRecorder = null
    }

    /**
     *  Pause Recording
     */
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
    /**
     *  Resume Recording after pause
     */
    private fun resumeRecording() {
        Timber.d("Resume!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
        } else {
            startRecording()
        }
        recordingStopped = false
    }

    /**
     *  show an MaterialAlertDialog and prompt to enter file name before
     *  exiting process completely
     */
    private fun launchDialog() {
        noteTitleField = dialogView.findViewById(R.id.noteTitleTextInputEditText)
        dialogBuilder.setView(dialogView)
                .setTitle(getString(R.string.save_note))
                .setMessage(getString(R.string.save_note_hint))
                .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                    //stopRecording("${noteTitleField.takeText()}.mp3")
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    showSnackBar(getString(R.string.recording_cancelled))
                    dialog.dismiss()
                }
                .show()
    }

    private fun setupRV() {
        adapter = VoiceNotesAdapter()
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.vnRecyclerView.layoutManager = manager
        binding.vnRecyclerView.adapter = adapter
        val dir = getOutputDirectory(requireContext())
        if (dir.exists()) {
            val files = dir.listFiles()
            val vns = ArrayList<File>()
            files?.forEach { vns.add(it) }
            adapter.setNotes(vns)
            adapter.notifyDataSetChanged()
        } else {
            binding.emptyNotesState.visibility = View.VISIBLE
        }


        /*tracker = SelectionTracker.Builder(
                "mySelection",
                binding.vnRecyclerView,
                StableIdKeyProvider(binding.vnRecyclerView),
                MyLookup(binding.vnRecyclerView, 2),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker*/
        //observeTracker()


        adapter.setOnClickListener(object : VoiceNotesAdapter.OnItemClickListener {

            override fun onFileClicked(file: File) {
            }

            override fun onPlayPauseClicked() {
            }

            override fun onStopPlaying() {
            }

            override fun onSkipForward() {
            }

            override fun onSkipBackward() {
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ACCESS_FILES_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            retrieveVoiceNotes()
        } else {
            //TODO: Show reason for need of permission and ask again
            Snackbar.make(binding.root, "You need to give permission", Snackbar.LENGTH_LONG).show()
        }

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.d("REQUEST_RECORD_AUDIO_PERMISSION: $REQUEST_RECORD_AUDIO_PERMISSION is GRANTED")
        }
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

    override fun onStop() {
        super.onStop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

}