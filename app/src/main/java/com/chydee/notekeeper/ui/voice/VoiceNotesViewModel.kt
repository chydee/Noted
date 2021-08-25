package com.chydee.notekeeper.ui.voice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VoiceNotesViewModel @Inject constructor() : ViewModel() {


    private val _voiceNotes = MutableLiveData<List<File>>()
    val voiceNotes: LiveData<List<File>>
        get() = _voiceNotes

    private val _isFileRenamed = MutableLiveData<Boolean>()
    private val _isFileDeleted = MutableLiveData<Boolean>()


    val isFiledRenamed: LiveData<Boolean>
        get() = _isFileRenamed

    val isFileDeleted: LiveData<Boolean>
        get() = _isFileDeleted

    fun fetchAudioFiles(dir: File) {
        viewModelScope.launch {
            val filesList = ArrayList<File>()
            val files = dir.listFiles()
            files?.forEach { file ->
                if (file.isDirectory) {
                    fetchAudioFiles(file)
                } else {
                    if (file.name.endsWith(".mp3")) {
                        filesList.add(file)
                    }
                }
            }
            _voiceNotes.postValue(filesList)
        }
    }

    fun renameFile(src: File, newName: String) {
        viewModelScope.launch {
            _isFileRenamed.value = false
            val from: File = File(src.absolutePath, src.nameWithoutExtension)
            val to: File = File(src.absolutePath, newName.trim())
            rename(from, to)
            _isFileRenamed.postValue(true)

        }
    }

    private fun rename(from: File, to: File): Boolean {
        return from.parentFile.exists() && from.exists() && from.renameTo(to)
    }

    fun deleteByFileInBackground(file: File) {
        viewModelScope.launch {

            _isFileDeleted.postValue(false)
            val f = File(file.toURI())
            f.delete()



            _isFileDeleted.postValue(true)

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
