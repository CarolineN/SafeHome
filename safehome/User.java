package com.example.caroline.safehome;

import java.util.List;

public class User {

    private int id;

    private String name;
    private String password;
    private String email;
    private String address;
    private String phone;
    private List<Follower> followers;





    public User(int id,String name, String password, String email, String address, String phone) {
        this.id=id;
        this.address = address;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;

    }
    public User() {

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }
    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone+ '\'' +
                ",followers" + followers +
                '}';
    }
}


