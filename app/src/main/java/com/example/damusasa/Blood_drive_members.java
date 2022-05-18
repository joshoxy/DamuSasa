package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.damusasa.Adapter.Blood_drive_members_adapter;
import com.example.damusasa.Adapter.Blood_drives_adapter;
import com.example.damusasa.Model.Blood_Drives_Model;
import com.example.damusasa.Model.blood_drive_members_model;
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

public class Blood_drive_members extends AppCompatActivity {
    RecyclerView recycler_members;
    private Toolbar toolbar;
    DatabaseReference database;
    ArrayList<blood_drive_members_model> list;
    Blood_drive_members_adapter members_adapter;

    //Activity to show blood drive that user is attending

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_drive_members);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Drives");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        list = new ArrayList<>();
        recycler_members = findViewById(R.id.recycler_members);
        database = FirebaseDatabase.getInstance().getReference("blood_drive_members");
        recycler_members.setHasFixedSize(true);
        recycler_members.setLayoutManager(new LinearLayoutManager(this));

        Collections.sort(list, new Comparator<blood_drive_members_model>() {
            @Override
            public int compare(blood_drive_members_model lhs, blood_drive_members_model rhs) {
                return rhs.getDrive_date().compareToIgnoreCase(lhs.getDrive_date());
            }
        });

        members_adapter = new Blood_drive_members_adapter(this, list);
        recycler_members.setAdapter(members_adapter);

        //Display blood drive members based on who is logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();

                //center is supposed to see only their blood drive
                if (type.equals("center")){
                    String centerName = snapshot.child("centerName").getValue().toString();

                    //Center name from drive table is same as center name of logged in user
                    Query query = database.orderByChild("drive_center").equalTo(centerName);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                blood_drive_members_model membersModel = dataSnapshot.getValue(blood_drive_members_model.class);
                                list.add(membersModel);
                            }
                            members_adapter.notifyDataSetChanged();
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
                                blood_drive_members_model membersModel = dataSnapshot.getValue(blood_drive_members_model.class);
                                list.add(membersModel);
                            }
                            members_adapter.notifyDataSetChanged();
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