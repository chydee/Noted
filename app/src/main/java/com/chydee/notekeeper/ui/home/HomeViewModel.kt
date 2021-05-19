package com.chydee.notekeeper.ui.home

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chydee.notekeeper.data.DBHelperImpl
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomeViewModel @ViewModelInject constructor(private val dbHelper: DBHelperImpl) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>>
        get() = _notes

    private val _navigateToSelectedNote = MutableLiveData<Note>()
    val navigateToSelectedNote: LiveData<Note>
        get() = _navigateToSelectedNote

    fun getNotes() {
        dbHelper.getAllNotes().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { notes ->
                    if (notes.isNotEmpty()) {
                        _notes.postValue(notes)
                    } else {
                        _notes.postValue(listOf())
                    }
                    notes.forEach {
                        Timber.d(it.noteTitle)
                    }
                },
                {}
            ).let { compositeDisposable.add(it) }
    }

    fun deleteNote(note: Note) {
        dbHelper.delete(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
        Log.d("DeleteHome", "$note deleted")
    }

    fun insertNote(note: Note) {
        dbHelper.insert(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun addToTrash(trash: List<Trash>) {
        dbHelper.insertTrash(trash).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun removeFromTrash(trash: Trash) {
        dbHelper.deleteTrash(trash).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun updateNote(note: Note) {
        dbHelper.update(note).subscribeOn(Schedulers.io())
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
