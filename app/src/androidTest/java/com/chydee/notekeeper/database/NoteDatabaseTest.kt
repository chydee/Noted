package com.chydee.notekeeper.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {

    private lateinit var noteDatabaseDao: NoteDatabaseDao
    private lateinit var noteDb: NoteDatabase

    @Before
    @Throws(IOException::class)
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        noteDb = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).allowMainThreadQueries().build()
        noteDatabaseDao = noteDb.noteDatabaseDao
    }


    @Test
    @Throws(Exception::class)
    fun insertAndGetNote() {
        val note = Note(noteId = 1, noteTitle = "Test Implementation", noteDetail = "Instrumentation Test for the database")
        noteDatabaseDao.insert(note)
        val thisNote = noteDatabaseDao.getThisNote()
        Assert.assertEquals(thisNote?.noteId, 1)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        noteDb.close()
    }
}