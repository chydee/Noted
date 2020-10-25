package com.chydee.notekeeper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chydee.notekeeper.data.model.Note


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao //Connects the database to the DAO

    companion object {
        /*
        * INSTANCE will keep a reference to any database returned via getInstance.
        *
        * this will help us avoid repeatedly initializing the database, which is expensive
        * The value of a volatile variable will never be cached, and all writes and
        *  reads will be done to and from the main memory. It means that changes made by one
        *  thread to shared data are visible to other threads.
        * */
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            NoteDatabase::class.java,
                            "note_keeper_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}