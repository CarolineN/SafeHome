package com.example.caroline.safehome;

public class Notification {

    private int id;
    private String followerNum;
    private String usersName;
    private String message;
    private String time;

    public Notification(String followerNum, String usersName, String message, String time) {

        this.followerNum = followerNum;
        this.usersName = usersName;
        this.message = message;
        this.time = time;
    }

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(String followerNum) {
        this.followerNum = followerNum;
    }

    public String getUsersName() {
        return usersName;
    }

    public void setUsersName(String usersName) {
        this.usersName = usersName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", followerNum='" + followerNum + '\'' +
                ", usersName='" + usersName + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }


}