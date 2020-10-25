package com.chydee.notekeeper.ui.addoreditnote

import android.content.Context
import androidx.lifecycle.ViewModel
import com.chydee.notekeeper.data.DBHelperImpl
import com.chydee.notekeeper.data.model.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EditNoteViewModel constructor(context: Context) : ViewModel() {
    private val dbHelper: DBHelperImpl = DBHelperImpl(context)

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

    override fun onCleared() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onCleared()
    }
}
