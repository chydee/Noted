package com.chydee.notekeeper.ui.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.chydee.notekeeper.R;
import com.chydee.notekeeper.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private ActivityMainBinding binding;

    private AppBarConfiguration appBarConfiguration;
    private NavController mNavController;

    private NavController navController;

    private MaterialToolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpAppBar();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.editNoteFragment
        )
                .setDrawerLayout(binding.mainDrawer)
                .build();
        setUpNavController();
        initNavDrawer();
        greetUser();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if (destination.getId() == R.id.editNoteFragment) {
                binding.hello.setVisibility(View.GONE);
                binding.greetings.setVisibility(View.GONE);
                binding.burgerMenu.setVisibility(View.GONE);
            } else {
                binding.hello.setVisibility(View.VISIBLE);
                binding.greetings.setVisibility(View.VISIBLE);
                binding.burgerMenu.setVisibility(View.VISIBLE);
            }
        });

        this.toolBar.setNavigationIcon(null);

    }

    private void setUpAppBar() {
        toolBar = binding.getRoot().findViewById(R.id.topAppBar);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            toolBar.setNavigationIcon(R.drawable.ic_up);
        }

        binding.burgerMenu.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    closeDrawer();
                } else {
                    openDrawer();
                }
            }
        }));
    }

    private void setUpNavController() {
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    private void greetUser() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.HOUR_OF_DAY) <= 11) {
            binding.greetings.setText(getString(R.string.good_morning));
        } else if (c.get(Calendar.HOUR_OF_DAY) > 11 && c.get(Calendar.HOUR_OF_DAY) <= 15) {
            binding.greetings.setText(getString(R.string.good_afternoon));
        } else if (c.get(Calendar.HOUR_OF_DAY) > 15 && c.get(Calendar.HOUR_OF_DAY) <= 20) {
            binding.greetings.setText(getString(R.string.good_evening));
        } else if (c.get(Calendar.HOUR_OF_DAY) > 20) {
            binding.greetings.setText(getString(R.string.good_night));
        } else {
            binding.greetings.setText(getString(R.string.good_day));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void initNavDrawer() {
        drawerLayout = findViewById(R.id.mainDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.closeDrawer(GravityCompat.START);
                binding.burgerMenu.setChecked(false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerLayout.openDrawer(GravityCompat.START);
                binding.burgerMenu.setChecked(true);
            }
        };
        toggle.setDrawerSlideAnimationEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toolBar.setNavigationIcon(null);
    }


    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
        binding.burgerMenu.setChecked(false);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
        binding.burgerMenu.setChecked(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.navigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.
        }
        return false;
    }
}
