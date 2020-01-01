package com.chydee.notekeeper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.chydee.notekeeper.model.Note;
import com.chydee.notekeeper.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mRepository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new NoteRepository(application);
        allNotes = mRepository.getAllNotes();
    }

    public void insert(Note note){
        mRepository.insert(note);
    }

    public void update(Note note){
        mRepository.update(note);
    }

    public void delete(Note note){
        mRepository.delete(note);
    }

    public void deleteAllNotes(){
        mRepository.deleteAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
