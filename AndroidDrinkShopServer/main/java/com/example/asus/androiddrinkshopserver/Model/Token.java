package com.example.asus.androiddrinkshopserver.Model;

public class Token {

    public String phone , token , isServerToken;

    public Token() {
    }

    public Token(String phone, String token, String isServerToken) {
        this.phone = phone;
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsServerToken() {
        return isServerToken;
    }

    public void setIsServerToken(String isServerToken) {
        this.isServerToken = isServerToken;
    }
}
