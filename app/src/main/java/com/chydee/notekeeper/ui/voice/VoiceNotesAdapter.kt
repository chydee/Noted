package com.chydee.notekeeper.ui.voice

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.databinding.ItemVoiceNoteBinding
import com.chydee.notekeeper.utils.AutoUpdatableAdapter
import com.chydee.notekeeper.utils.remove
import com.chydee.notekeeper.utils.show
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class VoiceNotesAdapter : RecyclerView.Adapter<VoiceNotesAdapter.VoiceNoteViewHolder>(), AutoUpdatableAdapter, Filterable {

    private var items: ArrayList<File> by Delegates.observable(ArrayList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.name == n.name }
    }

    private lateinit var itemsFilter: MutableList<File>

    private var onAttach = true

    private lateinit var listener: OnItemClickListener

    var tracker: SelectionTracker<Long>? = null


    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceNoteViewHolder {
        return VoiceNoteViewHolder(ItemVoiceNoteBinding.inflate(LayoutInflater.from(parent.context)))
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

    inner class VoiceNoteViewHolder(private var binding: ItemVoiceNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: File, isActivated: Boolean = false) {
            binding.file = note
            itemView.isActivated = isActivated
            binding.container.strokeColor = if (isActivated) Color.GREEN else Color.GRAY
            binding.playPauseCircle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.optionsArea.show()
                } else {
                    binding.optionsArea.remove()
                }
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

    interface OnItemClickListener {
        fun onFileClicked(file: File)
        fun onPlayPauseClicked()
        fun onStopPlaying()
        fun onSkipForward()
        fun onSkipBackward()
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun setNotes(notes: ArrayList<File>) {
        this.items = notes
        itemsFilter = ArrayList(notes)
    }

    internal fun getNotes() = this.items

    override fun onBindViewHolder(holder: VoiceNotesAdapter.VoiceNoteViewHolder, position: Int) {
        val note = items[position]
        tracker?.let {
            holder.bind(note, it.isSelected(position.toLong()))
        }
        holder.itemView.setOnClickListener {
            listener.onFileClicked(note)
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
            val filteredList: MutableList<File> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(itemsFilter)
            } else {
                val filterPattern =
                        constraint.toString().toLowerCase(Locale.ENGLISH).trim { it <= ' ' }
                for (item in itemsFilter) {
                    if (item.name.toLowerCase(Locale.ENGLISH).contains(filterPattern)) {
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
            items = (results.values as ArrayList<File>?)!!
        }
    }

}
