package com.chydee.notekeeper.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.database.Note
import com.chydee.notekeeper.databinding.NoteItemBinding

class NoteAdapter(private val onClickListener: OnClickListener) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    class NoteViewHolder(private var binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.property = note
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (note: Note) -> Unit) {
        fun onClick(note: Note) = clickListener(note)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val notes = getItem(position)
        holder.bind(notes)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(notes)
        }
    }
}

