package com.chydee.notekeeper.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.NoteItemBinding

class NoteAdapter : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback) {
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    inner class NoteViewHolder(private var binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, isActivated: Boolean = false) {
            binding.notes = note
            itemView.isActivated = isActivated
            binding.noteCard.strokeColor = if (isActivated) Color.GREEN else Color.GRAY
            binding.executePendingBindings()
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
                object : ItemDetailsLookup.ItemDetails<Long>() {
                    override fun getPosition(): Int = adapterPosition
                    override fun getSelectionKey(): Long? = itemId
                }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onNoteClick(note: Note)
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        tracker?.let {
            holder.bind(note, it.isSelected(position.toLong()))
        }
        holder.itemView.setOnClickListener {
            listener.onNoteClick(note)
        }
    }

}
