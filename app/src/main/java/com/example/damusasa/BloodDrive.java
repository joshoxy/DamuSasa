package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class BloodDrive extends AppCompatActivity {
    private TextInputEditText center_name, drive_date, location, phone, drive_time;
    private Spinner bloodGroupsSpinner;
    private Button setUpButton;
    private ProgressDialog loader;
    private DatabaseReference userDatabaseRef;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_drive);

        center_name = findViewById(R.id.center_name);
        location = findViewById(R.id.location);
        phone = findViewById(R.id.phone);
        drive_date = findViewById(R.id.drive_date);
        drive_time = findViewById(R.id.drive_time);
        bloodGroupsSpinner = findViewById(R.id.bloodGroupsSpinner);
        setUpButton = findViewById(R.id.setUpButton);
        loader = new ProgressDialog(this);

        //Pick date
        drive_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = makeDateString(dayOfMonth, month, year);
                        drive_date.setText(date);
                    }
                };
                //end of date pick listener

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int style = AlertDialog.THEME_HOLO_LIGHT;

                datePickerDialog = new DatePickerDialog(BloodDrive.this, style, dateSetListener, year, month, day);
                datePickerDialog.setTitle("Pick a date");
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                //Paste up to here
            }
        });

        setUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String donationCenter =  center_name.getText().toString().trim();
                final String drive_location =  location.getText().toString().trim();
                final String drive_phone =  phone.getText().toString().trim();
                final String time =  drive_time.getText().toString().trim();
                final String date =  drive_date.getText().toString().trim();
                final String bloodGroup = bloodGroupsSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(donationCenter)){
                    center_name.setError("Donation center is required!");
                    return;
                }
                else if(TextUtils.isEmpty(drive_location)){
                    center_name.setError("Location is required!");
                    return;
                }
                else if(TextUtils.isEmpty(drive_phone)){
                    center_name.setError("Phone Number is required!");
                    return;
                }

                else if(TextUtils.isEmpty(time)){
                    drive_time.setError("Time is required!");
                    return;
                }

                else if(TextUtils.isEmpty(date)){
                    drive_date.setError("Date is required!");
                    return;
                }
                else if(bloodGroup.equals("Select your blood group")){
                    Toast.makeText(BloodDrive.this, "Select a blood group", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("blood drives").push();

                    String ref_Id = userDatabaseRef.getKey();

                    HashMap userInfo = new HashMap();
                    userInfo.put("donationCenter",donationCenter);
                    userInfo.put("location",drive_location);
                    userInfo.put("phone",drive_phone);
                    userInfo.put("date",date);
                    userInfo.put("time",time);
                    userInfo.put("bloodGroup",bloodGroup);
                    userInfo.put("ref_Id",ref_Id);

                    userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BloodDrive.this, "Blood drive successfully set!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BloodDrive.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }

                    });

                    Intent intent = new Intent(BloodDrive.this, AdminPage.class);
                    startActivity(intent);
                    finish();
                    loader.dismiss();
                }

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

}