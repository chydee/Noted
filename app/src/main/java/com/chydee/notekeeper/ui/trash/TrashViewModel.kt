package com.chydee.notekeeper.ui.trash

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

class TrashViewModel @ViewModelInject constructor(private val dbHelper: DBHelperImpl) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _deletedNotes = MutableLiveData<List<Trash>>()
    val deletedNotes: LiveData<List<Trash>>
        get() = _deletedNotes

    fun getDeletedNotes() {
        dbHelper.getAllTrash().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ notes ->
                    if (notes.isNotEmpty()) {
                        _deletedNotes.postValue(notes)
                    } else {
                        _deletedNotes.postValue(listOf())
                    }
                    notes.forEach {
                        Timber.tag("Trash Note").d(it.noteTitle)
                    }
                }, {}).let { compositeDisposable.add(it) }
    }

    fun deleteForever() {
        dbHelper.clearTrash().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun removeTrash(trash: Trash) {
        dbHelper.deleteTrash(trash).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    fun insertNote(note: Note) {
        dbHelper.insert(note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {}).let { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onCleared()
    }
}