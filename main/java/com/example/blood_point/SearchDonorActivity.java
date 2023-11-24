package com.example.blood_point;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchDonorActivity extends AppCompatActivity {
    ImageView back;
    AutoCompleteTextView bg, loc;
    Button search;
    static String bloodGroup, location;

    public static String getBg() {
        return bloodGroup;
    }

    public static String getLoc() {
        return location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donor);

        back = findViewById(R.id.backarrow);
        bg = findViewById(R.id.search_blood_group);
        loc = findViewById(R.id.search_location);
        search = findViewById(R.id.search_button);

        back.setOnClickListener(v -> startActivity(new Intent(this, Dashboard.class)));

        // Set up the blood group autocomplete
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.blood_groups));
        bg.setAdapter(bloodGroupAdapter);

        // Set up the location autocomplete
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.locations));
        loc.setAdapter(locationAdapter);

        search.setOnClickListener(v -> {
            if (bg.getText().length() == 0) {
                bg.setError("Enter your Blood group!");
            } else if (loc.getText().length() == 0) {
                loc.setError("Enter your Location!");
            } else {
                // Assuming donor_ref is a DatabaseReference instance
                // Replace with your actual logic
                // Query searched = donor_ref.child(location).child(bloodGroup);
                startActivity(new Intent(this, SearchDashboard.class));
            }
            bloodGroup = bg.getText().toString();
            location = loc.getText().toString();
        });
    }
}
