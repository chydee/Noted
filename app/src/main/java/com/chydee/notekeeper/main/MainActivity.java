package com.chydee.notekeeper.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.chydee.notekeeper.R;
import com.chydee.notekeeper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                if (destination.getId() == controller.getGraph().getStartDestination()) {
                    appBarSetUpForStartDestination(binding);
                } else {
                    appBarSetUpForLastDestination(binding);
                }
            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration);
        //return super.onSupportNavigateUp();
    }

    private void appBarSetUpForStartDestination(ActivityMainBinding binding) {
        binding.editNoteToolbar.setVisibility(View.INVISIBLE);
        binding.mainToolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(binding.mainToolbar);
    }

    private void appBarSetUpForLastDestination(ActivityMainBinding binding) {
        binding.mainToolbar.setVisibility(View.INVISIBLE);
        binding.editNoteToolbar.setVisibility(View.VISIBLE);
        binding.editNoteToolbar.setTitle("");
        setSupportActionBar(binding.editNoteToolbar);
    }
}
