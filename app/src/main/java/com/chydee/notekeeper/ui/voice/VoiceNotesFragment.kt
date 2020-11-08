package com.chydee.notekeeper.ui.voice

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.chydee.notekeeper.databinding.VoiceNotesFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class VoiceNotesFragment : BaseFragment(), RecordNoteBottomSheet.OnClickListener {

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


    private fun saveNote(fileName: String) {
        val f: File = File("/somedir")
        // create a File object for the parent directory
        val notedAudiosDir = File("/voice_notes/")
        // have the object build the directory structure, if needed.
        notedAudiosDir.mkdirs()
        // create a File object for the output file
        val outputFile = File(notedAudiosDir, fileName)
        // now attach the OutputStream to the file object, instead of a String representation
        try {
            val fos = FileOutputStream(outputFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

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
    }

    override fun onPauseRecording() {
        //Pause Recording  and Release Media
    }

    override fun onStopRecording() {
        //Stop Recording and Release Media
    }

}