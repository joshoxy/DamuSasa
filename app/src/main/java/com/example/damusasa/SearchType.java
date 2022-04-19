package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.damusasa.Adapter.AppointmentsAdapter;
import com.example.damusasa.Adapter.BloodStock_Adapter;
import com.example.damusasa.Adapter.SearchType_adapter;
import com.example.damusasa.Model.BloodStock_model;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchType extends AppCompatActivity {
    private Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseReference database;
    SearchType_adapter myAdapter;
    ArrayList<BloodStock_model> list;
    Button searchBtn;
    ProgressDialog loader;
    Spinner search_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_type);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood stock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycler_stock);
        database = FirebaseDatabase.getInstance().getReference("blood_stock");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new SearchType_adapter(this, list);
        recyclerView.setAdapter(myAdapter);

        searchBtn = findViewById(R.id.searchBtn);
        search_type = findViewById(R.id.search_type);
        loader = new ProgressDialog(this);

        //Display blood stock to list
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BloodStock_model bloodStockModel = dataSnapshot.getValue(BloodStock_model.class);
                    list.add(bloodStockModel);

                    String blood_type = dataSnapshot.child("blood_type").getValue().toString();

                    for(int i=0; i < list.size(); i++){
                        for(int j=0; j < list.size(); j++){
                            if (list.get(i).equals(list.get(j))){

                            }
                        }
                    }

                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //onClick for search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                myAdapter.notifyDataSetChanged();
                String type = search_type.getSelectedItem().toString();

                if(type.equals("Select your blood group")){
                    Toast.makeText(SearchType.this, "Select a blood group", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    DatabaseReference refSearch = FirebaseDatabase.getInstance().getReference()
                            .child("blood_stock");

                    Query querySearch = refSearch.orderByChild("blood_type").equalTo(type);
                    querySearch.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                BloodStock_model bloodStockModel = dataSnapshot.getValue(BloodStock_model.class);
                                list.add(bloodStockModel);
                            }
                            myAdapter.notifyDataSetChanged();
                            loader.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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