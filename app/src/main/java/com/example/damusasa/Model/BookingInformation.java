package com.example.damusasa.Model;

public class BookingInformation {
    private String customerName, customerPhone, time, centerId, centerName, centerAddress;
    private Long slot;

    public BookingInformation() {
    }

    public BookingInformation(String customerName, String customerPhone, String time, String centerId, String centerName, String centerAddress, Long slot) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.centerId = centerId;
        this.centerName = centerName;
        this.centerAddress = centerAddress;
        this.slot = slot;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String setCustomerName(String customerName) {
        this.customerName = customerName;
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return customerPhone;
    }

    public String getTime() {
        return time;
    }

    public String setTime(String time) {
        this.time = time;
        return time;  //Remove
    }

    public String getCenterId() {
        return centerId;
    }

    public String setCenterId(String centerId) {
        this.centerId = centerId;
        return centerId;  //Remove
    }

    public String getCenterName() {
        return centerName;
    }

    public String setCenterName(String centerName) {
        this.centerName = centerName;
        return centerName;  //Remove
    }

    public String getCenterAddress() {
        return centerAddress;
    }

    public String setCenterAddress(String centerAddress) {
        this.centerAddress = centerAddress;
        return centerAddress;  //Remove
    }

    public Long getSlot() {
        return slot;
    }

    public String setSlot(Long slot) {
        this.slot = slot;
        return null;  //Remove
    }
}
