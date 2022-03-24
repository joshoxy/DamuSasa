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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        /*centerName = findViewById(R.id.centerName);
        centerLocation = findViewById(R.id.centerLocation);
        centerAddress = findViewById(R.id.centerAddress);
        centerPhone = findViewById(R.id.centerPhone);*/
        requestButton = findViewById(R.id.requestButton);
        bloodGroupsSpinner = findViewById(R.id.bloodGroupsSpinner);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String c_name = snapshot.child("centerName").getValue().toString();
                        String c_location = snapshot.child("centerLocation").getValue().toString();
                        String c_address = snapshot.child("centerAddress").getValue().toString();
                        String c_phone = snapshot.child("centerPhone").getValue().toString();
                        /*centerName.setText(c_name);
                        centerLocation.setText(c_location);
                        centerAddress.setText(c_address);
                        centerPhone.setText(c_phone);*/
                        final String bloodGroup = bloodGroupsSpinner.getSelectedItem().toString();

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
                            userInfo.put("name", c_name);
                            userInfo.put("bloodGroup", bloodGroup);
                            userInfo.put("location", c_location);
                            userInfo.put("address", c_address);
                            userInfo.put("phone", c_phone);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }
}