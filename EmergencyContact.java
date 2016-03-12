package com.example.caroline.safehome;

public class EmergencyContact {
    private int id;
    private String username;
    private String email;
    private String phoneNumber;
    private String user_Id;

    public EmergencyContact(){

    }
    public EmergencyContact(String username, String email, String phoneNumber, String user_Id) {
        super();
        this.username = username;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.user_Id=user_Id;



    }
    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
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
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String toString() {
        return "Emergency Contact{" +
                "id=" + id +
                ", name='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber+ '\'' +
                ",user_Id='" + user_Id+'\''+
                '}';
    }

}
