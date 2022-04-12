package com.example.damusasa.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damusasa.Adapter.MyTimeSlotAdapter;
import com.example.damusasa.Common.Common;
import com.example.damusasa.Common.SpacesItemDecoration;
import com.example.damusasa.Interface.ITimeSlotLoadListener;
import com.example.damusasa.Model.TimeSlot;
import com.example.damusasa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class BookingStep2Fragment extends Fragment implements ITimeSlotLoadListener {

    //Assign variables
    DocumentReference centerDoc; //His barberDoc is here
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    /*Calendar selected_date;*/

    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;

    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;
    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);  //Add current date
            //loadAvailableTimeSlot(Common.currentBarber.getBranchId(), simpleDateFormat.format(date.getTime()));

            //New method without
            loadAvailableTimeSlot();
        }
    };
    //New method testing

    private void loadAvailableTimeSlot() {
        dialog.show();
        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
        ///DonationCenters/Westlands/Branch/tAi9tXv8JadaPhuZuvZn
        /*centerDoc = FirebaseFirestore.getInstance()
                .collection("DonationCenters")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentBarber.getBranchId());

        centerDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) //If center is available
                    {
                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
            }
        });*/

    }

    //Original method

    /*private void loadAvailableTimeSlot(String branchId, String date) {
        dialog.show();
        ///DonationCenters/Westlands/Branch/tAi9tXv8JadaPhuZuvZn
        centerDoc = FirebaseFirestore.getInstance()
                .collection("DonationCenters")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentBarber.getBranchId());

        centerDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) //If center is available
                    {
                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
            }
        });

    }*/

    static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance() {
        if (instance==null)
            instance = new BookingStep2Fragment();
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iTimeSlotLoadListener = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot, new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));
        simpleDateFormat = new SimpleDateFormat("MMM dd yyyy");

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        /*selected_date = Calendar.getInstance();
        selected_date.add(Calendar.DATE,0); //Current Date*/
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_two, container,false);
        unbinder = ButterKnife.bind(this, itemView);
        init(itemView);
        return itemView;
    }

    private void init(View itemView) {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);  //Might need to be 2
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 5);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.currentDate.getTimeInMillis() != date.getTimeInMillis()){
                    Common.currentDate = date;
                   /* loadAvailableTimeSlot(Common.currentBarber.getBranchId(),
                            simpleDateFormat.format(date.getTime()));*/

                    //New method
                     loadAvailableTimeSlot();

                }
            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(), timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();

    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();

    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();

    }
}
