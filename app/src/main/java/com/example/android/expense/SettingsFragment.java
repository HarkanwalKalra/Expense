package com.example.android.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat {

    private int oldTheme, newTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();

        addPreferencesFromResource(R.xml.pref_main);

        Preference about = findPreference(getString(R.string.about));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            }
        });
        Preference help =  findPreference(getString(R.string.help));
        help.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
                return false;
            }
        });
        ListPreference mListPreference = (ListPreference) getPreferenceManager().findPreference("Theme");
        mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();

                if (preference instanceof ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                            index >= 0
                                    ? listPreference.getEntries()[index]
                                    : null);

                    newTheme = index;
                    if (oldTheme != newTheme) {
                        ((SettingsActivity)getActivity()).changeTheme(newTheme);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        oldTheme = Integer.parseInt(sharedPreferences.getString("Theme", "0"));
        if (oldTheme == 0) {
            int themeId = R.style.LightTheme;
            getActivity().setTheme(themeId);
        } else if (oldTheme == 1) {
            int themeId = R.style.DarkTheme;
            getActivity().setTheme(themeId);
        } else {
            Toast.makeText(getContext(), "No theme", Toast.LENGTH_SHORT).show();
        }
    }

}