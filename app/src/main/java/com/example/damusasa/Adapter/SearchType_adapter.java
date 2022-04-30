package com.example.damusasa.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Model.BloodStock_model;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Calendar;
import java.util.HashMap;

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
        holder.setIsRecyclable(false);
        String blood = bloodStockModel.getBlood_type();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("recipient")){
                    holder.btn_request.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        //Set On Click for request button
        holder.btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Request")
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
                                                String recipient_name = snapshot.child("name").getValue().toString();
                                                String recipient_phone = snapshot.child("phoneNumber").getValue().toString();
                                                String recipient_blood = snapshot.child("bloodGroup").getValue().toString();
                                                String user_Id = snapshot.child("id").getValue().toString();
                                                String center_name = bloodStockModel.getCenter_name();
                                                String location = bloodStockModel.getCenter_location();

                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("appointments")
                                                        .push();

                                                reference1.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String ref_Id = snapshot.getKey();

                                                        HashMap userInfo = new HashMap();
                                                        userInfo.put("recipient_name",recipient_name);
                                                        userInfo.put("recipient_phone",recipient_phone);
                                                        userInfo.put("recipient_blood",recipient_blood);
                                                        userInfo.put("center_name",center_name);
                                                        userInfo.put("location",location);
                                                        userInfo.put("date",date);
                                                        userInfo.put("ref_Id",ref_Id);
                                                        userInfo.put("user_Id",user_Id);

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
                                                        //end of upload code

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
                                        //End of save to db

                                    }
                                };
                                //end of date pick listener

                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int style = AlertDialog.THEME_HOLO_LIGHT;

                                holder.datePickerDialog = new DatePickerDialog(((Activity)context), style, dateSetListener, year, month, day);
                                holder.datePickerDialog.setTitle("Pick a date");
                                holder.datePickerDialog.setCancelable(false);
                                holder.datePickerDialog.show();

                                //set min date for datePicker
                                holder.datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.DAY_OF_MONTH,60);
                                //Set the maximum date to select from DatePickerDialog
                                holder.datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                                //Paste up to here
                            }
                            //end of onClick
                        })
                        //end of dialogInterface
                        .setNegativeButton("No", null)
                        .show();

            }
        });
        //end of button onClick

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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView blood_type1, quantity, center_name, location;
        Button btn_request;
        ProgressDialog loader;
        DatePickerDialog datePickerDialog;

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
