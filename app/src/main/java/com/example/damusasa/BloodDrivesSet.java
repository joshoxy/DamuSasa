package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.damusasa.Adapter.AppointmentsAdapter;
import com.example.damusasa.Adapter.Blood_drives_adapter;
import com.example.damusasa.Model.Appointment_model;
import com.example.damusasa.Model.Blood_Drives_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BloodDrivesSet extends AppCompatActivity {
    RecyclerView recyclerDrives;
    private Toolbar toolbar;
    DatabaseReference database;
    Blood_drives_adapter blood_drives_adapter;
    ArrayList<Blood_Drives_Model> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_drives_set);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Drives");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        list = new ArrayList<>();
        recyclerDrives = findViewById(R.id.recyclerDrives);
        database = FirebaseDatabase.getInstance().getReference("blood drives");
        recyclerDrives.setHasFixedSize(true);
        recyclerDrives.setLayoutManager(new LinearLayoutManager(this));

        Collections.sort(list, new Comparator<Blood_Drives_Model>() {
            @Override
            public int compare(Blood_Drives_Model lhs, Blood_Drives_Model rhs) {
                return rhs.getDate().compareToIgnoreCase(lhs.getDate());
            }
        });

        blood_drives_adapter = new Blood_drives_adapter(this, list);
        recyclerDrives.setAdapter(blood_drives_adapter);

        //Display blood drive based on who is logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();

                //center is supposed to see on their blood drive
                if (type.equals("center")){
                    String centerName = snapshot.child("centerName").getValue().toString();

                    //Center name from drive table is same as center name of logged in user
                    Query query = database.orderByChild("donationCenter").equalTo(centerName);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Blood_Drives_Model blood_drives_model = dataSnapshot.getValue(Blood_Drives_Model.class);
                                list.add(blood_drives_model);
                            }
                            blood_drives_adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                //the rest are supposed to see all blood drives
                else {
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Blood_Drives_Model blood_drives_model = dataSnapshot.getValue(Blood_Drives_Model.class);
                                list.add(blood_drives_model);
                            }
                            blood_drives_adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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