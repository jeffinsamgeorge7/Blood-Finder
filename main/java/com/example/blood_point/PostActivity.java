package com.example.blood_point;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.util.Calendar;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {
    ImageView back;
    EditText Post_bg,Post_phone_no,Post_address,Post_location;
    Button Post_button;
    Calendar cal;
    String Date,Time;
    String valid_id;
    FirebaseAuth mAuth;
    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    AutoCompleteTextView bg, loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        bg = findViewById(R.id.search_blood_group);
        loc = findViewById(R.id.search_location);
        back = findViewById(R.id.backarrow);
        Post_phone_no = findViewById(R.id.post_phone_no);
        Post_address = findViewById(R.id.post_address);

        Post_button = findViewById(R.id.post_button);
        back.setOnClickListener(v -> startActivity(new Intent(this,Dashboard.class)));
        cal = Calendar.getInstance();
        int Day = cal.get(Calendar.DAY_OF_MONTH);
        int Month = cal.get(Calendar.MONTH);
        int Year = cal.get(Calendar.YEAR);
        int Hour = cal.get(Calendar.HOUR);
        int Min = cal.get(Calendar.MINUTE);
        Month+=1;
        Time = "";
        Date = "";
        String AM_PM="AM";
        // Set up the blood group autocomplete
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.blood_groups));
        bg.setAdapter(bloodGroupAdapter);

        // Set up the location autocomplete
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.locations));
        loc.setAdapter(locationAdapter);

        if(cal.get(Calendar.AM_PM) == Calendar.PM)
        {
            AM_PM = "PM";
        }
        if(Hour<10)
        {
            Time += "0";
        }
        Time += Hour;
        Time +=" : ";
        if(Min<10) {
            Time += "0";
        }
        Time +=Min;
        Time +=(" "+AM_PM);
        Date = Day+"/"+Month+"/"+Year;

        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
        if(cur_user == null)
        {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(getApplicationContext(), "The posted user account does not exit", Toast.LENGTH_SHORT).show();
        } else {
            valid_id = cur_user.getUid();
        }
        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("POSTS");
        try {
            Post_button.setOnClickListener(v -> {
                final Query find_name = fdb.getReference("USERS").child(valid_id);
                if(bg.getText().length() == 0)
                {
                    bg.setError("Enter your Blood group!");
                }
                else if(Post_phone_no.getText().length() == 0)
                {
                    Post_phone_no.setError("Enter your Phone number!");
                }
                else if(Post_address.getText().length() == 0)
                {
                    Post_address.setError("Enter your Address!");
                }
                else if(loc.getText().length() == 0)
                {
                    loc.setError("Enter your Location!");
                }
                else {
                    find_name.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String NAME = Objects.requireNonNull(dataSnapshot.child("NAME").getValue()).toString();
                            if (dataSnapshot.exists()) {
                                db_ref.child(valid_id).child("NAME").setValue(NAME);
                                db_ref.child(valid_id).child("PHONE_NO").setValue(Post_phone_no.getText().toString());
                                db_ref.child(valid_id).child("ADDRESS").setValue(Post_address.getText().toString());
                                db_ref.child(valid_id).child("LOCATION").setValue(loc.getText().toString());
                                db_ref.child(valid_id).child("BLOOD_GROUP").setValue(bg.getText().toString());
                                db_ref.child(valid_id).child("TIME").setValue(Time);
                                db_ref.child(valid_id).child("DATE").setValue(Date);
                                Toast.makeText(PostActivity.this, "Your request has been posted successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "User account does not exit",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("User", databaseError.getMessage());
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}