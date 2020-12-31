package com.chydee.notekeeper.ui.voice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class VoiceNotesViewModel : ViewModel() {
    private val _isFileRenaming = MutableLiveData<Boolean>()
    private val _isFileDeleting = MutableLiveData<Boolean>()

    init {
        _isFileDeleting.postValue(false)
        _isFileRenaming.postValue(false)
    }

    val isFileRenaming: LiveData<Boolean>
        get() = _isFileRenaming

    val isFileDeleting: LiveData<Boolean>
        get() = _isFileDeleting

    fun fetchAudioFiles(dir: File) {
        val filesList = ArrayList<File>()
        val files = dir.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                fetchAudioFiles(file)
            } else {
                if (file.name.endsWith(".pdf")) {
                    filesList.add(file)
                }
            }
        }
    }

    fun renameFile(src: File?, dst: File) = viewModelScope.launch {
        copyFileInBackground(src, dst)
    }

    @Throws(IOException::class)
    private suspend fun copyFileInBackground(src: File?, dst: File) = withContext(Dispatchers.IO) {
        _isFileRenaming.postValue(true)
        FileInputStream(src).use { `in` ->
            FileOutputStream(dst).use { out ->
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }

        withContext(Dispatchers.Main) {
            _isFileRenaming.postValue(false)
        }
    }

    private suspend fun deleteByFileInBackground(file: File) = withContext(Dispatchers.IO) {
        _isFileDeleting.postValue(true)
        val f = File(file.toURI())
        f.delete()

        withContext(Dispatchers.Main) {
            _isFileDeleting.postValue(false)
        }
    }
}