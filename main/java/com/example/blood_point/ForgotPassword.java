package com.example.blood_point;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText email;
    Button reset_password;
    ImageView back;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.input_sex);
        reset_password = findViewById(R.id.input_continue);
        back = findViewById(R.id.backarrow);

        mAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        });
        reset_password.setOnClickListener(v -> {
            final String Email = email.getText().toString();
            if(TextUtils.isEmpty(Email))
            {
                Toast.makeText(getApplicationContext(), "Field is Empty", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mAuth.sendPasswordResetEmail(Email)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "We have sent an email to "+Email+".\nPlease check your email.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Sorry, There is something went wrong. please try again some time later.", Toast.LENGTH_SHORT)
                                        .show();
                                email.setText(null);
                            }
                        });
            }
        });
    }
}