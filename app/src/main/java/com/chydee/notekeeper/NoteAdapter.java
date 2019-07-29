package com.chydee.notekeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private List<Note> mNotes = new ArrayList<>();

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

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
