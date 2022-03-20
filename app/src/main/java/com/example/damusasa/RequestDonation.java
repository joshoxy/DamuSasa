package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RequestDonation extends AppCompatActivity {
    private TextInputEditText centerName, centerLocation, centerAddress, centerPhone;
    private Button requestButton;
    private Spinner bloodGroupsSpinner;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_donation);

        centerName = findViewById(R.id.centerName);
        centerLocation = findViewById(R.id.centerLocation);
        centerAddress = findViewById(R.id.centerAddress);
        centerPhone = findViewById(R.id.centerPhone);
        requestButton = findViewById(R.id.requestButton);
        bloodGroupsSpinner = findViewById(R.id.bloodGroupsSpinner);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String center_name =  centerName.getText().toString().trim();
                final String center_location =  centerLocation.getText().toString().trim();
                final String center_address =  centerAddress.getText().toString().trim();
                final String center_phone =  centerPhone.getText().toString().trim();
                final String bloodGroup = bloodGroupsSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(center_name)){
                    centerName.setError("Center Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(center_location)){
                    centerLocation.setError("Center Location is required!");
                    return;
                }
                if(TextUtils.isEmpty(center_address)){
                    centerAddress.setError("Center Address is required!");
                    return;
                }
                if(TextUtils.isEmpty(center_phone)){
                    centerPhone.setError("Center Phone Number is required!");
                    return;
                }
                if(bloodGroup.equals("Select your blood group")){
                    Toast.makeText(RequestDonation.this, "Select a blood group", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();


                    userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                .child("requests").push();


                        HashMap userInfo = new HashMap();
                        userInfo.put("name", center_name);
                        userInfo.put("bloodGroup", bloodGroup);
                        userInfo.put("location", center_location);
                        userInfo.put("address", center_address);
                        userInfo.put("phone", center_phone);
                        userInfo.put("type", "request");

                        userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RequestDonation.this, "Request Upload Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RequestDonation.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }

                        });

                    Intent intent = new Intent(RequestDonation.this, CenterMain.class);
                    startActivity(intent);
                    finish();
                    loader.dismiss();
                }

            }
        });


    }
}