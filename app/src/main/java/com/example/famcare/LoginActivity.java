package com.example.famcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView createAccount;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();


        emailEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextText2);
        loginButton = findViewById(R.id.loginButton);
        createAccount = findViewById(R.id.createAccountText);


        TextView appName = findViewById(R.id.appName);
        SpannableString spannable = new SpannableString("FamCare");
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        appName.setText(spannable);


        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{7,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(this, "Password must have:\n• 7+ chars\n• 1 uppercase\n• 1 number\n• 1 symbol", Toast.LENGTH_LONG).show();
                return;
            }


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();

                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                        }
                    });
        });


        createAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpPage.class));
        });
    }
}
