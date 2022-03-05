package com.example.damusasa.Model;

public class Branch {
    private String name, address;

    public Branch() { //His Salon is my branch
    }

    public Branch(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
