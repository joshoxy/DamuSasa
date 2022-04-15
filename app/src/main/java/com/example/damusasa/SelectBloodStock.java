package com.example.damusasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectBloodStock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button viewStock, addStock;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_blood_stock);
        viewStock = findViewById(R.id.viewStock);
        addStock = findViewById(R.id.addStock);

        //On Click to view the blood stock page
        viewStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBloodStock.this, BloodStock.class);
                startActivity(intent);
                finish();
            }
        });

        //OnClick to add new blood stock
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBloodStock.this, AddBloodStock.class);
                startActivity(intent);
                finish();
            }
        });
    }
}