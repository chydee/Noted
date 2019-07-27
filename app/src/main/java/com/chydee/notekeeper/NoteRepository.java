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
    }

}
