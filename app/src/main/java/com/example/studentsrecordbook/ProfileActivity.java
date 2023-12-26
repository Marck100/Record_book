package com.example.studentsrecordbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.marks) {
                startActivity(new Intent(getApplicationContext(), MarksActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                return true;
            }
            return false;
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame_layout, new SettingsFragment())
                .commit();
    }
}