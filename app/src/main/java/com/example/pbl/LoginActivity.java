package com.example.pbl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if already logged in
        SharedPreferences loginPrefs = getSharedPreferences("login", MODE_PRIVATE);
        if (loginPrefs.getBoolean("loggedIn", false)) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String inputEmail = emailInput.getText().toString().trim();
            String inputPassword = passwordInput.getText().toString().trim();

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch saved credentials from signup
            SharedPreferences userPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
            String savedEmail = userPrefs.getString("email", "");
            String savedPassword = userPrefs.getString("password", "");

            if (inputEmail.equals(savedEmail) && inputPassword.equals(savedPassword)) {
                // Save login state
                loginPrefs.edit().putBoolean("loggedIn", true).apply();

                // Navigate to Dashboard
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        TextView signupRedirectText = findViewById(R.id.goToSignup);
        signupRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }
}
