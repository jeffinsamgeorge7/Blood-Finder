package com.example.blood_point;

import static com.example.blood_point.R.style.comic_sans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAnalytics firebaseAnalytics;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton Post, Search;
    TextView name_blood_header, email_header;
    FirebaseAuth mAuth;
    FirebaseDatabase db_user;
    FirebaseUser cur_user;
    DatabaseReference db_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        Post = findViewById(R.id.post);
        Search = findViewById(R.id.search);
        Search.setOnClickListener(v -> startActivity(new Intent(this, SearchDonorActivity.class)));
        Post.setOnClickListener(v -> startActivity(new Intent(this, PostActivity.class)));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        toolbar.setTitleTextAppearance(this, comic_sans);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        View header = navigationView.getHeaderView(0);
        name_blood_header = header.findViewById(R.id.text_name_blood);
        email_header = header.findViewById(R.id.text_email_id);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        db_user = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        db_ref = db_user.getReference("USERS");

        Query single_user = db_ref.child(cur_user.getUid());
        single_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_data data = dataSnapshot.getValue(user_data.class);
                assert data != null;
                String name = Objects.requireNonNull(dataSnapshot.child("NAME").getValue()).toString();
                String blood = Objects.requireNonNull(dataSnapshot.child("BLOOD_GROUP").getValue()).toString();
                String header1 = "Welcome "+name;
                String header2 = "";
                name_blood_header.setText(header1);
                email_header.setText(header2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);

        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to exit ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
                    toolbar.setTitleTextAppearance(this, comic_sans);
                    Post.show();
                    Search.show();
                    break;
                case R.id.nav_profile:
                    startActivity(new Intent(this, ProfileActivity.class));
                    break;
                case R.id.nav_post:
                    startActivity(new Intent(this, MyBloodpost.class));
                    break;
                case R.id.nav_google_map:
                    DatabaseReference userRef = db_ref.child(cur_user.getUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Double latitude = dataSnapshot.child("LATITUDE").getValue(Double.class);
                                Double longitude = dataSnapshot.child("LONGITUDE").getValue(Double.class);

                                if (latitude != null && longitude != null) {
                                    // Create an Intent to open Google Maps
                                    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?z=15");
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");

                                    // Check if the Google Maps app is installed
                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    } else {
                                        // Google Maps app is not installed, show a message to the user
                                        Toast.makeText(Dashboard.this, "Google Maps app not installed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle the case where latitude or longitude is null
                                    Toast.makeText(Dashboard.this, "Latitude or Longitude is null", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle the case where user data doesn't exist
                                Toast.makeText(Dashboard.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                            Toast.makeText(Dashboard.this, "Error fetching user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;




                case R.id.nav_log_out:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Do you want to log out ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                mAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel()).create().show();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}