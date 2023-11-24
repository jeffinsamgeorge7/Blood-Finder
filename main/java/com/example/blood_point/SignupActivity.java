package com.example.blood_point;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    EditText name, gender, phone_no, address, location, blood_group, mail_id, password, confirm_password;
    CheckBox Donor;
    Button open;
    ImageView back;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase db_user;
    DatabaseReference user_ref, donor_ref,dd_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.input_name);
        gender = findViewById(R.id.search_gender);
        phone_no = findViewById(R.id.input_phoneno);
        address = findViewById(R.id.input_address);
        location = findViewById(R.id.search_location);
        blood_group = findViewById(R.id.search_blood_group);
        mail_id = findViewById(R.id.input_mail_id);
        password = findViewById(R.id.input_password);
        confirm_password = findViewById(R.id.input_confirm_password);
        Donor = findViewById(R.id.blood_donor);
        open = findViewById(R.id.open_account);
        back = findViewById(R.id.backarrow);
        FirebaseApp.initializeApp(this);
        db_user = FirebaseDatabase.getInstance();
        user_ref = db_user.getReference("USERS");
        donor_ref = db_user.getReference("DONORS");
        dd_ref = db_user.getReference("donor_details");
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(this,"connection"+db_user,Toast.LENGTH_LONG).show();
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
        open.setOnClickListener(v -> {
            String Name = name.getText().toString();
            String Gender = gender.getText().toString();
            String Phone_no = phone_no.getText().toString();
            String Address = address.getText().toString();
            String Location = location.getText().toString();
            String Blood_group = blood_group.getText().toString();
            String Mail_id = mail_id.getText().toString();
            String Password = password.getText().toString();
            String Confirm_password = confirm_password.getText().toString();
            try {
                if (Name.length() <= 2) {
                    name.setError("Name");
                    name.requestFocusFromTouch();
                } else if (Phone_no.length() > 10) {
                    phone_no.setError("Phone Number");
                    phone_no.requestFocusFromTouch();
                } else if (Address.length() <= 2) {
                    address.setError("Address");
                    address.requestFocusFromTouch();
                } else if (Location.length() <= 2) {
                    location.setError("Location");
                    location.requestFocusFromTouch();
                } else if (Mail_id.length() == 0) {
                    mail_id.setError("Email ID");
                    mail_id.requestFocusFromTouch();
                } else if (Password.length() <= 4) {
                    password.setError("Password");
                    password.requestFocusFromTouch();
                } else if (!Password.equals(Confirm_password)) {
                    Toast.makeText(this, "Password did not match!", Toast.LENGTH_SHORT).show();
                    confirm_password.requestFocusFromTouch();
                }
                mAuth.createUserWithEmailAndPassword(Mail_id, Password).addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Account Creation Failed !", Toast.LENGTH_SHORT).show();
                        Log.v("error", Objects.requireNonNull(task.getException()).getMessage());
                    } else {
                        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        user_ref.child(id).child("NAME").setValue(Name);
                        user_ref.child(id).child("GENDER").setValue(Gender);
                        user_ref.child(id).child("PHONE_NO").setValue(Phone_no);
                        user_ref.child(id).child("ADDRESS").setValue(Address);
                        user_ref.child(id).child("LOCATION").setValue(Location);
                        user_ref.child(id).child("BLOOD_GROUP").setValue(Blood_group);
                        if (Donor.isChecked()) {
                            donor_ref.child(Location).child(Blood_group).child(id).child("UID").setValue(id).toString();
                            donor_ref.child(Location).child(Blood_group).child(id).child("NAME").setValue(Name);
                            donor_ref.child(Location).child(Blood_group).child(id).child("GENDER").setValue(Gender);
                            donor_ref.child(Location).child(Blood_group).child(id).child("PHONE_NO").setValue(Phone_no);
                            donor_ref.child(Location).child(Blood_group).child(id).child("ADDRESS").setValue(Address);

                            dd_ref.child(id).child("DETAILS").child("NAME").setValue(Name);
                            dd_ref.child(id).child("DETAILS").child("GENDER").setValue(Gender);
                            dd_ref.child(id).child("DETAILS").child("PHONE_NO").setValue(Phone_no);
                            dd_ref.child(id).child("DETAILS").child("BLOOD_GROUP").setValue(Blood_group);
                            dd_ref.child(id).child("DETAILS").child("ADDRESS").setValue(Address+","+Location);
                        }
                        startActivity(new Intent(this, CreatedActivity.class));
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
