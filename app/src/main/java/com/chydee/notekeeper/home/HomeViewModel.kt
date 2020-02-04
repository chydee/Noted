package com.chydee.notekeeper.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chydee.notekeeper.database.Note
import com.chydee.notekeeper.database.NoteDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class HomeViewModel(val database: NoteDatabaseDao, application: Application) : AndroidViewModel(application) {


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var note = MutableLiveData<Note?>()

    val notes = database.getAllNotes()

    private val _properties = MutableLiveData<List<Note>>()


    val properties: LiveData<List<Note>>
        get() = _properties

    private val _navigateToSelectedNote = MutableLiveData<Note>()
    val navigateToSelectedNote: LiveData<Note>
        get() = _navigateToSelectedNote

    fun displayNoteDetails(note: Note) {
        _navigateToSelectedNote.value = note
    }

    fun displayNoteCompleted() {
        _navigateToSelectedNote.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
