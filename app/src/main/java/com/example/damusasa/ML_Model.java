package com.example.damusasa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ML_Model extends AppCompatActivity {

    private TextInputEditText recency, first_don, total_don, total_blood_donated;
    private Button predict_button;
    TextView result;
    String url = "https://damuliza.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ml_model);

        predict_button = findViewById(R.id.predict_button);
        recency = findViewById(R.id.recency);
        first_don = findViewById(R.id.first_don);
        total_don = findViewById(R.id.total_don);
        total_blood_donated = findViewById(R.id.total_blood_donated);
        result = findViewById(R.id.result);

        predict_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hit the API -> Volley
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String data = jsonObject.getString("donated blood in March 2007");

                                    if(data.equals("1")){
                                        result.setText("Donated");
                                    }else{
                                        result.setText("Not donated");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ML_Model.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        })

                {
                    @Override
                    protected Map getParams(){
                        Map params = new HashMap();
                        params.put("recency",recency.getText().toString());
                        params.put("first_donation",first_don.getText().toString());
                        params.put("total_donations",total_don.getText().toString());
                        params.put("total_blood_donated",total_blood_donated.getText().toString());
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(ML_Model.this);
                queue.add(stringRequest);
            }
        });

    }
}