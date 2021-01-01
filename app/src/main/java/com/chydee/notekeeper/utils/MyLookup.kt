package com.chydee.notekeeper.utils

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.chydee.notekeeper.ui.home.NoteAdapter

class MyLookup(private val recyclerView: RecyclerView, private val callingClassID: Int) :
        ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetailsLookup.ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        return if (view != null && callingClassID == 1) {
            (recyclerView.getChildViewHolder(view) as NoteAdapter.NoteViewHolder).getItemDetails()
            /* } else if (view != null && callingClassID == 2) {
                 (recyclerView.getChildViewHolder(view) as VoiceNotesAdapter.ViewHolder).getItemDetails()*/
        } else null
    }
}