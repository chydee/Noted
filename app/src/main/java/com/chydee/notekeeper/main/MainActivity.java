package com.chydee.notekeeper.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.chydee.notekeeper.R;
import com.chydee.notekeeper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.mainToolbar);
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getLabel() == "home_fragment")
                    setSupportActionBar(binding.mainToolbar);
                else if (destination.getLabel() == "edit_note_fragment")
                    setSupportActionBar(binding.editNoteToolbar);
                else
                    Log.e(TAG, "onDestinationChanged: " + destination.getLabel());
            }
        });
    }


}
