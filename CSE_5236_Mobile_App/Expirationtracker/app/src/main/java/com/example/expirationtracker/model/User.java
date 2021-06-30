package com.example.expirationtracker.model;

public class User {
    private String mUserName;
    private String mName;

    public User(String userName, String name) {
        mUserName = userName;
        mName = name;
    }
    public User(){
        mUserName = "";
        mName = "";
    }

    public String getUserName() {
        return mUserName;
    }
    public String getName() {
        return mName;
    }
    public void setName(String mName) {
        this.mName = mName;
    }
    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

}
