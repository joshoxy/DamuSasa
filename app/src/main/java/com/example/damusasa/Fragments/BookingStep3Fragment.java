package com.example.damusasa.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Date;

import com.example.damusasa.Common.Common;
import com.example.damusasa.Model.BookingInformation;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.text.format.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BookingStep3Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    Unbinder unbinder;
    private DatabaseReference userDatabaseRef;
    private FirebaseAuth mAuth;
    CollectionReference branchRef;

    @BindView(R.id.booking_time)
    TextView booking_time;

    @BindView(R.id.txt_location)
    TextView txt_location;

    @BindView(R.id.txt_center_name)
    TextView txt_center_name;

    @BindView(R.id.txt_phone)
    TextView txt_phone;

    @OnClick(R.id.btn_confirm)

    void confirmBooking(){

        //Create booking information
        BookingInformation bookingInformation = new BookingInformation();

        String CenterId = bookingInformation.setCenterId(Common.currentBarber.getBranchId());
        String CenterAddress = bookingInformation.setCenterAddress(Common.currentBarber.getAddress());
        String CenterName = bookingInformation.setCenterName(Common.currentBarber.getName());
        String Time = bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" on ")
                .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
        String Slot = bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String CustomerName = snapshot.child("name").getValue().toString();
                    String CustomerPhone = snapshot.child("phoneNumber").getValue().toString();
                    String date = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(new Date());
                    String date2 = new StringBuilder().append(simpleDateFormat.format(Common.currentDate.getTime())).toString();

                    //Upload
                    userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("appointments").push();
                    userDatabaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String ref_Id = snapshot.getKey();
                            HashMap userInfo = new HashMap();
                            userInfo.put("CustomerName",CustomerName);
                            userInfo.put("CustomerPhone",CustomerPhone);
                            userInfo.put("CenterId",CenterId);
                            userInfo.put("centerName",CenterName);
                            userInfo.put("CenterAddress",CenterAddress);
                            userInfo.put("Time",Time);
                            userInfo.put("BookingDate",date2);
                            userInfo.put("ref_Id", ref_Id);

                            userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    resetStaticActivity();  //Allow user to book again
                                    //getActivity().finish(); //Close Activity
                                    //Toast.makeText(getContext(), "Successfully booked!", Toast.LENGTH_SHORT).show();
                                    finishActivity();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //End of upload code

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String parseDateToddMMyyyy(String date) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date1 = null;
        String str = null;

        try {
            date1 = inputFormat.parse(date);
            str = outputFormat.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void resetStaticActivity() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentBarber = null;
        Common.currentDate.add(Calendar.DATE, 0);  //Current date
    }

    private void finishActivity() {
        if(getActivity() != null) {
            getActivity().finish();
            Toast.makeText(getContext(), "Successfully booked!", Toast.LENGTH_SHORT).show();
        }
    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();

        }
    };

    private void setData() {
        booking_time.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append(" on ")
        .append(simpleDateFormat.format(Common.currentDate.getTime())));
        txt_location.setText(Common.currentBarber.getAddress());
        txt_center_name.setText(Common.currentBarber.getName());
        txt_phone.setText(Common.currentBarber.getPhone());

        //Mine

    }



    static BookingStep3Fragment instance;

    public static BookingStep3Fragment getInstance() {
        if (instance==null)
            instance = new BookingStep3Fragment();
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("MMM dd yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_three, container,false);
        unbinder = ButterKnife.bind(this, itemView);
        return itemView;
    }
}
