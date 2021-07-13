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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.ext.hide
import com.chydee.notekeeper.utils.ext.show
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
        checkIfFilePermissionsAreGrantedAndFetchVoiceNotes()
        dialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    private fun setUpOnClickListeners() {
        binding?.recordNewVoiceNote?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                launchRecordNoteSheet()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            } else {
                registerForActivityResult(
                    RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        launchRecordNoteSheet()
                    } else {
                    }
                }.launch(Manifest.permission.RECORD_AUDIO)
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
        if (player?.isPlaying == true) {
            player?.pause()
            length = player?.currentPosition ?: 0
        }
    }

    private fun resumePlaying() {
        Timber.d("Resume!")
        if (player?.isPlaying == false) {
            player?.apply {
                seekTo(length)
                start()
            }
        }
    }

    private fun stopPlaying() {
        if (player?.isPlaying == true) {
            player?.stop()
            player?.release()
        } else {
            Timber.d("You are not playing any voice note right now!")
        }
        player = null
    }

    /**
     *  Check if the  Manifest.permission.WRITE_EXTERNAL_STORAGE and Manifest.permission.READ_EXTERNAL_STORAGE
     *  has been granted by the user and then retrieve VoiceNotes from App Specific Storage else
     *  !retrieve any VoiceNote and
     *  Show reason why they should grant the permission
     */
    private fun checkIfFilePermissionsAreGrantedAndFetchVoiceNotes() {

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fetchAndDisplayVoiceNotes()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            registerForActivityResult(
                RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    fetchAndDisplayVoiceNotes()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     *
     *  Fetch And Display Voice Notes
     */
    private fun fetchAndDisplayVoiceNotes() {
        viewModel.fetchAudioFiles(getOutputDirectory(requireContext()))
        loadVoiceNotes()
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
                    Toast.makeText(context, it.size.toString(), Toast.LENGTH_SHORT).show()
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
                if (player?.isPlaying == true) {
                    player?.apply {
                        seekTo(10)
                        start()
                    }
                }
            }

            override fun onSkipBackward() {
                if (player?.isPlaying == true) {
                    player?.apply {
                        seekTo(-10)
                        start()
                    }
                }
            }

            override fun onRenameClicked(file: File?) {
            }

            override fun onDeleteClicked(file: File?) {
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
            } catch (e: IOException) {
                Timber.e("prepare() failed")
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
            recordingStopped = true
        } else {
            Timber.d("You are not recording right now!")
            mediaRecorder = null
        }
        mediaRecorder = null
        fetchAndDisplayVoiceNotes()
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

    private fun initDialogs() {
        renameDialog = Dialog(requireContext())
        renameDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_name, null)
        renameDialog.setContentView(renameDialogView)
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }


    /**
     *  show an MaterialAlertDialog and prompt to enter file name before
     *  exiting process completely
     */
    /* private fun renameVoiceNoteFile(item: File?) {
         renameDialogView.apply {
             val fileName = item?.name?.split(".")?.get(0)
             etFileName.setText(fileName.toString())

             btnGoBack.setOnClickListener {
                 renameDialog.cancel()
             }

             btnRename.setOnClickListener {

                 var name: String = ""

                 if (etFileName.takeText().isEmpty()) {
                     etFileName.error = "Cannot be empty"
                     return@setOnClickListener
                 }

                 if (etFileName.isContainsSpecialCharacter()) {
                     etFileName.error = "Cannot contain special characters"
                     return@setOnClickListener
                 }

                 etFileName.error = null

                 if (etFileName.takeText().endsWith(".mp3")) {
                     name = etFileName.takeText().split(".mp3")[0]
                 }

                 Timber.d("filename: $name")

                 val fromfile = File(getOutputDirectory(requireActivity()), item?.name.toString())
                 val newFile = File(getOutputDirectory(requireActivity()), "$name.pdf")
                 try {
                     Timber.d("file last modified: ${newFile.lastModified()}")
                     viewModel.renameFile(fromfile, newFile)
                 } catch (e: IOException) {
                     // Toast.makeText(this@MainActivity, SyncStateContract.Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                 }
             }
         }

         renameDialog.show()

         val window = renameDialog.window
         window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
     }*/


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
