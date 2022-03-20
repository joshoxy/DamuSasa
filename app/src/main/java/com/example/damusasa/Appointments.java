package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Appointments extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView donorName, centerName, centerAddress, Time;

    //Appointments for the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Appointments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        donorName = findViewById(R.id.txt_name);
        centerName = findViewById(R.id.txt_center_name);
        centerAddress = findViewById(R.id.txt_location);
        Time = findViewById(R.id.txt_time);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("appointments").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    donorName.setText(snapshot.child("CustomerName").getValue().toString());
                    centerName.setText(snapshot.child("centerName").getValue().toString());
                    centerAddress.setText(snapshot.child("CenterAddress").getValue().toString());
                    Time.setText(snapshot.child("Time").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}