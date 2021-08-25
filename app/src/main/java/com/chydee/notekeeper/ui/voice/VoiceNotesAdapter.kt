package com.chydee.notekeeper.ui.voice

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.databinding.ItemVoiceNoteBinding
import com.chydee.notekeeper.utils.ext.remove
import com.chydee.notekeeper.utils.ext.show
import java.io.File

class VoiceNotesAdapter : RecyclerView.Adapter<VoiceNotesAdapter.VoiceNoteViewHolder>() {

    private var isPlaying: Boolean = false

    private var notes: ArrayList<File> = arrayListOf()

    inner class DiffCallback(private val oldList: List<File>, private val newList: List<File>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

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
        val diffCallback = DiffCallback(notes, voiceNotes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.notes.clear()
        this.notes.addAll(voiceNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    internal fun getItems() = notes

    override fun onBindViewHolder(holder: VoiceNotesAdapter.VoiceNoteViewHolder, position: Int) {
        val voiceNote = notes[position]
        holder.bind(voiceNote)
    }

    override fun getItemCount(): Int {
        return notes.size
    }


}
