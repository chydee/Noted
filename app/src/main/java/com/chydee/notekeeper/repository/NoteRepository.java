package com.chydee.notekeeper.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.chydee.notekeeper.dao.NoteDao;
import com.chydee.notekeeper.database.NoteDatabase;
import com.chydee.notekeeper.model.Note;

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
    //This helper methods below are the APIs that the repository exposes to the outside
    //Insert()
    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }
    //Update
    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }
    //Delete()
    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    //Delete all notes
    public void deleteAll(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
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
    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }


        @Override
        protected Void doInBackground(Note... notes) {

            noteDao.update(notes[0]);
            return null;
        }
    }
    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }


        @Override
        protected Void doInBackground(Note... notes) {

            noteDao.delete(notes[0]);
            return null;
        }
    }
    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void>{

        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            noteDao.deleteAll();
            return null;
        }
    }
}
