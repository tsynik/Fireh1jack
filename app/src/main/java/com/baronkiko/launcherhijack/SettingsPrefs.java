package com.baronkiko.launcherhijack;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsPrefs extends PreferenceFragment
{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        //You can change preference summary programmatically like following.
        android.preference.SwitchPreference preference = (android.preference.SwitchPreference) findPreference("switch");
        preference.setSummaryOff("Switch off state updated from code");
        preference.setSummaryOn("Switch on state updated from code");

        //You can read preference value anywhere in the app like following.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isChecked = sharedPreferences.getBoolean("switch", false);
        Toast.makeText(getActivity(), "isChecked : " + isChecked, Toast.LENGTH_LONG).show();
    }
}
