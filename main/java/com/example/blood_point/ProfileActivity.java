package com.example.blood_point;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    TextView name,gender,blood_group,phone_no,email,location,address,donor_text;
    ImageView back,edit;
    FirebaseAuth mAuth;
    FirebaseDatabase db_user;
    FirebaseUser cur_user;
    DatabaseReference db_ref,donor_ref;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profile_name);
        gender = findViewById(R.id.profile_gender);
        blood_group = findViewById(R.id.profile_blood);
        phone_no = findViewById(R.id.profile_phone);
        email = findViewById(R.id.profile_email);
        location = findViewById(R.id.profile_location);
        address = findViewById(R.id.profile_address);
        donor_text = findViewById(R.id.profile_donor);
        back = findViewById(R.id.backarrow);
        edit = findViewById(R.id.edit_profile);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            startActivity(intent);
            finish();
        });
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),UpdateActivity.class);
            startActivity(intent);
            finish();
        });
        mAuth = FirebaseAuth.getInstance();
        db_user = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        db_ref = db_user.getReference("USERS");
        donor_ref = db_user.getReference("DONORS");

        Query single_user = db_ref.child(cur_user.getUid());
        single_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NAME = "NAME : "+Objects.requireNonNull(dataSnapshot.child("NAME").getValue()).toString();
                String GENDER = "GENDER : "+Objects.requireNonNull(dataSnapshot.child("GENDER").getValue()).toString();
                String BLOOD_GROUP = "BLOOD GROUP : "+Objects.requireNonNull(dataSnapshot.child("BLOOD_GROUP").getValue()).toString();
                String PHONE_NO = "CONTACT : "+Objects.requireNonNull(dataSnapshot.child("PHONE_NO").getValue()).toString();
                String EMAIL = "EMAIL : "+cur_user.getEmail();
                String LOCATION = "LOCATION : "+Objects.requireNonNull(dataSnapshot.child("LOCATION").getValue()).toString();
                String ADDRESS = "ADDRESS : "+Objects.requireNonNull(dataSnapshot.child("ADDRESS").getValue()).toString();
                String loc =Objects.requireNonNull(dataSnapshot.child("LOCATION").getValue()).toString();
                String blood_g=Objects.requireNonNull(dataSnapshot.child("BLOOD_GROUP").getValue()).toString();
                name.setText(NAME);
                gender.setText(GENDER);
                blood_group.setText(BLOOD_GROUP);
                phone_no.setText(PHONE_NO);
                email.setText(EMAIL);
                location.setText(LOCATION);
                address.setText(ADDRESS);
                Query donor = donor_ref.child(loc).child(blood_g).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                donor.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            donor_text.setText("You are officially a donor");
                            donor_text.setTextColor(Color.RED);

                        }
                        else
                            donor_text.setText("You are not a donor");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }
}