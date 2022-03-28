package com.example.damusasa.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.damusasa.Email.JavaMailApi;
import com.example.damusasa.Model.User;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_displayed_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.type.setText(user.getType());

        //Filter donors out
        if (user.getType().equals("donor")){
            holder.emailNow.setVisibility(View.VISIBLE);
        }

        holder.userEmail.setText(user.getEmail());
        holder.phoneNumber.setText(user.getPhoneNumber());
        holder.userName.setText(user.getName());
        holder.bloodGroup.setText(user.getBloodGroup());

        //Hide button
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("center")){
                    holder.emailNow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Fetch image with glide
        Glide.with(context).load(user.getProfilepictureurl()).into(holder.userProfileImage);

        final String nameOfTheReceiver = user.getName();
        final String idOfTheReceiver = user.getIdNumber();

        //Set on click to send email
        holder.emailNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm")
                        .setMessage("Request donation from "+user.getName() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String nameOfSender = snapshot.child("name").getValue().toString();
                                        String email = snapshot.child("email").getValue().toString();
                                        String phone = snapshot.child("phoneNumber").getValue().toString();
                                        String blood = snapshot.child("bloodGroup").getValue().toString();

                                        String mEmail = user.getEmail();
                                        String mSubject = "New blood donation request";
                                        String mMessage = "Hello "+ nameOfTheReceiver+", "+nameOfSender+ " of blood group "+blood+
                                                " has requested a donation. Kindly reach out to them in the following ways:\n"
                                                +"Phone number: "+phone+ "\n"+
                                                        "Email: " +email+ "\n"+
                                            "Your help would be greatly appreciated !";

                                        JavaMailApi javaMailApi = new JavaMailApi(context, mEmail, mSubject, mMessage);
                                        javaMailApi.execute();

                                        //Add the request to the database
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                                                .child("recipient_requests").push();
                                        reference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String donor_name = user.getName();
                                                String donor_type = user.getBloodGroup();
                                                String donor_phone = user.getPhoneNumber();
                                                HashMap userInfo = new HashMap();
                                                userInfo.put("recipient_name", nameOfSender);
                                                userInfo.put("recipient_blood", blood);
                                                userInfo.put("recipient_phone", phone);
                                                userInfo.put("recipient_email", email);
                                                userInfo.put("donor_name", donor_name);
                                                userInfo.put("donor_blood_type", donor_type);
                                                userInfo.put("donor_phone", donor_phone);
                                                userInfo.put("status", "Pending");

                                                reference1.updateChildren(userInfo);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        /*DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("emails")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        senderRef.child(idOfTheReceiver).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("emails")
                                                            .child(idOfTheReceiver);
                                                    receiverRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                    addNotifications(idOfTheReceiver, FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                }

                                            }
                                        });*/
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public CircleImageView userProfileImage;
        public TextView userName, type, phoneNumber, bloodGroup, userEmail;
        public Button emailNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            type = itemView.findViewById(R.id.type);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            bloodGroup = itemView.findViewById(R.id.bloodGroup);
            userEmail = itemView.findViewById(R.id.userEmail);
            emailNow = itemView.findViewById(R.id.emailNow);
        }
    }

    private void addNotifications(String receiverId, String senderId){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("notifications").child(receiverId);

        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("receiverId", receiverId);
        hashMap.put("senderId", senderId);
        hashMap.put("text", "Made a request, kindly check it out");
        hashMap.put("date", date);

        reference.push().setValue(hashMap);
    }
}
