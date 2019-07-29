package com.chydee.notekeeper;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter {

    //Create a view holder class
    //This will hold the views in the recyclerView item
    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView mTextViewTitle;
        private TextView mTextViewDescription;
        private TextView mTextViewPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.text_view_title);
            mTextViewDescription = itemView.findViewById(R.id.text_view_description);
            mTextViewPriority = itemView.findViewById(R.id.text_view_priority);
        }
    }
}
