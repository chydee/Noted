package com.chydee.notekeeper.data

import android.content.Context
import com.chydee.notekeeper.data.database.NoteDatabase
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash
import io.reactivex.Completable
import io.reactivex.Single

class DBHelperImpl(context: Context) : DBHelper {

    private val noteTable = NoteDatabase.getInstance(context).noteDao
    private val trashTable = NoteDatabase.getInstance(context).trashDao

    override fun insert(note: Note): Completable {
        return noteTable.insert(note)
    }

    override fun insert(notes: List<Note>): Completable {
        return noteTable.insert(notes)
    }

    override fun update(note: Note): Completable {
        return noteTable.update(note)
    }

    override fun delete(note: Note): Completable {
        return noteTable.delete(note)
    }

    override fun deleteAllNotes(): Completable {
        return noteTable.deleteAllNotes()
    }

    override fun getLatestNote(): Single<Note> {
        return noteTable.getLatestNote()
    }

    override fun getAllNotes(): Single<List<Note>> {
        return noteTable.getAllNotes()
    }

    override fun insertTrash(trash: Trash): Completable {
        return trashTable.insert(trash)
    }

    override fun insertTrash(trash: List<Trash>): Completable {
        return trashTable.insert(trash)
    }

    override fun deleteTrash(trash: Trash): Completable {
        return trashTable.delete(trash)
    }

    override fun clearTrash(): Completable {
        return trashTable.clearTrash()
    }

    override fun getAllTrash(): Single<List<Trash>> {
        return trashTable.getAllTrash()
    }


}