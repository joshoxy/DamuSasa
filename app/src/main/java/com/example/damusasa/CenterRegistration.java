package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CenterRegistration extends AppCompatActivity {
    private TextView backButton;
    private Button registerButton;
    private TextInputEditText reg_centerName, reg_centerLocation, reg_centerAddress, reg_centerPhone, reg_centerEmail, reg_centerPass;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_registration);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CenterRegistration.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton = findViewById(R.id.registerButton);
        reg_centerName = findViewById(R.id.reg_centerName);
        reg_centerLocation = findViewById(R.id.reg_centerLocation);
        reg_centerAddress = findViewById(R.id.reg_centerAddress);
        reg_centerPhone = findViewById(R.id.reg_centerPhone);
        reg_centerEmail = findViewById(R.id.reg_centerEmail);
        reg_centerPass = findViewById(R.id.reg_centerPass);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String centerName =  reg_centerName.getText().toString().trim();
                final String centerLocation =  reg_centerLocation.getText().toString().trim();
                final String centerAddress =  reg_centerAddress.getText().toString().trim();
                final String centerPhone =  reg_centerPhone.getText().toString().trim();
                final String centerEmail =  reg_centerEmail.getText().toString().trim();
                final String centerPassword =  reg_centerPass.getText().toString().trim();


                if(TextUtils.isEmpty(centerName)){
                    reg_centerName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(centerLocation)){
                    reg_centerLocation.setError("Location is required!");
                    return;
                }
                if(TextUtils.isEmpty(centerAddress)){
                    reg_centerAddress.setError("Address is required!");
                    return;
                }
                if(TextUtils.isEmpty(centerPhone)){
                    reg_centerPhone.setError("Phone Number is required!");
                    return;
                }
                if(TextUtils.isEmpty(centerEmail)){
                    reg_centerEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(centerPassword)){
                    reg_centerPass.setError("Password is required!");
                    return;
                }

                else{
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(centerEmail, centerPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(CenterRegistration.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("centers").child(currentUserId);

                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("centerName",centerName);
                                userInfo.put("centerLocation",centerLocation);
                                userInfo.put("centerAddress",centerAddress);
                                userInfo.put("centerPhone",centerPhone);
                                userInfo.put("centerEmail",centerEmail);
                                userInfo.put("type","center");


                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(CenterRegistration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(CenterRegistration.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        //loader.dismiss();
                                    }
                                });

                                //Add Intent here
                                Intent intent = new Intent(CenterRegistration.this, CenterMain.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();

                                //end of upload image to db
                            }
                        }
                    });

                }
            }
        });


    }
}