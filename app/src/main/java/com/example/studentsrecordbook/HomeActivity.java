package com.example.studentsrecordbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class HomeActivity extends AppCompatActivity implements ExamAdapterListener {

    DBHandler dbHandler;
    User user;
    List<Exam> exams = new ArrayList<>();

    TextView meanLabel;
    TextView weightedMeanLabel;
    TextView expectedMarkLabel;
    ListView examListView;
    TextView seeMoreLabel;

    SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHandler = new DBHandler(this);

        SharedPreferences preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        if (preferences.contains("matricola") && preferences.contains("password")) {
            String matricola = preferences.getString("matricola", "");
            String password = preferences.getString("password", "");

            Optional<User> user = dbHandler.loadUser(matricola, password);
            user.ifPresent(u -> {
                this.user = u;
            });

            user.orElseGet(() -> {
                Intent loginIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(loginIntent);
                return null;
            });

        };

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        examListView = findViewById(R.id.marks_list);

        exams = dbHandler.loadUserExams(user.getMatricola());
        if (exams.size() > 2) {
            exams = exams.subList(0, 2);
        }


        ExamAdapter examAdapter = new ExamAdapter(this, new ArrayList<>(exams), false, this);
        examListView = findViewById(R.id.marks_list);
        examListView.setAdapter(examAdapter);
        examListView.setDivider(null);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.marks) {
                startActivity(new Intent(getApplicationContext(), MarksActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (item.getItemId() == R.id.home) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });

        meanLabel = findViewById(R.id.mean_label);
        weightedMeanLabel = findViewById(R.id.weighted_mean_label);
        expectedMarkLabel = findViewById(R.id.expected_mark_label);

        seeMoreLabel = findViewById(R.id.see_more_label);
        seeMoreLabel.setOnClickListener(listener -> {
            bottomNavigationView.setSelectedItemId(R.id.marks);
        });

        computeStatistics();
    }

    private void computeStatistics() {

        if (exams.size() == 0) {
            meanLabel.setText(R.string.not_available);
            weightedMeanLabel.setText(R.string.not_available);
            expectedMarkLabel.setText(R.string.not_available);

            return;
        }

        boolean countLodeGreater = settingsPreferences.getBoolean("lode", true);

        List<Integer> marks = new ArrayList<>();
        List<Integer> credits = new ArrayList<>();

        for (Exam exam: exams) {
            if (!countLodeGreater) {
                int mark = exam.getMark();
                if (mark == 31) {
                    mark = 30;
                }
                marks.add(mark);
            } else {
                marks.add(exam.getMark());
            }
            credits.add(exam.getCfu());
        }
        // Mean
        int sum = marks.stream().reduce(0, Integer::sum);
        float mean = (float) sum /marks.size();
        String meanLiteral = String.format(Locale.ITALY, "%.2f", mean);

        // Weighted mean
        int numerator = 0;
        for (int i = 0; i < marks.size(); i++) {
            numerator += marks.get(i) * credits.get(i);
        }
        int denominator = credits.stream().reduce(0, Integer::sum);

        float weightedMean = (float) numerator/denominator;
        String weightedMeanLiteral = String.format(Locale.ITALY, "%.2f", weightedMean);

        // Expected mark
        float expectedMark = weightedMean * 110 / 30;
        String expectedMarkLiteral = String.format(Locale.ITALY, "%.0f", expectedMark);

        meanLabel.setText(meanLiteral);
        weightedMeanLabel.setText(weightedMeanLiteral);
        expectedMarkLabel.setText(expectedMarkLiteral);

    }

    @Override
    public void examDeletionRequested(int position) {
    }
}