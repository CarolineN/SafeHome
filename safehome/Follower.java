package com.example.caroline.safehome;

/**
 * Created by Caroline on 2/1/2016.
 */

public class Follower {

    private int id;
    private String username;
    private String email;
    private String homeLocation;
    private String phoneNumber;
    private String userPhone;


    public Follower() {

    }

    public Follower(String username, String email, String homeLocation, String phoneNumber, String userPhone) {
        this.username = username;
        this.email = email;
        this.homeLocation = homeLocation;
        this.phoneNumber = phoneNumber;
        this.userPhone= userPhone;

    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String toString() {
        return "Follower{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + username + '\'' +
                ", address='" + homeLocation + '\'' +
                ", phone='" + phoneNumber + '\'' +
                ",userPhone=" + userPhone + '\'' +
                '}';
    }
}

