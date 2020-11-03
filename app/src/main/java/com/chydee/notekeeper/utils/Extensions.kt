package com.chydee.notekeeper.utils

import android.widget.EditText
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash

fun Note.toTrash() = Trash(noteId, noteTitle, noteDetail, lastEdit, isEncrypted, color)

fun Trash.toNote() = Note(noteId, noteTitle, noteDetail, lastEdit, isEncrypted, color)

fun EditText.takeText() = this.text.toString()