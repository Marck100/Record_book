package com.example.studentsrecordbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MarksActivity extends AppCompatActivity implements UpdateListener {

    MaterialToolbar topToolbar;
    DBHandler dbHandler;

    ListView examListView;

    User user;
    List<Exam> exams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        dbHandler = new DBHandler(MarksActivity.this);

        SharedPreferences preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        if (preferences.contains("matricola") && preferences.contains("password")) {
            String matricola = preferences.getString("matricola", "");
            String password = preferences.getString("password", "");

            Optional<User> user = dbHandler.loadUser(matricola, password);
            user.ifPresent(u -> {
                this.user = u;
            });

            user.orElseGet(() -> {
                Intent loginIntent = new Intent(MarksActivity.this, MainActivity.class);
                startActivity(loginIntent);
                return null;
            });

        };

        exams = dbHandler.loadUserExams(user.getMatricola());

        ExamAdapter examAdapter = new ExamAdapter(this, new ArrayList<>(exams));
        examListView = findViewById(R.id.marks_list);
        examListView.setAdapter(examAdapter);
        examListView.setDivider(null);

        topToolbar = findViewById(R.id.marks_toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.marks);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.marks) {
                return true;
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });

        examListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Exam exam = exams.get(i);
                Bundle items = new Bundle();
                items.putSerializable("user", user);
                items.putSerializable("exam", exam);
                ExamDialogFragment dialog = new ExamDialogFragment(MarksActivity.this);
                dialog.setArguments(items);

                dialog.show(getSupportFragmentManager(), "exam_alert");
            }
        });

        topToolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_add) {

                Intent addMarkActivity = new Intent(MarksActivity.this, AddMarkActivity.class);
                startActivity(addMarkActivity);

                return true;
            }
            return false;
        });
    }

    @Override
    public void callback(View view) {
        exams = dbHandler.loadUserExams(user.getMatricola());
        ExamAdapter examAdapter = new ExamAdapter(this, new ArrayList<>(exams));
        examListView.setAdapter(examAdapter);
        examListView.invalidateViews();
        examListView.refreshDrawableState();

    }
}