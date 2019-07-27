package com.chydee.notekeeper;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    //Constructor to reassign the variables
    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    //Create methods for all database operations
    //Insert()
    public void insert(Note note){

    }
    //Update
    public void update(Note note){

    }
    //Delete()
    public void delete(Note note){

    }

    //Delete all notes
    public void deleteAll(){

    }
    //Returns LiveData


    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
