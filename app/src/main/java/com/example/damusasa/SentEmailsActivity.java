package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.damusasa.Adapter.Recipient_Requests_Adapter;
import com.example.damusasa.Adapter.UserAdapter;
import com.example.damusasa.Model.Recipient_Requests_Model;
import com.example.damusasa.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SentEmailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    DatabaseReference database;
    Recipient_Requests_Adapter recipient_requests_adapter;
    ArrayList<Recipient_Requests_Model> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_emails);

        //Code to show toolbar title on top
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setTitle("Requests");*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.request_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance().getReference("recipient_requests");

        list = new ArrayList<>();
        recipient_requests_adapter = new Recipient_Requests_Adapter(this, list);
        recyclerView.setAdapter(recipient_requests_adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String donor_name = snapshot.child("name").getValue().toString();
                String type = snapshot.child("type").getValue().toString();

                //If donor, run query to show recipient requests
                if (type.equals("donor")){
                    getSupportActionBar().setTitle("Received requests");
                    //Query for donor logged in
                    Query query = database.orderByChild("donor_name").equalTo(donor_name);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Recipient_Requests_Model model = dataSnapshot.getValue(Recipient_Requests_Model.class);
                                list.add(model);
                            }
                            recipient_requests_adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                //If recipient run query to show donor details
                else{
                    getSupportActionBar().setTitle("Sent requests");
                    //Query for recipient logged in
                    Query query = database.orderByChild("recipient_name").equalTo(donor_name);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Recipient_Requests_Model model = dataSnapshot.getValue(Recipient_Requests_Model.class);
                                list.add(model);
                            }
                            recipient_requests_adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                //Query for donor logged in
                /*Query query = database.orderByChild("donor_name").equalTo(donor_name);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Recipient_Requests_Model model = dataSnapshot.getValue(Recipient_Requests_Model.class);
                            list.add(model);
                        }
                        recipient_requests_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
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