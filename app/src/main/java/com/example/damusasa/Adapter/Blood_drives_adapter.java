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
import com.example.damusasa.Model.Blood_Drives_Model;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Blood_drives_adapter extends RecyclerView.Adapter<Blood_drives_adapter.MyViewHolder>{

    Context context;
    ArrayList<Blood_Drives_Model> list;

    public Blood_drives_adapter(Context context, ArrayList<Blood_Drives_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blood_drive_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Blood_Drives_Model blood_drives_model = list.get(position);
        holder.drive_center.setText(blood_drives_model.getDonationCenter());
        holder.drive_location.setText(blood_drives_model.getLocation());
        holder.drive_type.setText(blood_drives_model.getBloodGroup());
        holder.drive_date.setText(blood_drives_model.getDate());
        holder.drive_time.setText(blood_drives_model.getTime());

        String drive_center = blood_drives_model.getDonationCenter();
        String drive_location = blood_drives_model.getLocation();
        String drive_type = blood_drives_model.getBloodGroup();
        String drive_date = blood_drives_model.getDate();
        String drive_time = blood_drives_model.getTime();

        //Show buttons based on who is logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("Admin")){
                    holder.button_cancel.setVisibility(View.VISIBLE);
                }
                else if (type.equals("donor")){
                    holder.button_attend.setVisibility(View.VISIBLE);
                }
                else if (type.equals("recipient")){
                    holder.button_attend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Add onClick for attend button
        holder.button_attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.child("name").getValue().toString();
                                        String phoneNumber = snapshot.child("phoneNumber").getValue().toString();
                                        String bloodGroup = snapshot.child("bloodGroup").getValue().toString();

                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                                                .child("blood_drive_members").push();
                                        reference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String attend_ref_Id = snapshot.getKey();
                                                HashMap attending = new HashMap();
                                                attending.put("name",name);
                                                attending.put("phoneNumber",phoneNumber);
                                                attending.put("bloodGroup",bloodGroup);
                                                attending.put("drive_center",drive_center);
                                                attending.put("drive_location",drive_location);
                                                attending.put("drive_date",drive_date);
                                                attending.put("drive_time",drive_time);
                                                attending.put("drive_type",drive_type);
                                                attending.put("attend_ref_Id",attend_ref_Id);

                                                reference2.updateChildren(attending).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        Toast.makeText(v.getContext(), "Successfully sent!", Toast.LENGTH_SHORT).show();
                                                        ((Activity)context).finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(v.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                //end of attending button
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

        //Add onClick for cancel button
        holder.button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                        .child("blood drives");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //Fix here
                                        String ref_Id = blood_drives_model.getRef_Id();
                                        Query query = reference1.orderByChild("ref_Id").equalTo(ref_Id);
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
        TextView drive_center, drive_location, drive_type, drive_date, drive_time;
        Button button_cancel, button_attend;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            drive_center = itemView.findViewById(R.id.drive_center);
            drive_location = itemView.findViewById(R.id.drive_location);
            drive_type = itemView.findViewById(R.id.drive_type);
            drive_date = itemView.findViewById(R.id.drive_date);
            drive_time = itemView.findViewById(R.id.drive_time);
            button_cancel = itemView.findViewById(R.id.button_cancel);
            button_attend = itemView.findViewById(R.id.button_attend);
        }
    }
}
