package com.chydee.notekeeper.data

import com.chydee.notekeeper.data.dao.NoteDao
import com.chydee.notekeeper.data.dao.TrashDao
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class DBHelperImpl @Inject constructor(private val noteDao: NoteDao, private val trashDao: TrashDao) : DBHelper {


    override fun insert(note: Note): Completable {
        return noteDao.insert(note)
    }

    override fun insert(notes: List<Note>): Completable {
        return noteDao.insert(notes)
    }

    override fun update(note: Note): Completable {
        return noteDao.update(note)
    }

    override fun delete(note: Note): Completable {
        return noteDao.delete(note)
    }

    override fun deleteAllNotes(): Completable {
        return noteDao.deleteAllNotes()
    }

    override fun getLatestNote(): Single<Note> {
        return noteDao.getLatestNote()
    }

    override fun getAllNotes(): Single<List<Note>> {
        return noteDao.getAllNotes()
    }

    override fun insertTrash(trash: Trash): Completable {
        return trashDao.insert(trash)
    }

    override fun insertTrash(trash: List<Trash>): Completable {
        return trashDao.insert(trash)
    }

    override fun deleteTrash(trash: Trash): Completable {
        return trashDao.delete(trash)
    }

    override fun clearTrash(): Completable {
        return trashDao.clearTrash()
    }

    override fun getAllTrash(): Single<List<Trash>> {
        return trashDao.getAllTrash()
    }


}