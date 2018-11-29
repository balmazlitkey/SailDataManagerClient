package com.example.balmaz.saildatamanagerclient.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class TrackingData implements Parcelable{
    private String instrumentId;

    private String startTime;

    private String endTime;

    private ArrayList<UserData> trackedUserData = new ArrayList<>();

    @Override
    public String toString() {
        return "TrackingData{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", instrumentId='" + instrumentId + '\'' +
                ", trackedUserData=" + trackedUserData +
                '}';
    }

    public TrackingData(){

    }

    protected TrackingData(Parcel in) {
        instrumentId = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        trackedUserData = in.createTypedArrayList(UserData.CREATOR);
    }

    public static final Creator<TrackingData> CREATOR = new Creator<TrackingData>() {
        @Override
        public TrackingData createFromParcel(Parcel in) {
            return new TrackingData(in);
        }

        @Override
        public TrackingData[] newArray(int size) {
            return new TrackingData[size];
        }
    };

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public void setTrackedUserData(ArrayList<UserData> trackedUserData){
        this.trackedUserData = trackedUserData;
    }

    public ArrayList<UserData> getUserData() {
        return trackedUserData;
    }

    public void addUserData(UserData data) {
        this.trackedUserData.add(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(instrumentId);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeTypedList(trackedUserData);
    }
}

