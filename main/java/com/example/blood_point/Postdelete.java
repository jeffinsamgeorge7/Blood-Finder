package com.example.blood_point;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Postdelete extends AppCompatActivity {
    Button deleted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdelete);

        deleted = findViewById(R.id.btn_delete_done);
        deleted.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });
    }
}