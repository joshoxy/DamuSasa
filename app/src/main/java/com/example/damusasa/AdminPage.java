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

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;

    private CircleImageView nav_profile_image;
    private TextView admin_fullname, admin_email, nav_bloodgroup, admin_type;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DamuLiza admin");

        drawerLayout = findViewById(R.id.admin_drawerLayout);
        nav_view = findViewById(R.id.nav_admin_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(AdminPage.this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.admin_progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.admin_recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(AdminPage.this, userList);

        recyclerView.setAdapter(userAdapter);

        //Read List of members based on who is logged in
        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("donor")) {
                    readRecipients();
                } else {
                    readDonors();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        readUsers();

        admin_fullname = nav_view.getHeaderView(0).findViewById(R.id.admin_fullname);
        admin_email = nav_view.getHeaderView(0).findViewById(R.id.admin_email);
        admin_type = nav_view.getHeaderView(0).findViewById(R.id.admin_type);

        //Fetch data from firebase and display it to those views
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();  //Get user name from name field in Firebase
                    admin_fullname.setText(name); //Set the nav_fullname to display name from Firebase

                    String email = snapshot.child("email").getValue().toString();  //Get email from name field in Firebase
                    admin_email.setText(email); //Set the nav_email to display name from Firebase

                    String type = snapshot.child("type").getValue().toString();
                    admin_type.setText(type);

                    /*if (snapshot.hasChild("profilepictureurl")){
                        //Fetch image from firebase using Glider dependency
                        String imageUrl = snapshot.child("profilepictureurl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);
                    }else{
                        nav_profile_image.setImageResource(R.drawable.profile_image);
                    }*/

                    //Change menu based on who is logged in **
                    Menu nav_menu = nav_view.getMenu();

                }
                else {
                    Toast.makeText(AdminPage.this, "Snapshot does not exists", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminPage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            userList.add(user);
                        }

                        Query query1 = reference.orderByChild("type").equalTo("recipient");
                        query1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    User user = dataSnapshot.getValue(User.class);
                                    userList.add(user);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        userAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                        if (userList.isEmpty()){
                            Toast.makeText(AdminPage.this, "No users found !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AdminPage.this, "No recipients found !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
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
                    Toast.makeText(AdminPage.this, "No donors found !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.center_appointments:  //View donation center appointments
                Intent intent12 = new Intent(AdminPage.this, All_Appointments.class); //set intent
                startActivity(intent12);
                break;

            case R.id.user_donation_requests:  //View donation requests sent by users
                Intent intent13 = new Intent(AdminPage.this, SentEmailsActivity.class); //set intent
                startActivity(intent13);
                break;

            case R.id.accepted_center_requests:  //View donation center requests accepted
                Intent intent14 = new Intent(AdminPage.this, Accepted_requests.class);  //set intent
                startActivity(intent14);
                break;

            case R.id.blood_drive:  //New Blood drive page
                Intent intent15 = new Intent(AdminPage.this, BloodDrive.class);  //set intent
                startActivity(intent15);
                break;

            case R.id.blood_drives:  //Blood drives set
                Intent intent16 = new Intent(AdminPage.this, BloodDrivesSet.class);  //set intent
                startActivity(intent16);
                break;

            case R.id.members_drive: //Add Intent for Viewing attending blood drives
                Intent intent18 = new Intent(AdminPage.this, Blood_drive_members.class);
                startActivity(intent18);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(AdminPage.this, LoginActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}