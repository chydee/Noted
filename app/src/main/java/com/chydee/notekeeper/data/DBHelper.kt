package com.chydee.notekeeper.data

import com.chydee.notekeeper.data.model.Note
import io.reactivex.Completable
import io.reactivex.Single

interface DBHelper {
    fun insert(note: Note): Completable

    fun update(note: Note): Completable

    fun delete(note: Note): Completable

    fun deleteAllNotes(): Completable

    fun getLatestNote(): Single<Note>

    fun getAllNotes(): Single<List<Note>>
}