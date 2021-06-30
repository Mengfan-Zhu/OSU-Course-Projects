package com.example.expirationtracker.model;

public class Category {
    private String mName;
    private String mBegin;
    private String mFrequency;
    private String mTime;

    public Category() {
        mName = "";
        mBegin = "";
        mFrequency = "";
        mTime = "";
    }

    public Category(String name, String begin, String frequency, String time) {
        mName = name;
        mBegin = begin;
        mFrequency = frequency;
        mTime = time;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    public String getBegin() {
        return mBegin;
    }

    public void setBegin(String begin) {
        mBegin = begin;
    }
    public String getFrequency() {
        return mFrequency;
    }

    public void setFrequency(String frequency) {
        mFrequency = frequency;
    }
    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }


}
