package com.example.damusasa.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.blood_drive_members_model;
import com.example.damusasa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Blood_drive_members_adapter extends RecyclerView.Adapter<Blood_drive_members_adapter.MyViewHolder> {
    Context context;
    ArrayList<blood_drive_members_model> list;

    public Blood_drive_members_adapter(Context context, ArrayList<blood_drive_members_model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blood_drive_members_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        blood_drive_members_model members_model = list.get(position);
        holder.donor_name.setText(members_model.getName());
        holder.donor_phone.setText(members_model.getPhoneNumber());
        holder.donor_blood_type.setText(members_model.getBloodGroup());
        holder.drive_center.setText(members_model.getDrive_center());
        holder.drive_location.setText(members_model.getDrive_location());
        holder.drive_type.setText(members_model.getDrive_type());
        holder.drive_date.setText(members_model.getDrive_date());
        holder.drive_time.setText(members_model.getDrive_time());

        //Show cancel buttons and views if donors, recipients and admin are logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("center")){
                    holder.layout_donor_name.setVisibility(View.VISIBLE);
                    holder.layout_donor_phone.setVisibility(View.VISIBLE);
                    holder.layout_blood_type.setVisibility(View.VISIBLE);
                }
                else if (type.equals("Admin")){
                    holder.layout_donor_name.setVisibility(View.VISIBLE);
                    holder.layout_donor_phone.setVisibility(View.VISIBLE);
                    holder.layout_blood_type.setVisibility(View.VISIBLE);
                    holder.button_cancel.setVisibility(View.GONE);
                    //Make this visible so that its view. gone instead

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //end of show button

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
                                        .child("blood_drive_members");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //Fix here
                                        String ref_Id = members_model.getAttend_ref_Id();
                                        Query query = reference1.orderByChild("attend_ref_Id").equalTo(ref_Id);
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
        //end of cancel button onClick

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView donor_name,donor_phone,donor_blood_type,drive_center,drive_location,drive_type,drive_date,drive_time;
        Button button_cancel;
        LinearLayout layout_donor_name, layout_donor_phone, layout_blood_type;  //These are only visible to centers and admin
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            donor_name = itemView.findViewById(R.id.donor_name);
            donor_phone = itemView.findViewById(R.id.donor_phone);
            donor_blood_type = itemView.findViewById(R.id.donor_blood_type);
            drive_center = itemView.findViewById(R.id.drive_center);
            drive_location = itemView.findViewById(R.id.drive_location);
            drive_type = itemView.findViewById(R.id.drive_type);
            drive_date = itemView.findViewById(R.id.drive_date);
            drive_time = itemView.findViewById(R.id.drive_time);
            button_cancel = itemView.findViewById(R.id.button_cancel);
            layout_donor_name = itemView.findViewById(R.id.layout_donor_name);
            layout_donor_phone = itemView.findViewById(R.id.layout_donor_phone);
            layout_blood_type = itemView.findViewById(R.id.layout_blood_type);

        }
    }
}
