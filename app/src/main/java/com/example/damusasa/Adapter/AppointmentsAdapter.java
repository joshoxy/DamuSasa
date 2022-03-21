package com.example.damusasa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Appointment_model;
import com.example.damusasa.R;

import java.util.ArrayList;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Appointment_model> list;

    public AppointmentsAdapter(Context context, ArrayList<Appointment_model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.appointment_list, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Appointment_model user = list.get(position);
        holder.donorName.setText(user.getCustomerName());
        holder.centerName.setText(user.getCenterName());
        holder.centerAddress.setText(user.getCenterAddress());
        holder.Time.setText(user.getTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView donorName, centerName, centerAddress, Time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            donorName = itemView.findViewById(R.id.all_txt_name);
            centerName = itemView.findViewById(R.id.all_txt_center_name);
            centerAddress = itemView.findViewById(R.id.all_txt_location);
            Time = itemView.findViewById(R.id.all_txt_time);
        }
    }
}
