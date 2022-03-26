package com.example.damusasa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Accepted_Requests_Model;
import com.example.damusasa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Accepted_Requests_Adapter extends RecyclerView.Adapter<Accepted_Requests_Adapter.MyViewHolder> {
    Context context;
    ArrayList<Accepted_Requests_Model> list;

    public Accepted_Requests_Adapter(Context context, ArrayList<Accepted_Requests_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.accepted_requests_list, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Accepted_Requests_Model requests_model = list.get(position);
        holder.txt_name.setText(requests_model.getDonor_name());
        holder.txt_type.setText(requests_model.getDonor_blood());
        holder.txt_phone.setText(requests_model.getDonor_phone());
        holder.txt_center.setText(requests_model.getCenter_name());
        holder.txt_date.setText(requests_model.getDate());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_name, txt_type, txt_phone, txt_center, txt_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_type = itemView.findViewById(R.id.txt_type);
            txt_phone = itemView.findViewById(R.id.txt_phone);
            txt_center = itemView.findViewById(R.id.txt_center);
            txt_date = itemView.findViewById(R.id.txt_date);
        }
    }
}
