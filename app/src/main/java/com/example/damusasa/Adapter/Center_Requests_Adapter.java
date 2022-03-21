package com.example.damusasa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Center_Requests_Model;
import com.example.damusasa.R;

import java.util.ArrayList;

public class Center_Requests_Adapter extends RecyclerView.Adapter<Center_Requests_Adapter.MyViewHolder2> {

    Context context;
    ArrayList<Center_Requests_Model> list;

    public Center_Requests_Adapter(Context context, ArrayList<Center_Requests_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.center_requests_list, parent,false);
        return new MyViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        Center_Requests_Model requests_model = list.get(position);
        holder.c_name.setText(requests_model.getName());
        holder.c_type.setText(requests_model.getBloodGroup());
        holder.c_location.setText(requests_model.getLocation());
        holder.c_address.setText(requests_model.getAddress());
        holder.c_phone.setText(requests_model.getPhone());

        //Set On Click for donate button

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView c_name, c_type, c_location, c_address, c_phone;
        Button c_button_request;
        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            c_name = itemView.findViewById(R.id.c_name);
            c_type = itemView.findViewById(R.id.c_type);
            c_location = itemView.findViewById(R.id.c_location);
            c_address = itemView.findViewById(R.id.c_address);
            c_phone = itemView.findViewById(R.id.c_phone);
            c_button_request = itemView.findViewById(R.id.c_button_request);
        }
    }
}
