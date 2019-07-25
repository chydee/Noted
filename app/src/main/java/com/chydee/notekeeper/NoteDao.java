package com.chydee.notekeeper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NoteDao {

    //Insert or add a note to the note_table
    @Insert
    void insert(Note note);

    //Update a single note
    @Update
    void update(Note note);

    //Delete a single note from the note_table
    @Delete
    void delete(Note note);

    //Delete all notes inside the note_table
    @Query("DELETE FROM note_table")
    void deleteAll();

    //This method returns all the notes needed so that we can put them in our recycler view
}
