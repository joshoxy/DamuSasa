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

import com.example.damusasa.Model.Appointment_model;
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
import java.util.Calendar;
import java.util.HashMap;

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
        holder.BookingDate.setText(user.getBookingDate());

        //Show button if donor is logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("donor")){
                    holder.button_cancel.setVisibility(View.VISIBLE);
                    holder.button_edit.setVisibility(View.VISIBLE);
                }
                if (type.equals("Admin")){
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
                new AlertDialog.Builder(context)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                        .child("appointments");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        //Fix here
                                        String ref_id = user.getRef_Id();
                                        Query query = reference1.orderByChild("ref_Id").equalTo(ref_id);
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

                /*DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                        .child("appointments");
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Fix here
                        String ref_id = user.getRef_Id();
                        Query query = reference1.orderByChild("ref_Id").equalTo(ref_id);
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
                });*/

                //End of cancel request code

            }
        });

        //Add onClick for edit button
        holder.button_edit.setOnClickListener(new View.OnClickListener() {
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
                                        .child("appointments");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String ref_id = user.getRef_Id();
                                        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                month = month+1;
                                                String date = makeDateString(dayOfMonth, month, year);
                                                HashMap editInfo = new HashMap();
                                                editInfo.put("BookingDate",date);
                                                reference1.child(ref_id).updateChildren(editInfo).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        Toast.makeText(context, "Successfully changed", Toast.LENGTH_SHORT).show();
                                                        ((Activity)context).finish();
                                                        holder.datePickerDialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(v.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        };
                                        //end of date picker
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR);
                                        int month = calendar.get(Calendar.MONTH);
                                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                                        int style = AlertDialog.THEME_HOLO_LIGHT;

                                        holder.datePickerDialog = new DatePickerDialog(((Activity)context), style, dateSetListener, year, month, day);
                                        holder.datePickerDialog.setTitle("Pick a new date");
                                        holder.datePickerDialog.setCancelable(false);

                                        //set min date for datePicker
                                        holder.datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.DAY_OF_MONTH,60);
                                        //Set the maximum date to select from DatePickerDialog
                                        holder.datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

                                        if(!((Activity) context).isFinishing())
                                        {
                                            holder.datePickerDialog.show();
                                            //show dialog
                                        }
                                        /*holder.datePickerDialog.show();*/
                                        //Paste up to here
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView donorName, centerName, centerAddress, Time, BookingDate;
        Button button_cancel,button_edit;
        DatePickerDialog datePickerDialog;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            donorName = itemView.findViewById(R.id.all_txt_name);
            centerName = itemView.findViewById(R.id.all_txt_center_name);
            centerAddress = itemView.findViewById(R.id.all_txt_location);
            Time = itemView.findViewById(R.id.all_txt_time);
            BookingDate = itemView.findViewById(R.id.all_txt_date);
            button_cancel = itemView.findViewById(R.id.button_cancel);
            button_edit = itemView.findViewById(R.id.button_edit);
        }
    }
}
