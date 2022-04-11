package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.damusasa.Adapter.AppointmentsAdapter;
import com.example.damusasa.Model.Appointment_model;
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

public class All_Appointments extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseReference database;
    AppointmentsAdapter myAdapter;
    ArrayList<Appointment_model> list;

    //Appointments for donation center

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_appointments);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Appointments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerAppoint);
        database = FirebaseDatabase.getInstance().getReference("appointments");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Collections.sort(list, new Comparator<Appointment_model>() {
            @Override
            public int compare(Appointment_model lhs, Appointment_model rhs) {
                return lhs.getBookingDate().compareToIgnoreCase(rhs.getBookingDate());
            }
        });

        myAdapter = new AppointmentsAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        //Display appointments based on who is logged in
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();

                if (type.equals("center")){
                    String centerName = snapshot.child("centerName").getValue().toString();

                    //Center name from appointments table is same as center name of logged in user
                    Query query = database.orderByChild("centerName").equalTo(centerName);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Appointment_model user = dataSnapshot.getValue(Appointment_model.class);
                                list.add(user);
                            }
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                //If donor is logged in
                else if (type.equals("donor")){
                    String name = snapshot.child("name").getValue().toString();

                    //if name from appointments table is same as name of logged in user
                    Query query = database.orderByChild("CustomerName").equalTo(name);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Appointment_model user = dataSnapshot.getValue(Appointment_model.class);
                                list.add(user);
                            }
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                else if (type.equals("Admin")) {
                    //If admin is logged in show all appointments
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("appointments");
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Appointment_model user = dataSnapshot.getValue(Appointment_model.class);
                                list.add(user);
                            }
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /*database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Appointment_model user = dataSnapshot.getValue(Appointment_model.class);
                                list.add(user);
                            }
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/
                    //end of dbRef

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Show all appointments

        /*database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Appointment_model user = dataSnapshot.getValue(Appointment_model.class);
                    list.add(user);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

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