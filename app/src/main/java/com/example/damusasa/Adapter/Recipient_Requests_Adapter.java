package com.example.damusasa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Recipient_Requests_Model;
import com.example.damusasa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
                }
                //What the recipient is supposed to see
                else {
                    holder.r_name.setText(requestsModel.getDonor_name());
                    holder.r_type.setText(requestsModel.getDonor_blood_type());
                    holder.r_phone.setText(requestsModel.getDonor_phone());
                    holder.r_status.setText(requestsModel.getStatus());
                    holder.r_button_accept.setVisibility(View.GONE);

                }

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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView r_name, r_type, r_phone, r_status;
        Button r_button_accept;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            r_name = itemView.findViewById(R.id.r_name);
            r_type = itemView.findViewById(R.id.r_type);
            r_phone = itemView.findViewById(R.id.r_phone);
            r_status = itemView.findViewById(R.id.r_status);
            r_button_accept = itemView.findViewById(R.id.r_button_accept);
        }

    }

}
