package com.example.caroline.safehome;

/**
 * Created by Caroline on 3/5/2016.
 */

public class Message {

    private int id;
    private String myPhone;
    private String message;
    private String followerNum;
    private String date;
    private String time;


    public Message(String myPhone, String message, String followerNum, String date, String time) {
        super();
        this.myPhone = myPhone;
        this.message = message;
        this.followerNum = followerNum;
        this.date = date;
        this.time = time;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMyPhone() {
        return myPhone;
    }

    public void setMyPhone(String myPhone) {
        this.myPhone = myPhone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(String followerNum) {
        this.followerNum = followerNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "EmergencyContact{" +
                "id=" + id +
                ", myPhone='" + myPhone + '\'' +
                ", message='" + message + '\'' +
                ", followerNum='" + followerNum + '\'' +
                ", date='" + date + '\'' +
                ",time_'" + time + '\'' +
                '}';
    }
}