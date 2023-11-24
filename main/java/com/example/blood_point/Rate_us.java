package com.example.blood_point;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

public class Rate_us extends AppCompatActivity {
    ImageView back;
    Button rate;
    RatingBar rBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);

        back = findViewById(R.id.backarrow);
        rate = findViewById(R.id.rate_btn);
        rBar = findViewById(R.id.ratingBar);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            startActivity(intent);
            finish();
        });
        rate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Thank you for rating --> "+rBar.getRating()+" stars !")
                    .setCancelable(false)
                    .setPositiveButton("ok", (dialog, id) -> {
                        dialog.cancel();
                        finish();
                        startActivity(new Intent(this,Dashboard.class));
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }
}