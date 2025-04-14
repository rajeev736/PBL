package com.example.pbl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Save user credentials and name to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", name);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Toast.makeText(SignupActivity.this, "Signup successful! Please login.", Toast.LENGTH_SHORT).show();

                // Navigate to LoginActivity
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });
        TextView loginRedirectText = findViewById(R.id.goToLogin);
        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish(); // Optional
        });

    }
}
