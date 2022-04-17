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

import java.util.ArrayList;

public class BloodStock_Adapter extends RecyclerView.Adapter<BloodStock_Adapter.MyViewHolder> {
    Context context;
    ArrayList<BloodStock_model> list;

    public BloodStock_Adapter(Context context, ArrayList<BloodStock_model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.blood_stock_list, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BloodStock_model bloodStockModel = list.get(position);
        holder.blood_type.setText(bloodStockModel.getBlood_type());
        holder.volume.setText(bloodStockModel.getBlood_volume());
        holder.date_added.setText(bloodStockModel.getDate_added());
        holder.expiry_date.setText(bloodStockModel.getExpiry_date());
        holder.center_name.setText(bloodStockModel.getCenter_name());
        holder.location.setText(bloodStockModel.getCenter_location());
        holder.loader = new ProgressDialog(context);

        //Show center name and location if admin is logged in
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("Admin")){
                    holder.layout_name.setVisibility(View.VISIBLE);
                    holder.layout_location.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //OnClick for remove button
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("blood_stock");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String ref_Id = bloodStockModel.getRef_Id();
                                        Query query = reference.orderByChild("ref_Id").equalTo(ref_Id);
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                    dataSnapshot.getRef().removeValue();
                                                    Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                                                    ((Activity)context).finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                //End of cancel request code

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView blood_type, volume, date_added, expiry_date, center_name, location;
        Button btn_remove, searchBtn;
        LinearLayout layout_name, layout_location, search;
        TextInputEditText search_type;
        ProgressDialog loader;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            blood_type = itemView.findViewById(R.id.blood_type);
            volume = itemView.findViewById(R.id.volume);
            date_added = itemView.findViewById(R.id.date_added);
            expiry_date = itemView.findViewById(R.id.expiry_date);
            center_name = itemView.findViewById(R.id.center_name);
            location = itemView.findViewById(R.id.location);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            layout_name = itemView.findViewById(R.id.layout_name);
            layout_location = itemView.findViewById(R.id.layout_location);
            search = itemView.findViewById(R.id.search);
            searchBtn = itemView.findViewById(R.id.searchBtn);
            search_type = itemView.findViewById(R.id.search_type);
        }
    }
}
