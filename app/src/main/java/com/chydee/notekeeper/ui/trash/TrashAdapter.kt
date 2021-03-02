package com.chydee.notekeeper.ui.trash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.data.model.Trash
import com.chydee.notekeeper.databinding.ItemTrashBinding

class TrashAdapter : RecyclerView.Adapter<TrashAdapter.TrashViewHolder>() {


    private lateinit var listener: OnItemClickListener

    var trashes: ArrayList<Trash> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        return TrashViewHolder(ItemTrashBinding.inflate(LayoutInflater.from(parent.context)))
    }

    inner class TrashViewHolder(private var binding: ItemTrashBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trash: Trash) {
            c
            binding.trash = trash
            if (trash.color != -1) {
                binding.noteCard.setCardBackgroundColor(trash.color)
            }
            binding.executePendingBindings()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class DiffCallback(private val oldList: List<Trash>, private val newList: List<Trash>) : DiffUtil.Callback() {

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
        fun onTrashClick(trash: Trash)
    }

    internal fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun updateTrash(newItems: List<Trash>) {
        val diffCallback = DiffCallback(trashes, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.trashes.clear()
        this.trashes.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val trash = trashes[position]

        holder.bind(trash)
        holder.itemView.setOnClickListener {
            listener.onTrashClick(trash)
        }
    }

    override fun getItemCount(): Int {
        return trashes.size
    }

}
