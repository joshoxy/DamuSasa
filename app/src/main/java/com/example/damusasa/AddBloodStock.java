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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddBloodStock extends AppCompatActivity {
    private TextInputEditText blood_vol;
    private Spinner add_spinner;
    private Button addBtn;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blood_stock);

        blood_vol = findViewById(R.id.blood_vol);
        add_spinner = findViewById(R.id.add_spinner);
        addBtn = findViewById(R.id.addBtn);
        loader = new ProgressDialog(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Volume =  blood_vol.getText().toString().trim();
                final String blood_type = add_spinner.getSelectedItem().toString();
                String date_added = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(new Date());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,42);  //add 42 to the current date to determine expiry date
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                String expiry_date = sdf.format(calendar.getTime());

                if(TextUtils.isEmpty(Volume)){
                    blood_vol.setError("Blood Volume is required!");
                    return;
                }
                else {
                    loader.setMessage("Please wait...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //reference to get center name and location
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String center_name = snapshot.child("centerName").getValue().toString();
                            String center_location = snapshot.child("centerLocation").getValue().toString();

                            //Create reference of the blood stock table
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                    .child("blood_stock").push();
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String ref_Id = snapshot.getKey();

                                    HashMap blood_stock = new HashMap();
                                    blood_stock.put("blood_type",blood_type);
                                    blood_stock.put("blood_volume",Volume);
                                    blood_stock.put("date_added",date_added);
                                    blood_stock.put("expiry_date",expiry_date);
                                    blood_stock.put("center_name",center_name);
                                    blood_stock.put("center_location",center_location);
                                    blood_stock.put("ref_Id",ref_Id);
                                    reference1.updateChildren(blood_stock).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(AddBloodStock.this, "Successfully added !", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddBloodStock.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent = new Intent(AddBloodStock.this, SelectBloodStock.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });
                            //end of ref to blood stock table

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //end of ref to get center details


                }
                //end of else to add new stock
            }
        });
        //end of btn add on click

    }
}