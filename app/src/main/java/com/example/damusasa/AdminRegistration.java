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

public class AdminRegistration extends AppCompatActivity {
    private TextView backButton;
    private Button registerButton;
    private TextInputEditText reg_adminName, reg_adminPhone, reg_adminEmail, reg_adminPass;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminRegistration.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton = findViewById(R.id.registerButton);
        reg_adminName = findViewById(R.id.reg_adminName);
        reg_adminPhone = findViewById(R.id.reg_adminPhone);
        reg_adminEmail = findViewById(R.id.reg_adminEmail);
        reg_adminPass = findViewById(R.id.reg_adminPass);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String adminName =  reg_adminName.getText().toString().trim();
                final String adminPhone =  reg_adminPhone.getText().toString().trim();
                final String adminEmail =  reg_adminEmail.getText().toString().trim();
                final String adminPass =  reg_adminPass.getText().toString().trim();

                if(TextUtils.isEmpty(adminName)){
                    reg_adminName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(adminPhone)){
                    reg_adminPhone.setError("Phone number is required!");
                    return;
                }
                if(TextUtils.isEmpty(adminEmail)){
                    reg_adminEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(adminPass)){
                    reg_adminPass.setError("Password is required!");
                    return;
                }

                else{
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(adminEmail, adminPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(AdminRegistration.this, "Error" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                            else{
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentUserId);

                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("adminName",adminName);
                                userInfo.put("adminPhone",adminPhone);
                                userInfo.put("adminEmail",adminEmail);
                                userInfo.put("type","Admin");

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(AdminRegistration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(AdminRegistration.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        //loader.dismiss();
                                    }
                                });

                                //Add Intent here
                                Intent intent = new Intent(AdminRegistration.this, AdminPage.class);
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