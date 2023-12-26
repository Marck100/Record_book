package com.example.studentsrecordbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText matricolaField;

    EditText fullnameField;
    EditText birthdayField;
    EditText passwordField;
    MaterialButton signupButton;
    TextView loginTextView;

    Calendar birthday;

    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHandler = new DBHandler(SignupActivity.this);

        matricolaField = findViewById(R.id.matricolaField);
        fullnameField = findViewById(R.id.fullnameField);
        birthdayField = findViewById(R.id.birthdayField);
        passwordField = findViewById(R.id.passwordField);
        signupButton = findViewById(R.id.signup_button);
        loginTextView = findViewById(R.id.login_textview);

        styleLoginTextView();

        loginTextView.setOnClickListener(listener -> {
            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
        });

        birthdayField.setInputType(InputType.TYPE_NULL);
        birthdayField.setKeyListener(null);

        birthdayField.setOnClickListener(listener -> {
            DatePickerFragment dateFragment = new DatePickerFragment(this::onDateSet);
            dateFragment.show(getSupportFragmentManager(), "datePicker");
        });

        birthday = GregorianCalendar.getInstance(Locale.ITALY);

        signupButton.setOnClickListener(listener -> {
            performSignup();
        });

    }

    private void styleLoginTextView() {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString("Already have an account? ");
        builder.append(str1);

        SpannableString str2= new SpannableString("Login");
        str2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.tint)), 0, str2.length(), 0);
        builder.append(str2);

        loginTextView.setText(builder, TextView.BufferType.SPANNABLE);
    }

    private void updateBirthdayFieldUI() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        String literalDate = formatter.format(birthday.getTime());

        birthdayField.setText(literalDate);
    }

    private void onDateSet(DatePicker view, int year, int month, int day) {
        birthday.set(Calendar.YEAR, year);
        birthday.set(Calendar.MONTH, month);
        birthday.set(Calendar.DATE, day);

        updateBirthdayFieldUI();
    }

    private void performSignup() {
        Optional<User> user = validateInputs();
        Intent loginActivity = new Intent(this, MainActivity.class);

        user.ifPresent(u -> {
            dbHandler.addNewUser(u);
            Toast.makeText(getApplicationContext(), "User has been created successfully. Login using the same credentials.", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(loginActivity), 800);
        });

    }

    private Optional<User> validateInputs() {

        boolean validInputs = true;

        String password = passwordField.getText().toString();
        // Password
        Pattern passwordPattern = Pattern.compile("[A-Z]|[a-z]|[0-9]");
        if (passwordPattern.matcher(password).find() && password.length() >= 8) {
            passwordField.setError(null);
        } else {
            passwordField.setError(
                    "Provide a valid password. " +
                    "Password must include at least a lowercase letter, an uppercase letter and " +
                    "a digit. Password length must be greater or equal than 8"
            );
            validInputs = false;
        }

        // Full name
        String fullName = fullnameField.getText().toString();
        if (fullName.isEmpty()) {
            fullnameField.setError("Full name is required");
            validInputs = false;
        } else {
            fullnameField.setError(null);
        }

        // Birthday
        Calendar now = GregorianCalendar.getInstance(Locale.ITALY);
        int years = now.get(Calendar.YEAR) -  birthday.get(Calendar.YEAR);
        if (years < 18) {
            birthdayField.setError("You must be 18 to enroll");
            validInputs = false;
        } else {
            birthdayField.setError(null);
        }

        // Matricola
        String matricola = matricolaField.getText().toString();
        if (matricola.isEmpty()) {
            matricolaField.setError("Matricola is required");
            validInputs = false;
        } else {
            matricolaField.setError(null);
        }

        if (validInputs) {
            User user = new User(fullName, matricola, password, birthday);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }

    }
}