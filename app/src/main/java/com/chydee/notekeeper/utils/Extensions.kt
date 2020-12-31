package com.chydee.notekeeper.utils

import android.view.View
import android.widget.EditText
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash

fun Note.toTrash() = Trash(noteId, noteTitle, noteDetail, lastEdit, isLocked, color, password)

fun Trash.toNote() = Note(noteId, noteTitle, noteDetail, lastEdit, isLocked, color, password)

fun EditText.takeText() = this.text.toString()

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.remove() {
    this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

