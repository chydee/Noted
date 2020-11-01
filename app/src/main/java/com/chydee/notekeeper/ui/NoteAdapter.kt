package com.chydee.notekeeper.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.NoteItemBinding

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    private lateinit var listener: OnItemClickListener

    var tracker: SelectionTracker<Long>? = null

    var items: ArrayList<Note> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    inner class NoteViewHolder(private var binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, isActivated: Boolean = false) {
            binding.notes = note
            itemView.isActivated = isActivated
            binding.noteCard.strokeColor = if (isActivated) Color.GREEN else Color.GRAY
            if (note.color != -1) {
                binding.noteCard.setCardBackgroundColor(note.color)
            }
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

    inner class DiffCallback(private val oldList: List<Note>, private val newList: List<Note>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].noteId == newList[newItemPosition].noteId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].noteDetail == newList[newItemPosition].noteDetail
        }
    }

    interface OnItemClickListener {
        fun onNoteClick(note: Note)
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun updateNotes(newItems: List<Note>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: NoteAdapter.NoteViewHolder, position: Int) {
        val note = items[position]
        tracker?.let {
            holder.bind(note, it.isSelected(position.toLong()))
        }
        holder.itemView.setOnClickListener {
            listener.onNoteClick(note)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
