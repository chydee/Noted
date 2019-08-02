package com.chydee.notekeeper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class AddNoteActivity extends AppCompatActivity {
    //For now I'll retrieve the data from the form via startIntentForResult
    // and later on change it to the normal way
    //Key for the intent Extras:
   // public static final String EXTRA_TITLE = "com.chydee.notekeeper.EXTRA_TITLE";
   // public static final String EXTRA_DESCRIPTION = "com.chydee.notekeeper.EXTRA_DESCRIPTION";
   // public static final String EXTRA_PRIORITY = "com.chydee.notekeeper.EXTRA_PRIORITY";

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private NumberPicker mNumberPickerPriority;

    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mNumberPickerPriority = findViewById(R.id.number_picker_priority);

        mNumberPickerPriority.setMinValue(1);
        mNumberPickerPriority.setMaxValue(10);


        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        //In order to get the little 'x' in the top left corner of the action bar
        //i.e the close o exit activity button,
        // We call :
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Note");// This will just display the "Add Note" text in the action bar
    }

    private void saveNote(){
        //Get inputs from the textFields and the number picker
        String title = mEditTextTitle.getText().toString();
        String description = mEditTextDescription.getText().toString();
        int priority = mNumberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, "Please add a title and a description", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(title, description, priority);
        mNoteViewModel.insert(note);
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        finish();
        /*Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        setResult(RESULT_OK, data);*/
    }

    //So to comfirm the inputs when we click the save icon in the top menu right corner of the action bar
    //First of all we have to get this icon there. We do this by overriding :

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;// Which means that we want to display the menu
    }

    //To handle the clicks on our menu icons or one icons as the case may be,
    //We have to Override onOptionsItemSelected

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //The item.getItemId() helps us to find the item that was clicked on
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
