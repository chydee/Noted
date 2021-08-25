package com.chydee.notekeeper.ui.voice

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.ext.hide
import com.chydee.notekeeper.utils.ext.isContainsSpecialCharacter
import com.chydee.notekeeper.utils.ext.show
import com.chydee.notekeeper.utils.ext.takeText
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class VoiceNotesFragment : BaseFragment(), RecordNoteBottomSheet.OnClickListener {


    private val viewModel: VoiceNotesViewModel by viewModels()
    private var binding: VoiceNotesFragmentBinding? = null
    private lateinit var dialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialogView: View
    private lateinit var noteTitleField: TextInputEditText

    private var fileName: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    private var playingStopped: Boolean = false

    private var player: MediaPlayer? = null

    private var length: Int = 0

    private lateinit var adapter: VoiceNotesAdapter

    private lateinit var renameDialog: Dialog
    private lateinit var renameDialogView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Record to the external cache directory for visibility
        fileName = "${getOutputDirectory(requireContext())}/New Voice Note ${Date().time}.mp3"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VoiceNotesFragmentBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = VoiceNotesAdapter()
        hideNavigationIcon()
        setUpOnClickListeners()
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        initDialogs()
        fetchAndDisplayVoiceNotes()
        player?.setOnCompletionListener {
            MediaPlayer.OnCompletionListener { stopPlaying() }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAndDisplayVoiceNotes()
    }

    private fun setUpOnClickListeners() {
        binding?.recordNewVoiceNote?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                launchRecordNoteSheet()
            }
        }
    }

    private fun launchRecordNoteSheet() {
        RecordNoteBottomSheet.instanceOfThis(this).show(childFragmentManager, "RecordNotes")
    }


    private fun playVoiceNote(fileName: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                setWakeMode(requireContext(), PowerManager.PARTIAL_WAKE_LOCK)
                prepare()
                start()
            } catch (e: IOException) {
                Timber.e("prepare() failed")
            }
        }
    }

    private fun pausePlaying() {
        if (player != null && player?.isPlaying == true) {
            player?.pause()
            length = player?.currentPosition ?: 0
        }
    }

    private fun resumePlaying() {
        Timber.d("Resume!")
        if (player?.isPlaying == false) {
            player?.start()
        } else {
            player?.apply {
                seekTo(length)
                start()
            }
        }
    }

    private fun stopPlaying() {
        if (player != null && player?.isPlaying == true) {
            player?.stop()
            player?.reset()
            player?.release()
            player = null
        } else {
            Timber.d("You are not playing any voice note right now!")
        }
    }

    /**
     *
     *  Fetch And Display Voice Notes
     */
    private fun fetchAndDisplayVoiceNotes() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchAudioFiles(getOutputDirectory(requireContext()))
            loadVoiceNotes()
        }
    }

    /**
     *  Observe voiceNotes and load to RecyclerView
     */
    private fun loadVoiceNotes() {
        viewModel.voiceNotes.observe(
            viewLifecycleOwner,
            {
                if (it != null && it.isNotEmpty()) {
                    setupRecyclerView(it as ArrayList<File>)
                } else {
                    showEmptyState()
                }
            }
        )
    }


    /**
     *  Set up the RecyclerView and set adapter
     */
    private fun setupRecyclerView(audios: ArrayList<File>) {
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding?.vnRecyclerView?.layoutManager = manager
        binding?.vnRecyclerView?.adapter = adapter
        adapter.submitList(audios)
        adapter.setOnClickListener(object : VoiceNotesAdapter.OnItemClickListener {
            override fun onFileClicked(file: File) {
            }

            override fun onPlay(file: File) {
                playVoiceNote(file.absolutePath)
            }

            override fun onPause() {
                pausePlaying()
            }

            override fun onResume() {
                resumePlaying()
            }

            override fun onStop() {
                stopPlaying()
            }

            override fun onSkipForward() {
                if (player != null && player?.isPlaying == true) {
                    if (player?.currentPosition ?: 0 <= player?.duration ?: 0) {
                        player?.apply {
                            seekTo(10000)
                        }
                    }
                }
            }

            override fun onSkipBackward() {
                if (player != null && player?.isPlaying == true) {
                    if (player?.currentPosition ?: 0 <= player?.duration ?: 0) {
                        player?.apply {
                            seekTo(-10000)
                        }
                    }
                }
            }

            override fun onRenameClicked(file: File?) {
                showSnackBar("Coming soon!")
                //renameVoiceNoteFile(file)
                /*viewModel.isFiledRenamed.observe(viewLifecycleOwner, {
                    when (it) {
                        true -> adapter.notifyDataSetChanged()
                        false -> showSnackBar("Error: unable to rename voice note")
                    }
                })*/
            }

            override fun onDeleteClicked(file: File?) {
                showSnackBar("Coming soon!")
                /* if (file != null) {
                     viewModel.deleteByFileInBackground(file)
                     *//*viewModel.isFileDeleted.observe(viewLifecycleOwner, {
                        when (it) {
                            true -> adapter.notifyDataSetChanged()
                            false -> showSnackBar("Unable to delete voice note")
                        }
                    })*//*
                }*/
            }
        })
    }

    /**
     *  Hide the RecyclerView and Show the empty state
     *  when there's no voice note available
     */
    private fun showEmptyState() {
        binding?.vnRecyclerView?.hide()
        binding?.emptyNotesState?.show()
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
                start()
                state = true
            } catch (e: IOException) {
                Timber.e("prepare() failed")
            }
        }
    }

    /**
     *  Stop Recording and release recorder
     */
    private fun stopRecording() {
        if (state && mediaRecorder != null) {
            try {
                mediaRecorder?.stop()
                mediaRecorder?.reset()
                mediaRecorder?.release()
                mediaRecorder = null
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            state = false
            recordingStopped = true
            fetchAndDisplayVoiceNotes()
        } else {
            Timber.d("You are not recording right now!")
            mediaRecorder = null
        }
    }

    /**
     *  Pause Recording
     */
    private fun pauseRecording() {
        if (state) {
            if (!recordingStopped) {
                Timber.d("Stopped!")
                if (mediaRecorder != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mediaRecorder?.pause()
                    } else {
                        stopRecording()
                    }
                    recordingStopped = true
                }
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
        if (mediaRecorder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder?.resume()
            } else {
                startRecording()
            }
            recordingStopped = false
        }
    }

    private fun initDialogs() {
        renameDialog = Dialog(requireContext())
        renameDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_name, null)
        renameDialog.setContentView(renameDialogView)
    }

    /**
     *  show an MaterialAlertDialog and prompt to enter file name before
     *  exiting process completely
     */
    private fun renameVoiceNoteFile(item: File?) {
        renameDialogView.apply {
            val fileName = item?.name?.split(".mp3")?.get(0)
            val fileNameEdt = findViewById<EditText>(R.id.etFileName)

            fileNameEdt.setText(fileName.toString())

            findViewById<MaterialButton>(R.id.btnGoBack).setOnClickListener {
                renameDialog.cancel()
            }

            findViewById<MaterialButton>(R.id.btnRename).setOnClickListener {

                var name: String = ""

                if (fileNameEdt.takeText().isEmpty()) {
                    fileNameEdt.error = "Cannot be empty"
                    return@setOnClickListener
                }

                if (fileNameEdt.isContainsSpecialCharacter()) {
                    fileNameEdt.error = "Cannot contain special characters"
                    return@setOnClickListener
                }

                fileNameEdt.error = null

                if (fileNameEdt.takeText().endsWith(".mp3")) {
                    name = fileNameEdt.takeText().split(".mp3")[0]
                }

                Timber.d("filename: $name")

                val fromFile = File(getOutputDirectory(requireActivity()), item?.name.toString())
                val newFile = File(getOutputDirectory(requireActivity()), "$name.mp3")
                try {
                    Timber.d("file last modified: ${newFile.lastModified()}")
                    viewModel.renameFile(fromFile, "$name.mp3")
                } catch (e: IOException) {
                    showSnackBar("Something went wrong: $e")

                }
            }
        }

        renameDialog.show()

        val window = renameDialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    override fun onStartRecording() {
        // Start Recording Note
        startRecording()
    }

    override fun onPauseRecording() {
        // Pause Recording  and Release Media
        pauseRecording()
    }

    override fun onStopRecording() {
        // Stop Recording and Release Media
        stopRecording()
    }

    override fun onStop() {
        super.onStop()
        mediaRecorder?.release()
        mediaRecorder = null
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
