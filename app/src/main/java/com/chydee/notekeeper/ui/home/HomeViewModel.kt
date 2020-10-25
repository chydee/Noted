package com.chydee.notekeeper.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chydee.notekeeper.data.DBHelperImpl
import com.chydee.notekeeper.data.model.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel constructor(application: Application) : AndroidViewModel(application) {

    private val dbHelper: DBHelperImpl = DBHelperImpl(application)

    private val compositeDisposable = CompositeDisposable()

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>>
        get() = _notes

    private val _navigateToSelectedNote = MutableLiveData<Note>()
    val navigateToSelectedNote: LiveData<Note>
        get() = _navigateToSelectedNote


    private val _properties = MutableLiveData<List<Note>>()
    val properties: LiveData<List<Note>>
        get() = _properties

    fun getNotes() {
        dbHelper.getAllNotes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ notes ->
                    if (notes.isNotEmpty()) {
                        _notes.postValue(notes)
                    } else {
                        _notes.postValue(listOf())
                    }
                    notes.forEach {
                        Log.d("Notes", it.noteTitle)
                    }
                }, {}).let { compositeDisposable.add(it) }
    }

    fun deleteAllNotes() {
        dbHelper.deleteAllNotes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {}).let { compositeDisposable.add(it) }
    }


    fun displayNoteDetails(note: Note) {
        _navigateToSelectedNote.value = note
    }

    fun displayNoteCompleted() {
        _navigateToSelectedNote.value = null
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onCleared()
    }
}
