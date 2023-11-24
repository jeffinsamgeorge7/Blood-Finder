package com.example.blood_point;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button login,reset,signup;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.input_name);
        password = findViewById(R.id.input_confirm_password);
        login = findViewById(R.id.login);
        reset = findViewById(R.id.reset);
        signup = findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
        login.setOnClickListener(v -> {
            try {
                String input_email = email.getText().toString();
                String input_password = password.getText().toString();
                if(input_password.length()>0 && input_email.length()>0) {
                    mAuth.signInWithEmailAndPassword(input_email,input_password)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Failed Check again", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Failed Check again", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
        });

        reset.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
            startActivity(intent);
        });
    }
    @Override
    public void onBackPressed() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to exit ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        finish();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
    }
}