package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.damusasa.Adapter.UserAdapter;
import com.example.damusasa.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class CenterMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_center_view;

    private TextView center_fullname, center_email, center_type, center_location;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<User> userList;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_main);

        toolbar = findViewById(R.id.center_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DamuLiza");

        drawerLayout = findViewById(R.id.center_drawerLayout);
        nav_center_view = findViewById(R.id.nav_center_view);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(CenterMain.this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_center_view.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.center_progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.center_recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(CenterMain.this, userList);

        recyclerView.setAdapter(userAdapter);
        readUsers();

        center_fullname = nav_center_view.getHeaderView(0).findViewById(R.id.center_fullname);
        center_location = nav_center_view.getHeaderView(0).findViewById(R.id.center_location);
        center_email = nav_center_view.getHeaderView(0).findViewById(R.id.center_email);
        center_type = nav_center_view.getHeaderView(0).findViewById(R.id.center_type);

        //Fetch data from firebase and display it to those views
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("centerName").getValue().toString();  //Get user name from name field in Firebase
                    center_fullname.setText(name); //Set the center_fullname to display name from Firebase

                    String location = snapshot.child("centerLocation").getValue().toString();
                    center_location.setText(location);

                    String email = snapshot.child("centerEmail").getValue().toString();
                    center_email.setText(email);

                    String type = snapshot.child("type").getValue().toString();
                    center_type.setText(type);


                    //Change menu based on who is logged in **
                    Menu nav_menu = nav_center_view.getMenu();

                    /*if (type.equals("donor")){
                        nav_menu.findItem(R.id.sentEmail).setTitle("Received requests");
                        nav_menu.findItem(R.id.notifications).setVisible(true);
                        nav_menu.findItem(R.id.book).setVisible(true);
                        nav_menu.findItem(R.id.appointments).setVisible(true);
                    }*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readDonors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){
                    Toast.makeText(CenterMain.this, "No donors found !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){
                    Toast.makeText(CenterMain.this, "No recipients found !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Query query = reference.orderByChild("type").equalTo("donor");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        userAdapter.notifyDataSetChanged();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            userList.add(user);
                        }

                        Query query1 = reference.orderByChild("type").equalTo("recipient");
                        query1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    User user1 = dataSnapshot.getValue(User.class);
                                    userList.add(user1);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        userAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                        if (userList.isEmpty()){
                            Toast.makeText(CenterMain.this, "No users found !", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){
                    Toast.makeText(AdminPage.this, "No users found !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.aplus:
                Intent intent3 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent3.putExtra("group", "A+");
                startActivity(intent3);
                break;

            case R.id.aminus:
                Intent intent4 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent4.putExtra("group", "A-");
                startActivity(intent4);
                break;

            case R.id.bplus:
                Intent intent5 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent5.putExtra("group", "B+");
                startActivity(intent5);
                break;

            case R.id.bminus:
                Intent intent6 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent6.putExtra("group", "B-");
                startActivity(intent6);
                break;

            case R.id.abplus:
                Intent intent7 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent7.putExtra("group", "AB+");
                startActivity(intent7);
                break;

            case R.id.abminus:
                Intent intent8 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent8.putExtra("group", "AB-");
                startActivity(intent8);
                break;

            case R.id.oplus:
                Intent intent9 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent9.putExtra("group", "O+");
                startActivity(intent9);
                break;

            case R.id.ominus:
                Intent intent10 = new Intent(CenterMain.this, CategorySelectedActivity.class);
                intent10.putExtra("group", "O-");
                startActivity(intent10);
                break;


            case R.id.requestCenter:  //Add Intent for Requesting activity
                Intent intent12 = new Intent(CenterMain.this, RequestDonation.class);
                startActivity(intent12);
                break;

            case R.id.center_appointments: //Add Intent for Viewing appointments
                Intent intent15 = new Intent(CenterMain.this, All_Appointments.class);
                startActivity(intent15);
                break;

            case R.id.accepted_requests: //Add Intent for Viewing accepted requests
                Intent intent16 = new Intent(CenterMain.this, Accepted_requests.class);
                startActivity(intent16);
                break;

            case R.id.blood_drives: //Add Intent for Viewing set blood drives
                Intent intent17 = new Intent(CenterMain.this, BloodDrivesSet.class);
                startActivity(intent17);
                break;

            case R.id.members_drive: //Add Intent for Viewing attending blood drives
                Intent intent18 = new Intent(CenterMain.this, Blood_drive_members.class);
                startActivity(intent18);
                break;

            case R.id.blood_stock: //Add Intent for choosing stock action
                Intent intent19 = new Intent(CenterMain.this, SelectBloodStock.class);
                startActivity(intent19);
                break;

            case R.id.predict: //Add Intent for predict page
                Intent intent20 = new Intent(CenterMain.this, ML_Model.class);
                startActivity(intent20);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(CenterMain.this, LoginActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}