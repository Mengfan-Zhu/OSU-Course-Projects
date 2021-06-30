package com.example.expirationtracker.model;

public class Item {
    private String mName;
    private String mExpirationDate;
    private int mQuantity;
    private String mDescription;
    private long mEventId;

    public Item(){
        mName = "";
        mExpirationDate = "";
        mQuantity = 0;
        mDescription = "";
        mEventId = 0;
    }
    public Item(String name, String expirationDate, int quantity, String description, long eventId) {
        mName = name;
        mExpirationDate = expirationDate;
        mQuantity = quantity;
        mDescription = description;
        mEventId = eventId;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        mExpirationDate = expirationDate;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
    public long getEventId() {
        return mEventId;
    }

    public void setEventId(long eventId) {
        mEventId = eventId;
    }

}
