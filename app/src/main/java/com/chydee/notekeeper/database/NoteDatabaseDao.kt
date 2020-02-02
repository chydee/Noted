package com.chydee.notekeeper.database

import androidx.lifecycle.LiveData
import androidx.room.*


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

    @Delete

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM note_table")
    fun clear()

    @Query("SELECT * FROM note_table ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Note>>
}