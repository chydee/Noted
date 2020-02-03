package com.chydee.notekeeper

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chydee.notekeeper.database.Note
import com.chydee.notekeeper.database.NoteDatabase
import com.chydee.notekeeper.database.NoteDatabaseDao
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {

    private lateinit var noteDao: NoteDatabaseDao
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext // initializes the context
        // Will make use of an in-memory database cos the data saved here are cleared once the process is killed
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)//Allowing main thread queries just start testing
                .allowMainThreadQueries()
                .build()

        noteDao = db.noteDatabaseDao
    }

    @After
    @Throws(Exception::class)
    fun closedb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNote() {
        val note = Note(noteTitle = "Hello", noteDetail = "Details")
        noteDao.insert(note)
        val thisNote = noteDao.getTonight()
        assertEquals(thisNote?.noteTitle, "Hello")
    }
}