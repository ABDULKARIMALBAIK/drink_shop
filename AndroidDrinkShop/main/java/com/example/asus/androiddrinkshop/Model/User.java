package com.example.asus.androiddrinkshop.Model;

public class User {

    private String phone;
    private String address;
    private String name;
    private String birthdate;
    private String error_msg;  //It will empty if user return object
    private String avatarUrl;

    public User() {
    }

    public User(String phone, String address, String name, String birthdate, String error_msg, String avatarUrl) {
        this.phone = phone;
        this.address = address;
        this.name = name;
        this.birthdate = birthdate;
        this.error_msg = error_msg;
        this.avatarUrl = avatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
