package com.example.blood_point;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.PostHolder> {
    private ArrayList<user_data> postLists;
    private OnCallClickListener onCallClickListener;

    public RequestAdapter(ArrayList<user_data> postLists, OnCallClickListener onCallClickListener) {
        this.postLists = postLists;
        this.onCallClickListener = onCallClickListener;
    }

    // Define the OnCallClickListener interface
    public interface OnCallClickListener {
        void onCallClick(String phoneNumber);
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        TextView L1, L2, L3, L4, L5;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            L1 = itemView.findViewById(R.id.line1);
            L2 = itemView.findViewById(R.id.line2);
            L3 = itemView.findViewById(R.id.line3);
            L4 = itemView.findViewById(R.id.line4);
            L5 = itemView.findViewById(R.id.line5);
        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.request_list, viewGroup, false);
        return new PostHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {
        user_data data = postLists.get(i);
        postHolder.L1.setText("NEEDED " + data.getBlood_group() + " BLOOD DONOR !");
        postHolder.L2.setText("PHONE NO : " + data.getPhone_no());
        postHolder.L3.setText("POSTED BY : " + data.getName());
        postHolder.L4.setText("POSTED ON : " + data.getTime() + " , " + data.getDate());
        postHolder.L5.setText("FROM : " + data.getAddress() + " , " + data.getLocation());

        // Add call button click listener
        postHolder.itemView.findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCallClickListener != null) {
                    onCallClickListener.onCallClick(data.getPhone_no());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }
}
