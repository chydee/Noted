package com.chydee.notekeeper.ui.voice

import android.view.*
import android.widget.Filter
import android.widget.Filterable
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

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceNoteViewHolder {
        return VoiceNoteViewHolder(ItemVoiceNoteBinding.inflate(LayoutInflater.from(parent.context)))
    }

    inner class VoiceNoteViewHolder(private var binding: ItemVoiceNoteBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        private var currentFile: File? = null
        fun bind(file: File) {
            binding.file = file
            this.currentFile = file
            //binding.fileName.text = file.name
            binding.playPauseCircle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.optionsArea.show()
                } else {
                    binding.optionsArea.remove()
                }
            }
            itemView.setOnCreateContextMenuListener(this)
            binding.executePendingBindings()
        }

        /**
         *  Create Context Menu
         */
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.setHeaderTitle("Options")
            val renameOption: MenuItem? = menu?.add(Menu.NONE, 1, 1, "Rename")
            val deleteOption: MenuItem? = menu?.add(Menu.NONE, 2, 2, "Delete")

            renameOption?.setOnMenuItemClickListener(this)
            deleteOption?.setOnMenuItemClickListener(this)
        }

        /**
         * Listen to MenuItem's click event and perform set listener
         */
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (currentFile != null) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    return when (item?.itemId) {
                        1 -> {
                            listener.onRenameClicked(file = currentFile!!)
                            true
                        }
                        2 -> {
                            listener.onDeleteClicked(currentFile!!)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }
            return false
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
        fun onRenameClicked(file: File)
        fun onDeleteClicked(file: File)
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun submitList(voiceNotes: ArrayList<File>) {
        this.items = voiceNotes
        itemsFilter = ArrayList(voiceNotes)
    }

    internal fun getItems() = this.items

    override fun onBindViewHolder(holder: VoiceNotesAdapter.VoiceNoteViewHolder, position: Int) {
        val voiceNote = items[position]
        holder.bind(voiceNote)
        holder.itemView.setOnClickListener {
            listener.onFileClicked(voiceNote)
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
