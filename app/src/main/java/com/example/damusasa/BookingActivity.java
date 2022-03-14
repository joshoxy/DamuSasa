package com.example.damusasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.damusasa.Adapter.MyViewPagerAdapter;
import com.example.damusasa.Common.Common;
import com.example.damusasa.Common.NonSwipeViewPager;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingActivity extends AppCompatActivity {
    LocalBroadcastManager localBroadcastManager;
    @BindView(R.id.step_view) StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;


    @OnClick(R.id.btn_previous_step)
    void previousStep(){
        if (Common.step == 3 || Common.step > 0){  //Changed them from 3
            Common.step--;
            viewPager.setCurrentItem(Common.step);

            if (Common.step < 2) //Always enable next button
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }
    //Event
    @OnClick(R.id.btn_next_step)
    void nextClick(){
        if (Common.step < 3 || Common.step == 0){   //Changed them from 3
            Common.step++; //increase
            if (Common.step == 1) //After Choosing Center
            {
                //this is his currentSalon
                if (Common.currentBarber != null){

                }
            }
            /*else if (Common.step == 2) //Choose time
            {
                loadTimeSlot(Common.currentBarber.getBranchId());  //This is different from his
            }*/

            else if (Common.step == 2) //Choose time
            {
                if (Common.currentTimeSlot != -1)   //Might need to remove
                confirmBooking();  //This is different from his
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        //Send broadcast to step 3
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlot(String branchId) {
        //Send broadcast to fragment 2
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    //Broadcast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, 0); //Might need to disable this
            if (step == 1)
                //this is his currentSalon
            Common.currentBarber = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step==2)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);

            //His step 2 is barber select and my step 2 is time select

            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    private Button btn_previous_step, btn_next_step;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_next_step =  findViewById(R.id.btn_next_step);
        btn_previous_step = findViewById(R.id.btn_previous_step);

        setupStepView();
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3); //We have three fragments
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //Show step
                stepView.go(position, true);
                if (position == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);
                //Set disable button
                btn_next_step.setEnabled(false);
                setColorButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.drawable.step_buttons_background);
        }else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_previous_step.isEnabled()){
            btn_previous_step.setBackgroundResource(R.drawable.step_buttons_background);
        }else {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Pick a donation center");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
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