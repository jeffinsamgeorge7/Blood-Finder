package com.example.blood_point;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MyBloodpost extends AppCompatActivity {
    TextView L1, L2, L3, L4, L5,No_post,heading;
    Button Post_delete;
    ImageView back;
    FirebaseAuth mAuth;
    FirebaseDatabase db_user;
    FirebaseUser cur_user;
    DatabaseReference post_ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybloodpost);

        L1 = findViewById(R.id.l1);
        L2 = findViewById(R.id.l2);
        L3 = findViewById(R.id.l3);
        L4 = findViewById(R.id.l4);
        L5 = findViewById(R.id.l5);
        heading = findViewById(R.id.your_blood_post);
        No_post = findViewById(R.id.no_post);
        back = findViewById(R.id.backarrow);
        Post_delete = findViewById(R.id.post_delete);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            startActivity(intent);
            finish();
        });
        mAuth = FirebaseAuth.getInstance();
        db_user = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        post_ref = db_user.getReference("POSTS");
        Query posts = post_ref.child(cur_user.getUid());
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String BLOOD_GROUP = Objects.requireNonNull(dataSnapshot.child("BLOOD_GROUP").getValue()).toString();
                    String PHONE_NO = Objects.requireNonNull(dataSnapshot.child("PHONE_NO").getValue()).toString();
                    String NAME = Objects.requireNonNull(dataSnapshot.child("NAME").getValue()).toString();
                    String TIME = Objects.requireNonNull(dataSnapshot.child("TIME").getValue()).toString();
                    String DATE = Objects.requireNonNull(dataSnapshot.child("DATE").getValue()).toString();
                    String ADDRESS = Objects.requireNonNull(dataSnapshot.child("ADDRESS").getValue()).toString();
                    String LOCATION = Objects.requireNonNull(dataSnapshot.child("LOCATION").getValue()).toString();
                    L1.setText("\t\tNEEDED " + BLOOD_GROUP + " BLOOD DONOR !");
                    L2.setText("\t\tPHONE NO : " + PHONE_NO);
                    L3.setText("\t\tPOSTED BY : " + NAME);
                    L4.setText("\t\tPOSTED ON : " + TIME + " , " + DATE);
                    L5.setText("\t\tFROM : " + ADDRESS + " , " + LOCATION);
                }
                else
                {
                    heading.setVisibility(View.GONE);
                    Post_delete.setVisibility(View.GONE);
                    L1.setVisibility(View.GONE);
                    L2.setVisibility(View.GONE);
                    L3.setVisibility(View.GONE);
                    L4.setVisibility(View.GONE);
                    L5.setVisibility(View.GONE);
                    No_post.setText("YOUR BLOOD REQUEST IS NOT BEING POSTED !");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
        Post_delete.setOnClickListener(v -> {
            post_ref.child(cur_user.getUid()).removeValue();
            startActivity(new Intent(this, Postdelete.class));
            finish();
        });
    }
}