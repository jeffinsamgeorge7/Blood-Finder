package com.example.blood_point;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeView extends Fragment implements RequestAdapter.OnCallClickListener {
    private View view;
    private RecyclerView recView;
    private RequestAdapter request;
    private ArrayList<user_data> postLists;
    private DatabaseReference post_ref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_view, container, false);
        recView = view.findViewById(R.id.recycle_view);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        post_ref = FirebaseDatabase.getInstance().getReference();
        postLists = new ArrayList<>();
        request = new RequestAdapter(postLists, this);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
        recView.setAdapter(request);
        addPosts();
        return view;
    }

    private void addPosts() {
        Query posts = post_ref.child("POSTS");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot db : dataSnapshot.getChildren()) {
                        // Check for null values before accessing them
                        String BLOOD_GROUP = getStringValue(db, "BLOOD_GROUP");
                        String PHONE_NO = getStringValue(db, "PHONE_NO");
                        String NAME = getStringValue(db, "NAME");
                        String TIME = getStringValue(db, "TIME");
                        String DATE = getStringValue(db, "DATE");
                        String ADDRESS = getStringValue(db, "ADDRESS");
                        String LOCATION = getStringValue(db, "LOCATION");

                        if (BLOOD_GROUP != null && PHONE_NO != null && NAME != null
                                && TIME != null && DATE != null && ADDRESS != null && LOCATION != null) {
                            user_data data = new user_data(BLOOD_GROUP, PHONE_NO, NAME, TIME, DATE, ADDRESS, LOCATION, 0.0, 0.0);

                            postLists.add(data);
                        }
                    }
                    request.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "No Blood Request posted -> Database is empty!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    // Helper method to safely retrieve string values from DataSnapshot
    private String getStringValue(DataSnapshot dataSnapshot, String key) {
        if (dataSnapshot.child(key).exists()) {
            Object value = dataSnapshot.child(key).getValue();
            return (value != null) ? value.toString() : null;
        }
        return null;
    }


    @Override
    public void onCallClick(String phoneNumber) {
        // Handle the call functionality here
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}

