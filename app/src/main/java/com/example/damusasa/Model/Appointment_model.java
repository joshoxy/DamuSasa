package com.example.damusasa.Model;

public class Appointment_model {
    String donor_name, donation_center, address, time;

    public void setDonor_name(String donor_name) {
        this.donor_name = donor_name;
    }

    public void setDonation_center(String donation_center) {
        this.donation_center = donation_center;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDonor_name() {
        return donor_name;
    }

    public String getDonation_center() {
        return donation_center;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }
}
