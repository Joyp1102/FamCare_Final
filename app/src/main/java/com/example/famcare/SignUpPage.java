package com.example.famcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpPage extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    Button signUpButton;
    TextView backToLogin;
    ImageView passwordInfo;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();


        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signUpButton = findViewById(R.id.signUpButton);
        backToLogin = findViewById(R.id.backToLogin);
        passwordInfo = findViewById(R.id.passwordInfo);


        passwordInfo.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Password Requirements")
                    .setMessage("Password must contain:\n• Minimum 7 characters\n• At least 1 uppercase letter\n• At least 1 number\n• At least 1 symbol (@#$%^&+=!)")
                    .setPositiveButton("OK", null)
                    .show();
        });


        signUpButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();


            if (name.isEmpty()) {
                Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{7,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(this, "Invalid password. Tap ℹ️ to view password rules.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            prefs.edit().putString("name", name).apply();

                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpPage.this, LoginActivity.class));
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(this, "Signup failed: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });


        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpPage.this, LoginActivity.class));
            finish();
        });
    }
}
