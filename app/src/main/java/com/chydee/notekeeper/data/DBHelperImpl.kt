package com.chydee.notekeeper.data

import android.app.Application
import com.chydee.notekeeper.data.database.NoteDatabase
import com.chydee.notekeeper.data.model.Note
import io.reactivex.Completable
import io.reactivex.Single

class DBHelperImpl(application: Application) : DBHelper {

    private val database = NoteDatabase.getInstance(application).noteDao

    override fun insert(note: Note): Completable {
        return database.insert(note)
    }

    override fun update(note: Note): Completable {
        return database.update(note)
    }

    override fun delete(note: Note): Completable {
        return database.delete(note)
    }

    override fun deleteAllNotes(): Completable {
        return database.deleteAllNotes()
    }

    override fun getLatestNote(): Single<Note> {
        return database.getLatestNote()
    }

    override fun getAllNotes(): Single<List<Note>> {
        return database.getAllNotes()
    }

}