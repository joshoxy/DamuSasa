package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damusasa.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextView backButton;
    private TextInputEditText login_Email, login_pass;
    private TextView forgotPass;
    private Button loginButton, admin_loginButton;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String type = snapshot.child("type").getValue().toString();

                            if (type.equals("center")){
                                Intent intent = new Intent(LoginActivity.this, CenterMain.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (type.equals("Admin")){
                                Intent intent3 = new Intent(LoginActivity.this, AdminPage.class);
                                startActivity(intent3);
                                finish();
                            }

                            else
                            {
                                Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent2);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();*/

                } //end of if


            }
        };


        backButton = findViewById(R.id.backButton);
        login_Email = findViewById(R.id.login_Email);
        login_pass = findViewById(R.id.login_pass);
        forgotPass = findViewById(R.id.forgotPass);
        loginButton = findViewById(R.id.loginButton);
        admin_loginButton = findViewById(R.id.admin_loginButton);
        loader = new ProgressDialog(this);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SelectRegistrationActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = login_Email.getText().toString().trim();
                final String password = login_pass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    login_Email.setError("Email is required !");
                }

                if (TextUtils.isEmpty(password)){
                    login_pass.setError("Password is required !");
                }
                else{
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();
                        }
                    });
                }
            }
        });

        admin_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Set intent to open admin page
                Intent intent = new Intent(LoginActivity.this, AdminPage.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}