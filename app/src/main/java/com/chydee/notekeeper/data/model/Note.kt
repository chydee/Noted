package com.chydee.notekeeper.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "note")
data class Note(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val noteId: Long,

        @ColumnInfo(name = "note_title")
        val noteTitle: String,

        @ColumnInfo(name = "note_detail")
        val noteDetail: String,

        @ColumnInfo(name = "last_edit")
        val lastEdit: String,

        @ColumnInfo(name = "isEncrypted")
        val isEncrypted: Boolean
) : Parcelable

