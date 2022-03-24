package com.example.damusasa.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.Center_Requests_Model;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

        String center_name = requests_model.getName();
        String location = requests_model.getLocation();
        String address = requests_model.getAddress();

        //Set On Click for donate button
        holder.c_button_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Donation")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        month = month+1;
                                        String date = makeDateString(dayOfMonth, month, year);
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String donor_name = snapshot.child("name").getValue().toString();
                                                String donor_phone = snapshot.child("phoneNumber").getValue().toString();
                                                String donor_blood = snapshot.child("bloodGroup").getValue().toString();

                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("accepted_center_donation")
                                                        .push();

                                                HashMap userInfo = new HashMap();
                                                userInfo.put("donor_name",donor_name);
                                                userInfo.put("donor_phone",donor_phone);
                                                userInfo.put("donor_blood",donor_blood);
                                                userInfo.put("center_name",center_name);
                                                userInfo.put("location",location);
                                                userInfo.put("address",address);
                                                userInfo.put("date",date);


                                                reference1.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
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

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        //End of save to db

                                    }
                                };
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int style = AlertDialog.THEME_HOLO_LIGHT;

                                holder.datePickerDialog = new DatePickerDialog(((Activity)context), style, dateSetListener, year, month, day);
                                holder.datePickerDialog.setTitle("Pick a date");
                                holder.datePickerDialog.setCancelable(false);
                                holder.datePickerDialog.show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return getMonthFormat(month) +" " + dayOfMonth + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView c_name, c_type, c_location, c_address, c_phone;
        Button c_button_request;
        DatePickerDialog datePickerDialog;
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
