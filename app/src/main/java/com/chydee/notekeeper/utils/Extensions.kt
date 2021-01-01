package com.chydee.notekeeper.utils

import android.view.View
import android.widget.EditText
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash
import java.util.regex.Matcher
import java.util.regex.Pattern

fun Note.toTrash() = Trash(noteId, noteTitle, noteDetail, lastEdit, isLocked, color, password)

fun Trash.toNote() = Note(noteId, noteTitle, noteDetail, lastEdit, isLocked, color, password)

fun EditText.takeText() = this.text.toString()

fun EditText.isContainsSpecialCharacter(): Boolean {
    val p: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
    val m: Matcher = p.matcher(this.text.toString().trim())
    return m.find()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.remove() {
    this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

