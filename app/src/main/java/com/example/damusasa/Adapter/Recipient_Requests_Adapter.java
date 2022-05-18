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

import com.example.damusasa.Model.Recipient_Requests_Model;
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

public class Recipient_Requests_Adapter extends RecyclerView.Adapter<Recipient_Requests_Adapter.MyViewHolder>{
    Context context;
    ArrayList<Recipient_Requests_Model> list;

    public Recipient_Requests_Adapter(Context context, ArrayList<Recipient_Requests_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recipient_request_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recipient_Requests_Model requestsModel = list.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("donor")){ //What the donor is supposed to see
                    holder.r_name.setText(requestsModel.getRecipient_name());
                    holder.r_type.setText(requestsModel.getRecipient_blood());
                    holder.r_phone.setText(requestsModel.getRecipient_phone());
                    holder.r_status.setText(requestsModel.getStatus());
                    holder.Layout_donor.setVisibility(View.GONE);

                    //Hide button if request has been approved
                    String status = requestsModel.getStatus();
                    if (status.equals("Approved")){
                        holder.r_button_accept.setVisibility(View.GONE);
                    }

                }
                //What the admin is supposed to see
                else if (type.equals("Admin")){
                    holder.r_name.setText(requestsModel.getRecipient_name());
                    holder.d_name.setText(requestsModel.getDonor_name());
                    holder.r_type.setText(requestsModel.getDonor_blood_type());
                    holder.r_phone.setText(requestsModel.getDonor_phone());
                    holder.r_status.setText(requestsModel.getStatus());
                    holder.r_button_accept.setVisibility(View.GONE);
                    holder.r_button_cancel.setVisibility(View.VISIBLE);
                }

                //What the recipient is supposed to see
                else {
                    holder.d_name.setText(requestsModel.getDonor_name());
                    holder.r_type.setText(requestsModel.getDonor_blood_type());
                    holder.r_phone.setText(requestsModel.getDonor_phone());
                    holder.r_status.setText(requestsModel.getStatus());
                    holder.r_button_accept.setVisibility(View.GONE);
                    holder.layout_recipient.setVisibility(View.GONE);
                    holder.r_button_cancel.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Set on click listener for accept request
        holder.r_button_accept.setOnClickListener(new View.OnClickListener() {
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
                                        .child("recipient_requests");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String ref_id = requestsModel.getRef_id();
                                        HashMap userInfo = new HashMap();
                                        userInfo.put("status", "Approved");
                                        reference1.child(ref_id).updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                ((Activity)context).finish();
                                                holder.r_button_accept.setVisibility(View.GONE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        //end of update

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        //Set on click listener for cancel request
        holder.r_button_cancel.setOnClickListener(new View.OnClickListener() {
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
                                        .child("recipient_requests");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        //Fix here
                                        String ref_id = requestsModel.getRef_id();
                                        Query query = reference1.orderByChild("ref_id").equalTo(ref_id);
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView r_name, r_type, r_phone, r_status, donor_name, d_name;
        Button r_button_accept, r_button_cancel;
        LinearLayout Layout_donor, layout_recipient;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            r_name = itemView.findViewById(R.id.r_name);
            r_type = itemView.findViewById(R.id.r_type);
            r_phone = itemView.findViewById(R.id.r_phone);
            r_status = itemView.findViewById(R.id.r_status);
            donor_name = itemView.findViewById(R.id.donor_name);
            Layout_donor = itemView.findViewById(R.id.Layout_donor);
            layout_recipient = itemView.findViewById(R.id.layout_recipient);
            Layout_donor = itemView.findViewById(R.id.Layout_donor);
            d_name = itemView.findViewById(R.id.d_name);
            r_button_accept = itemView.findViewById(R.id.r_button_accept);
            r_button_cancel = itemView.findViewById(R.id.r_button_cancel);
        }

    }

}
