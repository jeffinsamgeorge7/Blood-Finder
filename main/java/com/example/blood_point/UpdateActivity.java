package com.example.blood_point;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

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

public class UpdateActivity extends AppCompatActivity {
    EditText name, gender, phone_no, address, location, blood_group;
    CheckBox Donor;
    Button update;
    ImageView back;
    FirebaseAuth mAuth;
    FirebaseDatabase db_user;
    FirebaseUser cur_user;
    DatabaseReference db_ref, donor_ref, dd_ref;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        name = findViewById(R.id.update_name);
        gender = findViewById(R.id.update_gender);
        phone_no = findViewById(R.id.update_phoneno);
        address = findViewById(R.id.update_address);
        location = findViewById(R.id.update_location);
        blood_group = findViewById(R.id.update_blood_group);
        Donor = findViewById(R.id.update_donor);
        update = findViewById(R.id.update_account);
        back = findViewById(R.id.backarrow);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        });
        mAuth = FirebaseAuth.getInstance();
        db_user = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        db_ref = db_user.getReference("USERS");
        donor_ref = db_user.getReference("DONORS");
        dd_ref = db_user.getReference("donor_details");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Query single_user = db_ref.child(cur_user.getUid());
        single_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_data data = dataSnapshot.getValue(user_data.class);
                assert data != null;
                String NAME = Objects.requireNonNull(dataSnapshot.child("NAME").getValue()).toString();
                String GENDER = Objects.requireNonNull(dataSnapshot.child("GENDER").getValue()).toString();
                String PHONE_NO = Objects.requireNonNull(dataSnapshot.child("PHONE_NO").getValue()).toString();
                String ADDRESS = Objects.requireNonNull(dataSnapshot.child("ADDRESS").getValue()).toString();
                String LOCATION = Objects.requireNonNull(dataSnapshot.child("LOCATION").getValue()).toString();
                String BLOOD_GROUP = Objects.requireNonNull(dataSnapshot.child("BLOOD_GROUP").getValue()).toString();

                name.setText(NAME);
                gender.setText(GENDER);
                phone_no.setText(PHONE_NO);
                address.setText(ADDRESS);
                location.setText(LOCATION);
                blood_group.setText(BLOOD_GROUP);

                Query donor = donor_ref.child(LOCATION).child(BLOOD_GROUP)
                        .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                donor.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.exists()) {
                                Donor.setChecked(true);
                                Donor.setText("UNCHECK IF YOU WANNA LEAVE FROM DONORS");
                            } else {
                                Donor.setText("IF YOU WANNA REGISTER AS A DONOR");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });

                update.setOnClickListener(v -> {
                    // Request a single location update when the user clicks the update button
                    try {
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        // Initialize location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Update latitude and longitude in the database
                updateLocationInDatabase(id, location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    // Add this method to update latitude and longitude in the database
    private void updateLocationInDatabase(String userId, double latitude, double longitude) {
        // Update latitude and longitude in the database
        db_ref.child(userId).child("LATITUDE").setValue(latitude);
        db_ref.child(userId).child("LONGITUDE").setValue(longitude);
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove location updates when the activity is destroyed
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
