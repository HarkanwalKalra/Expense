package com.example.android.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener, StatsFragment.OnFragmentInteractionListener {

    private BottomNavigationView navigation;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTheme();
        setContentView(R.layout.main_activity);

        makeBottomNavBar();

    }

    @Override
    public void onBackPressed() {
        Fragment frag = manager.findFragmentById(R.id.frame_layout);

        if (frag instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            manager.beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("Theme", "0");
        switch (theme) {
            case "0": {
                int themeId = R.style.LightTheme;
                setTheme(themeId);
                break;
            }
            case "1": {
                int themeId = R.style.DarkTheme;
                setTheme(themeId);
                break;
            }
            default:
                Toast.makeText(this, "No theme", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void makeBottomNavBar() {
        manager = getSupportFragmentManager();
        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        manager.beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_history:
                        manager.beginTransaction().replace(R.id.frame_layout, new HistoryFragment()).commit();
                        return true;
                    case R.id.navigation_home:
                        manager.beginTransaction().replace(R.id.frame_layout, new HomeFragment(), "Home").commit();
                        return true;
                    case R.id.navigation_stats:
                        manager.beginTransaction().replace(R.id.frame_layout, new StatsFragment()).commit();
                        return true;
                    case R.id.navigation_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
