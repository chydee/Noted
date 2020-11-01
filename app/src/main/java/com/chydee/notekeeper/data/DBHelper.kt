package com.chydee.notekeeper.data

import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash
import io.reactivex.Completable
import io.reactivex.Single

interface DBHelper {
    fun insert(note: Note): Completable

    fun update(note: Note): Completable

    fun delete(note: Note): Completable

    fun deleteAllNotes(): Completable

    fun getLatestNote(): Single<Note>

    fun getAllNotes(): Single<List<Note>>

    fun insertTrash(trash: Trash): Completable

    fun insertTrash(trash: List<Trash>): Completable

    fun deleteTrash(trash: Trash): Completable

    fun clearTrash(): Completable

    fun getAllTrash(): Single<List<Trash>>
}