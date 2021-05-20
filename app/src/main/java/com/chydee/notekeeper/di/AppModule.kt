package com.chydee.notekeeper.di

import android.content.Context
import androidx.room.Room
import com.chydee.notekeeper.data.database.NoteDatabase
import com.chydee.notekeeper.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        NoteDatabase::class.java,
        Constants.NOTED_DB_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideNoteDao(db: NoteDatabase) = db.noteDao

    @Singleton
    @Provides
    fun provideTrashDao(db: NoteDatabase) = db.trashDao
}