package com.chydee.notekeeper.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.data.model.Color
import com.chydee.notekeeper.databinding.ItemColorBinding

class ColorsAdapter : ListAdapter<Color, ColorsAdapter.MyViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onColorSelected(color: Color)
    }

    private lateinit var listener: OnItemClickListener


    inner class MyViewHolder(val binding: ItemColorBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(color: Color) {
            with(binding) {
                colorCard.setOnClickListener {
                    listener.onColorSelected(color)
                }
                colorCard.setCardBackgroundColor(color.colorRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return from(parent)
    }

    private fun from(parent: ViewGroup): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemColorBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val color = getItem(position)
        holder.bind(color)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Color>() {
        override fun areContentsTheSame(oldItem: Color, newItem: Color): Boolean {
            return oldItem.color == newItem.color
        }

        override fun areItemsTheSame(oldItem: Color, newItem: Color): Boolean {
            return oldItem == newItem
        }
    }


    internal fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}