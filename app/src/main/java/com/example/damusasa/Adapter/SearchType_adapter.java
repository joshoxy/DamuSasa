package com.example.damusasa.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.BloodStock_model;
import com.example.damusasa.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.util.ArrayList;

public class SearchType_adapter extends RecyclerView.Adapter<SearchType_adapter.MyViewHolder> {
    Context context;
    ArrayList<BloodStock_model> list;

    public SearchType_adapter(Context context, ArrayList<BloodStock_model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_blood_stock_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BloodStock_model bloodStockModel = list.get(position);
        holder.blood_type1.setText(bloodStockModel.getBlood_type());
        holder.center_name.setText(bloodStockModel.getCenter_name());
        holder.location.setText(bloodStockModel.getCenter_location());
        holder.loader = new ProgressDialog(context);
        String blood = bloodStockModel.getBlood_type();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("blood_stock");
        reference.orderByChild("blood_type").equalTo(blood).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                String number = String.valueOf(size);
                holder.quantity.setText(number);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView blood_type1, quantity, center_name, location;
        Button btn_request;
        ProgressDialog loader;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            blood_type1 = itemView.findViewById(R.id.blood_type1);
            quantity = itemView.findViewById(R.id.quantity);
            center_name = itemView.findViewById(R.id.center_name);
            location = itemView.findViewById(R.id.location);
            btn_request = itemView.findViewById(R.id.btn_request);
        }
    }
}
