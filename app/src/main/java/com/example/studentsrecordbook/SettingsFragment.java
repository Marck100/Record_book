package com.example.studentsrecordbook;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference logOutPreference = findPreference("log_out");
        if (logOutPreference != null) {
            logOutPreference.setOnPreferenceClickListener(listener -> {
                Activity activity = getActivity();
                if (activity != null) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("loginData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("matricola");
                    editor.remove("password");
                    editor.apply();

                    Intent loginIntent = new Intent(activity, MainActivity.class);
                    activity.startActivity(loginIntent);
                }

                return true;
            });
        }
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
