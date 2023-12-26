package com.example.studentsrecordbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    EditText matricolaField;
    EditText passwordField;
    MaterialButton loginButton;
    TextView signupTextView;

    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(MainActivity.this);

        SharedPreferences settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        if (preferences.contains("matricola") && preferences.contains("password")) {
            String matricola = preferences.getString("matricola", "");
            String password = preferences.getString("password", "");

            Optional<User> user = dbHandler.loadUser(matricola, password);
            user.ifPresent(u -> {

                String defaultInitialView = settingsPreferences.getString("view_preference", "");
                if (defaultInitialView.equals(getString(R.string.login_view_exams))) {
                    Intent examsActivity = new Intent(MainActivity.this, MarksActivity.class);
                    startActivity(examsActivity);
                } else {
                    Intent homeActivity = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(homeActivity);
                }
            });
        }

        setContentView(R.layout.activity_main);

        matricolaField = findViewById(R.id.matricolaField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.login_button);
        signupTextView = findViewById(R.id.signup_textview);

        styleSignupTextView();

        signupTextView.setOnClickListener(listener -> {
            Intent signupIntent = new Intent(this, SignupActivity.class);
            startActivity(signupIntent);
        });

    }

    private void styleSignupTextView() {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString("Don't you have an account? ");
        builder.append(str1);

        SpannableString str2= new SpannableString("Signup here");
        str2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.tint)), 0, str2.length(), 0);
        builder.append(str2);

        signupTextView.setText(builder, TextView.BufferType.SPANNABLE);
        loginButton.setOnClickListener(listener -> performLogin());
    }

    private void performLogin() {

        Intent tabBarActivity = new Intent(this, HomeActivity.class);

        if (validateInputs()) {
            String matricola = matricolaField.getText().toString();
            String password = passwordField.getText().toString();

            Optional<User> user = dbHandler.loadUser(matricola, password);
            user.ifPresent(u -> {

                SharedPreferences preferences = getSharedPreferences("loginData", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("matricola", matricola);
                editor.putString("password", password);
                editor.apply();

                startActivity(tabBarActivity);
            });

            user.orElseGet(() -> {
                Toast.makeText(getApplicationContext(), "User do not exists. Check your credentials and try again.", Toast.LENGTH_SHORT).show();
                return null;
            });
        }
    }

    private boolean validateInputs() {

        boolean isValid = true;

        String matricola = matricolaField.getText().toString();
        String password = passwordField.getText().toString();

        if (matricola.isEmpty()) {
            matricolaField.setError("Matricola is required.");
            isValid = false;
        } else {
            matricolaField.setError(null);
        }

        if (password.isEmpty()) {
            passwordField.setError("Password is required.");
            isValid = false;
        } else {
            passwordField.setError(null);
        }

        return isValid;
    }
}