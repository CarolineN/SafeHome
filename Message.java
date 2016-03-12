package com.example.caroline.safehome;

/**
 * Created by Caroline on 3/5/2016.
 */

public class Message {

    private int id;
    private String myPhone;
    private String message;
    private String followerPhone;
    private String dateTime;


    public Message(String myPhone, String message, String followerPhone, String dateTime) {
        this.myPhone = myPhone;
        this.message = message;
        this.followerPhone = followerPhone;
        this.dateTime = dateTime;


    }
    public Message() {
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

    public String getFollowerPhone() {
        return followerPhone;
    }

    public void setFollowerPhone(String followerPhone) {
        this.followerPhone = followerPhone;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", myPhone='" + myPhone + '\'' +
                ", message='" + message + '\'' +
                ", followerPhone='" + followerPhone + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
