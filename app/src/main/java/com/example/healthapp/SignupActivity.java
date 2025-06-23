package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText fullNameInput, emailInput, phoneInput, passwordInput;
    private Button signupBtn;
    private ProgressBar progressBar;
    private TextView goToLoginText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        fullNameInput = findViewById(R.id.fullname);
        emailInput = findViewById(R.id.email);
        phoneInput = findViewById(R.id.phone);
        passwordInput = findViewById(R.id.pass);
        signupBtn = findViewById(R.id.signupbtn);
        progressBar = findViewById(R.id.pgbar);
        goToLoginText = findViewById(R.id.gt_login);

        signupBtn.setOnClickListener(v -> {
            String name = fullNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!validateInputs(name, email, phone, password)) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setEnabled(false);

            // Create user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        signupBtn.setEnabled(true);

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this,
                                                        "Signup successful! Please verify your email before logging in.",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(SignupActivity.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            // Navigate to Login screen anyway
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                            finish();
                                        });
                            }
                        } else {
                            Toast.makeText(SignupActivity.this,
                                    "Signup failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        goToLoginText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String phone, String password) {
        if (TextUtils.isEmpty(name)) {
            fullNameInput.setError("Full name is required");
            fullNameInput.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid email is required");
            emailInput.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            phoneInput.setError("Valid phone number is required");
            phoneInput.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }
        return true;
    }
}
