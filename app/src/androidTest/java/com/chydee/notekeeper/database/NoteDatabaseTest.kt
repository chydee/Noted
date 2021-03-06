package com.chydee.notekeeper.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chydee.notekeeper.data.dao.NoteDao
import com.chydee.notekeeper.data.database.NoteDatabase
import com.chydee.notekeeper.data.model.Note
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {
    private lateinit var noteDao: NoteDao
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        noteDao = db.noteDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNote() {
        val note = Note(noteId = 1, noteTitle = "Hi", noteDetail = "Hmmmm", isEncrypted = false, lastEdit = "12", color = -1)
        noteDao.insert(note)
        // val thisNote = noteDao.getThisNote()
        // Assert.assertEquals(thisNote.noteTitle, "Hi")
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetNote() {
        noteDao.update(Note(noteId = 1, noteTitle = "Test", noteDetail = "Testing Testing", isEncrypted = false, lastEdit = "12", color = -1))
        // val updatedNote = noteDao.getThisNote()
        // Assert.assertNotEquals(updatedNote.noteTitle, "Hi")
    }
}
