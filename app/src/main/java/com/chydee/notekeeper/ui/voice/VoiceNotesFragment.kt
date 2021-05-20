package com.chydee.notekeeper.ui.voice

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
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

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

private const val REQUEST_ACCESS_FILES_PERMISSION = 300

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

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var recordAudioPermission: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var accessFilesPermission: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var adapter: VoiceNotesAdapter

    private lateinit var renameDialog: Dialog
    private lateinit var renameDialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Record to the external cache directory for visibility
        fileName = "${getOutputDirectory(requireContext())}/${System.currentTimeMillis()}.mp3"
        // ActivityCompat.requestPermissions(requireActivity(), accessFilesPermission, REQUEST_ACCESS_FILES_PERMISSION)
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
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                RecordNoteBottomSheet.instanceOfThis(this).show(childFragmentManager, "RecordNotes")
            } else {
                ActivityCompat.requestPermissions(requireActivity(), recordAudioPermission, REQUEST_RECORD_AUDIO_PERMISSION)
            }
        }
    }

    /**
     *  Check if the  Manifest.permission.WRITE_EXTERNAL_STORAGE and Manifest.permission.READ_EXTERNAL_STORAGE
     *  has been granted by the user and then retrieve VoiceNotes from App Specific Storage else
     *  !retrieve any VoiceNote and
     *  Show reason why they should grant the permission
     */
    private fun checkIfFilePermissionsAreGrantedAndFetchVoiceNotes() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchAndDisplayVoiceNotes()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), accessFilesPermission, REQUEST_ACCESS_FILES_PERMISSION)
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

            override fun onPlayPauseClicked() {
            }

            override fun onStopPlaying() {
            }

            override fun onSkipForward() {
            }

            override fun onSkipBackward() {
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
            fetchAndDisplayVoiceNotes()
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

    private fun initDialogs() {
        renameDialog = Dialog(requireContext())
        renameDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_name, null)
        renameDialog.setContentView(renameDialogView)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ACCESS_FILES_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.d("REQUEST_RECORD_AUDIO_PERMISSION: $REQUEST_RECORD_AUDIO_PERMISSION is GRANTED")
            checkIfFilePermissionsAreGrantedAndFetchVoiceNotes()
        }

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Do Something
        }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
