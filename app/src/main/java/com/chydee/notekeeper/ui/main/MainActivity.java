package com.chydee.notekeeper.ui.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.chydee.notekeeper.R;
import com.chydee.notekeeper.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private ActivityMainBinding binding;

    private AppBarConfiguration appBarConfiguration;

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
                R.id.editNoteFragment,
                R.id.trashFragment,
                R.id.aboutFragment,
                R.id.settingsFragment
        )
                .setOpenableLayout(binding.mainDrawer)
                .build();
        setUpNavController();
        initNavDrawer();
        greetUser();
        setupSharedPreferences();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.settingsFragment:
                    binding.burgerMenu.setVisibility(View.GONE);
                    hideWelcomingGroup(getString(R.string.settings));
                    break;
                case R.id.editNoteFragment:
                    binding.burgerMenu.setVisibility(View.GONE);
                    hideWelcomingGroup("");
                    break;
                case R.id.trashFragment:
                    hideWelcomingGroup(getString(R.string.trash));
                    break;
                case R.id.aboutFragment:
                    hideWelcomingGroup(getString(R.string.about));
                    break;
                default:
                    showWelcomingGroup();
            }
        });
    }


    private void hideWelcomingGroup(String title) {
        binding.hello.setVisibility(View.VISIBLE);
        binding.hello.setText(title);
        binding.greetings.setVisibility(View.GONE);
    }

    private void showWelcomingGroup() {
        binding.hello.setVisibility(View.VISIBLE);
        binding.hello.setText(getString(R.string.hello));
        binding.greetings.setVisibility(View.VISIBLE);
        binding.burgerMenu.setVisibility(View.VISIBLE);
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
        NavigationUI.setupWithNavController(binding.navigationView, navController);
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

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        loadThemeFromPreference(sharedPreferences);
    }

    private void loadThemeFromPreference(SharedPreferences sharedPreferences) {
        changeAppTheme(sharedPreferences.getString(getString(R.string.pref_theme_key), getString(R.string.mode_system)));
    }

    // Method to set Color of Text.
    private void changeAppTheme(String theme) {
        Log.d("Noted", theme);
        switch (theme) {
            case "MODE_NIGHT_YES":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getDelegate().applyDayNight();
                break;
            case "MODE_NIGHT_NO":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getDelegate().applyDayNight();
                break;
            case "MODE_NIGHT_AUTO_BATTERY":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                getDelegate().applyDayNight();
                break;
            case "MODE_NIGHT_FOLLOW_SYSTEM":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                getDelegate().applyDayNight();
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);
                getDelegate().applyDayNight();
                break;
        }
    }

    private void checkPermissionAndOpenFragment() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            navController.navigate(R.id.voiceNotesFragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            navController.navigate(R.id.voiceNotesFragment);
        } else {
            Snackbar.make(binding.getRoot(), "You need to give permission", Snackbar.LENGTH_LONG).show();
        }
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
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {
            case R.id.aboutFragment:
                navController.navigate(R.id.aboutFragment);
                break;
            case R.id.voiceNotesFragment:
                checkPermissionAndOpenFragment();
                break;
            case R.id.trashFragment:
                navController.navigate(R.id.trashFragment);
                break;
            case R.id.settingsFragment:
                navController.navigate(R.id.settingsFragment);
                break;
            default:
                navController.navigate(R.id.homeFragment);
                break;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_theme_key))) {
            loadThemeFromPreference(sharedPreferences);
        } else {
            Log.d("Noted", "Error loading theme");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
