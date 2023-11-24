package com.example.blood_point;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {
    ArrayList<donor_data> donor_Lists;

    public interface OnLocateClickListener {
    }


    public class SearchHolder extends RecyclerView.ViewHolder
    {
        TextView L1, L2, L3;
        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            L1 = itemView.findViewById(R.id.line1);
            L2 = itemView.findViewById(R.id.line2);
            L3 = itemView.findViewById(R.id.line3);
        }
    }
    public SearchAdapter(ArrayList<donor_data> donor_Lists)
    {
        this.donor_Lists = donor_Lists;
    }
    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list,parent, false);
        return new SearchHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        donor_data data = donor_Lists.get(position);
        holder.L1.setText("NAME : "+data.getName());
        holder.L2.setText("PHONE NO : "+data.getPhone_no());
        
    }
    @Override
    public int getItemCount() {
        return donor_Lists.size();
    }

}
