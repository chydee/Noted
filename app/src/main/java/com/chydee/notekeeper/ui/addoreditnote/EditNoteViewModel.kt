package com.chydee.notekeeper.ui.addoreditnote

import androidx.lifecycle.ViewModel
import com.chydee.notekeeper.data.DBHelperImpl
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(private val dbHelper: DBHelperImpl) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun insertNote(note: Note) {
        dbHelper.insert(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun updateNote(note: Note) {
        dbHelper.update(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun deleteNote(note: Note) {
        dbHelper.delete(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun addToTrash(trash: Trash) {
        dbHelper.insertTrash(trash).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onCleared()
    }
}
