package com.example.jam.myapplication.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String emailNum;
    private Integer userType;

    public LoggedInUser(String userId, String displayName, String emailNum, Integer userType) {
        this.userId = userId;
        this.displayName = displayName;
        this.emailNum = emailNum;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmailNum(){return emailNum;}

    public Integer getUserType(){return userType;}
}
