package com.chydee.notekeeper.data

import androidx.room.*
import com.chydee.notekeeper.data.model.Note
import io.reactivex.Completable
import io.reactivex.Single


//Defines the method for using the Note class with room
@Dao
interface NoteDao {

    /*
    * This method inserts the note into the database
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Completable

    /*
    * This method updates a single note from the note database
     */
    @Update
    suspend fun update(note: Note): Completable

    @Delete
    suspend fun deleteNote(note: Note): Completable

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM note")
    suspend fun deleteAllNotes(): Completable

    /**
     * Selects and returns the latest night.
     */
    @Query("SELECT * FROM note ORDER BY id DESC LIMIT 1")
    suspend fun getLatestNote(): Single<Note>

    @Query("SELECT * FROM note ORDER BY id DESC")
    suspend fun getAllNotes(): Single<List<Note>>
}