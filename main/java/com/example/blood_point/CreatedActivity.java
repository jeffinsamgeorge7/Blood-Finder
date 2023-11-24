package com.example.blood_point;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreatedActivity extends AppCompatActivity {
    Button created;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created);

        created = findViewById(R.id.input_continue);
        created.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Welcome, your account has been created!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });
    }
}