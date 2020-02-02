package com.chydee.notekeeper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
        @PrimaryKey(autoGenerate = true)
        var noteId: Long,

        @ColumnInfo(name = "note_title")
        var noteTitle: String,

        @ColumnInfo(name = "note_detail")
        var noteDetail: String

)

