package com.example.studentsrecordbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class AddMarkActivity extends AppCompatActivity {

    MaterialToolbar topToolbar;
    EditText nameField;
    EditText creditsField;
    EditText dateField;
    EditText markField;
    MaterialButton saveButton;
    CheckBox lodeCheckbox;

    Calendar examDate;

    DBHandler dbHandler;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(AddMarkActivity.this);

        SharedPreferences preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        if (preferences.contains("matricola") && preferences.contains("password")) {
            String matricola = preferences.getString("matricola", "");
            String password = preferences.getString("password", "");

            Optional<User> user = dbHandler.loadUser(matricola, password);
            user.ifPresent(u -> {
                this.user = u;
            });

            user.orElseGet(() -> {
                Intent loginIntent = new Intent(AddMarkActivity.this, MainActivity.class);
                startActivity(loginIntent);
                return null;
            });

        };
        setContentView(R.layout.activity_add_mark);

        nameField = findViewById(R.id.examNameField);
        creditsField = findViewById(R.id.cfuField);
        dateField = findViewById(R.id.examDateField);
        markField = findViewById(R.id.examMarkField);
        lodeCheckbox = findViewById(R.id.lode_checkbox);
        saveButton = findViewById(R.id.save_button);

        dateField.setOnClickListener(listener -> {
            DatePickerFragment dateFragment = new DatePickerFragment(this::onDateSet);
            dateFragment.show(getSupportFragmentManager(), "datePicker");
        });

        topToolbar = findViewById(R.id.add_mark_toolbar);

        topToolbar.setNavigationOnClickListener(listener -> {
            Intent marksIntent = new Intent(AddMarkActivity.this, MarksActivity.class);
            startActivity(marksIntent);
        });

        lodeCheckbox.setEnabled(false);

        markField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lodeCheckbox.setChecked(false);
                lodeCheckbox.setEnabled(true);

                String mark = markField.getText().toString();
                if (mark.isEmpty()) {
                    lodeCheckbox.setEnabled(false);
                    return;
                }

                try {
                    int numericMark = Integer.parseInt(mark);

                    if (numericMark != 30) {
                        AddMarkActivity.this.lodeCheckbox.setEnabled(false);
                        return;
                    }

                } catch (Exception e) {
                    AddMarkActivity.this.lodeCheckbox.setEnabled(false);
                    return;
                }

                return;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        saveButton.setOnClickListener(listener -> {
            performSaveExam();
        });

        examDate = GregorianCalendar.getInstance(Locale.ITALY);
    }

    private void updateExamDateFieldUI() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        String literalDate = formatter.format(examDate.getTime());

        dateField.setText(literalDate);
    }
    private void onDateSet(DatePicker view, int year, int month, int day) {
        examDate.set(Calendar.YEAR, year);
        examDate.set(Calendar.MONTH, month);
        examDate.set(Calendar.DATE, day);

        updateExamDateFieldUI();
    }

    private void performSaveExam() {
        Optional<Exam> exam = validateInputs();


        exam.ifPresent(e -> {
            dbHandler.addNewExam(e);
            Toast.makeText(getApplicationContext(), "Exam has been created successfully", Toast.LENGTH_SHORT).show();
            Intent examsActivity = new Intent(AddMarkActivity.this, MarksActivity.class);
            new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(examsActivity), 800);
        });
    }
    private Optional<Exam> validateInputs() {

        boolean validInputs = true;

        // Name
        String name = nameField.getText().toString();
        if (name.isEmpty()) {
            nameField.setError("Exam's name is required");
            validInputs = false;
        } else {
            nameField.setError(null);
        }

        // CFU
        String cfu = creditsField.getText().toString();
        if (cfu.isEmpty()) {
            creditsField.setError("Credits is required");
            validInputs = false;
        } else {
            creditsField.setError(null);
        }

        int numericCFU = 0;

        try {
            numericCFU = Integer.parseInt(cfu);

            if (numericCFU < 1) {
                creditsField.setError("Credits must be a positive number");
                validInputs = false;
            }

        } catch (Exception e) {
            validInputs = false;
            creditsField.setError("Credits must be a valid number");
        }

        // Date
        Calendar now = GregorianCalendar.getInstance(Locale.ITALY);
        long diff = now.getTime().getTime() - examDate.getTime().getTime();
        if (diff < 0) {
            dateField.setError("Provide a valid date. You cannot provide a future date.");
            validInputs = false;
        }

        // Mark
        String mark = markField.getText().toString();
        if (mark.isEmpty()) {
            markField.setError("Mark is required");
            validInputs = false;
        } else {
            markField.setError(null);
        }

        int numericMark = 0;

        try {
            numericMark = Integer.parseInt(mark);

            if (numericMark < 18 || numericMark > 30) {
                markField.setError("Provide a valid mark. Mark must be between 18 and 30");
                validInputs = false;
            }

        } catch (Exception e) {
            validInputs = false;
            markField.setError("Mark must be a valid number");
        }

        if (numericMark == 30 && lodeCheckbox.isChecked()) {
            numericMark = 31;
        }

        if (validInputs) {
            Exam exam = new Exam(user.getMatricola(), name, numericMark, numericCFU, examDate);
            return Optional.of(exam);
        } else {
            return Optional.empty();
        }
    }


}