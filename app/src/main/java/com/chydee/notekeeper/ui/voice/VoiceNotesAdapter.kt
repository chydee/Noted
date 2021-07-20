package com.chydee.notekeeper.ui.voice

import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.databinding.ItemVoiceNoteBinding
import com.chydee.notekeeper.utils.ext.remove
import com.chydee.notekeeper.utils.ext.show
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class VoiceNotesAdapter : RecyclerView.Adapter<VoiceNotesAdapter.VoiceNoteViewHolder>(), Filterable {

    private var isPlaying: Boolean = false
    private val diffCallback = object : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    private lateinit var itemsFilter: MutableList<File>

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceNoteViewHolder {
        val bd = ItemVoiceNoteBinding.inflate(LayoutInflater.from(parent.context))
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bd.root.layoutParams = lp
        return VoiceNoteViewHolder(bd)
    }

    inner class VoiceNoteViewHolder(private var binding: ItemVoiceNoteBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener,
        MenuItem.OnMenuItemClickListener {
        private var currentFile: File? = null
        fun bind(file: File) {
            binding.file = file
            this.currentFile = file
            // binding.fileName.text = file.name

            binding.playPauseCircle.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    listener.onPlay(file)
                    binding.optionsArea.show()
                } else {
                    listener.onPause()
                    binding.optionsArea.remove()
                }
            }

            binding.stopPlaying.setOnClickListener {
                binding.optionsArea.remove()
                listener.onStop()
                binding.playPauseCircle.isChecked = false
            }

            binding.skipForward.setOnClickListener { listener.onSkipForward() }
            binding.skipBackward.setOnClickListener { listener.onSkipBackward() }
            itemView.setOnCreateContextMenuListener(this)
            binding.executePendingBindings()
        }

        private fun pauseResume() {
            if (isPlaying) {
                listener.onPause()
                binding.optionsArea.remove()
            } else {
                listener.onResume()
                binding.optionsArea.show()
            }
            isPlaying = !isPlaying
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
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    return when (item?.itemId) {
                        1 -> {
                            listener.onRenameClicked(file = currentFile)
                            true
                        }
                        2 -> {
                            listener.onDeleteClicked(currentFile)
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
        fun onPlay(file: File)
        fun onPause()
        fun onResume()
        fun onStop()
        fun onSkipForward()
        fun onSkipBackward()
        fun onRenameClicked(file: File?)
        fun onDeleteClicked(file: File?)
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun submitList(voiceNotes: ArrayList<File>) {
        differ.submitList(voiceNotes)
        itemsFilter = ArrayList(voiceNotes)
    }

    internal fun getItems() = differ.currentList

    override fun onBindViewHolder(holder: VoiceNotesAdapter.VoiceNoteViewHolder, position: Int) {
        val voiceNote = differ.currentList[position]
        holder.bind(voiceNote)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
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
            differ.submitList((results.values as ArrayList<File>?)!!)
        }
    }
}
