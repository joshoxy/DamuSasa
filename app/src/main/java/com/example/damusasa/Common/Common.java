package com.example.damusasa.Common;

import com.example.damusasa.Model.Branch;
import com.example.damusasa.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_SAVE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static Branch currentBarber; //Donation center..this is his Salon currentSalon
    public static int step;
    public static String city ="";
    public static int currentTimeSlot = -1;
    public static Calendar currentDate = Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static User currentUser;

    public static String convertTimeSlotToString(int slot) {
        switch (slot){
            case 0: return "9:00-9:30 am";
            case 1: return "9:30-10:00 am";
            case 2: return "10:00-10:30 am";
            case 3: return "10:30-11:00 am";
            case 4: return "11:00-11:30 am";
            case 5: return "11:30-12:00 pm";
            case 6: return "12:00-12:30 pm";
            case 7: return "12:30-1:00 pm";
            case 8: return "1:00-1:30 pm";
            case 9: return "1:30-2:00 pm";
            case 10: return "2:00-2:30 pm";
            case 11: return "2:30-3:00 pm";
            case 12: return "3:00-3:30 pm";
            case 13: return "3:30-4:00 pm";
            case 14: return "4:00-4:30 pm";
            case 15: return "4:30-5:00 pm";
            case 16: return "5:00-5:30 pm";
            case 17: return "5:30-6:00 pm";
            case 18: return "6:00-6:30 pm";
            case 19: return "6:30-7:00 pm";
            default: return "Closed";
        }
    }
}
