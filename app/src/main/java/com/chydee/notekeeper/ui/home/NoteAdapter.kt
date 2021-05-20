package com.chydee.notekeeper.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.ItemNoteBinding
import com.chydee.notekeeper.utils.AutoUpdatableAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), AutoUpdatableAdapter, Filterable {

    private var items: ArrayList<Note> by Delegates.observable(ArrayList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.noteId == n.noteId }
    }

    private lateinit var itemsFilter: MutableList<Note>

    private var onAttach = true

    private lateinit var listener: OnItemClickListener

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class NoteViewHolder(private var binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
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
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface OnItemClickListener {
        fun onNoteClick(note: Note)
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun setNotes(notes: ArrayList<Note>) {
        this.items = notes
        itemsFilter = ArrayList(notes)
    }

    internal fun getNotes() = this.items

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = items[position]
        tracker.let {
            it?.isSelected(position.toLong())?.let { pos -> holder.bind(note, pos) }
        }
        holder.itemView.setOnClickListener {
            listener.onNoteClick(note)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getFilter(): Filter {
        return itemFilter
    }

    private val itemFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Note> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(itemsFilter)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ENGLISH).trim { it <= ' ' }
                for (item in itemsFilter) {
                    if (item.noteTitle.toLowerCase(Locale.ENGLISH).contains(filterPattern) or item.noteDetail.toLowerCase(Locale.ENGLISH)
                            .contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            items = (results.values as ArrayList<Note>?) as ArrayList<Note>
        }
    }
}
