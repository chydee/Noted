package com.chydee.notekeeper.home

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.chydee.notekeeper.database.Note

class NoteAdapter(val onClickListener: OnClickListener) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback), Parcelable {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        return NoteViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }


    class OnClickListener(val clickListener: (notes: Note) -> Unit) {
        fun onClick(notes: Note) = clickListener(notes)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }

    class NoteViewHolder(private var binding: GridViewItemBinding)

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteAdapter> {
        override fun createFromParcel(parcel: Parcel): NoteAdapter {
            return NoteAdapter(parcel)
        }

        override fun newArray(size: Int): Array<NoteAdapter?> {
            return arrayOfNulls(size)
        }
    }
}