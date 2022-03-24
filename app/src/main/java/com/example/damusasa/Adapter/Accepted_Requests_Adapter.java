package com.example.damusasa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Accepted_Requests_Model;
import com.example.damusasa.R;

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

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
