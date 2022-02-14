package com.example.damusasa.Model;

public class User {
    String name, email, bloodGroup, idNumber, phoneNumber, search, type, profilepictureurl;

    public User() {
    }

    public User(String name, String email, String bloodGroup, String idNumber, String phoneNumber, String search, String type, String profilepictureurl) {
        this.name = name;
        this.email = email;
        this.bloodGroup = bloodGroup;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.search = search;
        this.type = type;
        this.profilepictureurl = profilepictureurl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfilepictureurl() {
        return profilepictureurl;
    }

    public void setProfilepictureurl(String profilepictureurl) {
        this.profilepictureurl = profilepictureurl;
    }
}
