package com.example.blood_point;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity context;
    ArrayList<String> markerId;
    public CustomInfoWindowAdapter(Activity context,ArrayList<String> markerId){
        this.context = context;
        this.markerId = markerId;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        try{
            if(markerId.contains(marker.getId())) {
                @SuppressLint("InflateParams")
                View view = context.getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView L1 = view.findViewById(R.id.l1);
                TextView L2 = view.findViewById(R.id.l2);
                TextView L3 = view.findViewById(R.id.l3);
                TextView L4 = view.findViewById(R.id.l4);
                String title = marker.getTitle();
                assert title != null;
                String[] str = title.split("_");
                String snippet = marker.getSnippet();
                assert snippet != null;
                final String[] str2 = snippet.split("_");
                L1.setText("Name : " + str[0]);
                L2.setText("Blood group : " + str[1]);
                L3.setText("Phone number : " + str2[0]);
                L4.setText("Address : " + str2[1]);
                return view;
            }
            else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }
}