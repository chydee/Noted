package com.chydee.notekeeper;

import android.app.Application;
import android.os.AsyncTask;

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
        new InsertNoteAsyncTask(noteDao).execute(note);
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

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }


        @Override
        protected Void doInBackground(Note... notes) {

            noteDao.insert(notes[0]);
            return null;
        }
    }
}
