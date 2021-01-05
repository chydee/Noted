package com.chydee.notekeeper.utils

import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.data.model.Trash

fun Note.toTrash() = Trash(noteId, noteTitle, noteDetail, lastEdit, isLocked, color, password)

fun Trash.toNote() = Note(noteId, noteTitle, noteDetail, lastEdit, isLocked, color, password)
