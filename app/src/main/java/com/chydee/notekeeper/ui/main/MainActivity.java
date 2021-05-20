package com.chydee.notekeeper.ui.main;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.chydee.notekeeper.R;
import com.chydee.notekeeper.databinding.ActivityMainBinding;
import com.chydee.notekeeper.utils.worker.ClearTrashWorker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private ActivityMainBinding binding;

    private AppBarConfiguration appBarConfiguration;

    private NavController navController;

    private MaterialToolbar toolBar;

    private WorkManager workManager;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        workManager = WorkManager.getInstance(getApplicationContext());
        workerClearTrashInTheBackground();
        setUpAppBar();
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.editNoteFragment,
                R.id.trashFragment,
                R.id.settingsFragment
        )
                .setOpenableLayout(binding.mainDrawer)
                .build();
        setUpNavController();
        initNavDrawer();
        greetUser();
        setupSharedPreferences();

        // Listens to the NavController for DestinationChange and
        // Hides or show views based on the destinationID
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.editNoteFragment:
                    binding.burgerMenu.setVisibility(View.GONE);
                    hideWelcomingGroup("");
                    break;
                case R.id.settingsFragment:
                    binding.burgerMenu.setVisibility(View.GONE);
                    hideWelcomingGroup(getString(R.string.settings));
                    break;
                case R.id.trashFragment:
                    hideWelcomingGroup(getString(R.string.trash));
                    break;
                default:
                    showWelcomingGroup();
            }
        });
    }

    /**
     * Hides views for when the currentDestination is set to !homeFragment
     */
    private void hideWelcomingGroup(String title) {
        binding.hello.setVisibility(View.VISIBLE);
        binding.hello.setText(title);
        binding.greetings.setVisibility(View.GONE);
    }

    /**
     * Shows views for when the currentDestination is set to homeFragment
     */
    private void showWelcomingGroup() {
        binding.hello.setVisibility(View.VISIBLE);
        binding.hello.setText(getString(R.string.hello));
        binding.greetings.setVisibility(View.VISIBLE);
        binding.burgerMenu.setVisibility(View.VISIBLE);
    }

    //Set up AppBar
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

    //Sets up NevController with ActionBar
    private void setUpNavController() {
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navigationView, navController);
    }

    // Displays a greeting on startScreen based on the current time of the day
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

    //Initialise and set up NavDrawer
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

    // Closes NavDrawer when the @{link SwitchButton} burgerMenu is clicked and isChecked is set to false
    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
        binding.burgerMenu.setChecked(false);
    }

    // Opens NavDrawer when the @{link SwitchButton} burgerMenu is clicked and isChecked is set to true
    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
        binding.burgerMenu.setChecked(true);
    }

    // Set up SharedPref
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        loadThemeFromPreference(sharedPreferences);
    }

    // Loads Theme based onn the values in the SharedPreference
    private void loadThemeFromPreference(SharedPreferences sharedPreferences) {
        changeAppTheme(sharedPreferences.getString(getString(R.string.pref_theme_key), getString(R.string.mode_system)));
    }

    /**
     * Changes the App's Theme based on
     *
     * @param theme is a String value of the AppCompatDelegate.<Mode>
     *              then gets the Delegate and applyDayNight() theme.
     */
    private void changeAppTheme(String theme) {
        Timber.d(theme);
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

    /**
     * change the status bar color programmatically (and provided the device has Android 5.0)
     * then you can use Window.setStatusBarColor().
     * It shouldn't make a difference whether the activity is derived from Activity or
     * ActionBarActivity.
     */
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * creates and enqueues a work in the background using the WorkManager.
     * clears the trash every 7 days intervals
     */
    private void workerClearTrashInTheBackground() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(false)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                ClearTrashWorker.class, 7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();
        workManager.enqueue(periodicWorkRequest);
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


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {
            case R.id.voiceNotesFragment:
                navController.navigate(R.id.voiceNotesFragment);
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
            Timber.d("Error loading theme");
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
        binding = null;
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
