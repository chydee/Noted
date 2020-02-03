package com.chydee.notekeeper.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


//Defines the method for using the Note class with room
@Dao
interface NoteDatabaseDao {

    /*
    * This method inserts the note into the database
    */
    @Insert
    fun insert(note: Note)

    /*
    * This method updates a single note from the note database
     */
    @Update
    fun update(note: Note)

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM note_table")
    fun clear()

    /**
     * Selects and returns the latest night.
     */
    @Query("SELECT * FROM note_table ORDER BY noteId DESC LIMIT 1")
    fun getTonight(): Note?

    @Query("SELECT * FROM note_table ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Note>>
}