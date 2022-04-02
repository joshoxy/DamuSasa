package com.example.damusasa.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Appointment_model;
import com.example.damusasa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        //Show button if donor is logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("donor")){
                    holder.button_cancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Add onClick for cancel button
        holder.button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                        .child("appointments");
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ref_Id = user.getRef_Id();
                        Query query = reference1.orderByChild(ref_Id);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    dataSnapshot.getRef().removeValue();
                                    Toast.makeText(context, "Successfully cancelled", Toast.LENGTH_SHORT).show();
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
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView donorName, centerName, centerAddress, Time;
        Button button_cancel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            donorName = itemView.findViewById(R.id.all_txt_name);
            centerName = itemView.findViewById(R.id.all_txt_center_name);
            centerAddress = itemView.findViewById(R.id.all_txt_location);
            Time = itemView.findViewById(R.id.all_txt_time);
            button_cancel = itemView.findViewById(R.id.button_cancel);
        }
    }
}
