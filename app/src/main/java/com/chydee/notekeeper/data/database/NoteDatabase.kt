package com.chydee.notekeeper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chydee.notekeeper.data.dao.NoteDao
import com.chydee.notekeeper.data.dao.TrashDao
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash


@Database(entities = [Note::class, Trash::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val trashDao: TrashDao
}