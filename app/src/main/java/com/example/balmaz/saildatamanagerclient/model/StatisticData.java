package com.example.balmaz.saildatamanagerclient.model;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class StatisticData implements Parcelable {
    private double speedStatistic;
    private double tempStatistic;
    private double windSpeedStatistic;
    private double windDirectionStatistic;
    private double maximumWindSpeed;
    private double maximumSpeed;
    private double maximumTemp;

    protected StatisticData(Parcel in) {
        speedStatistic = in.readDouble();
        tempStatistic = in.readDouble();
        windSpeedStatistic = in.readDouble();
        windDirectionStatistic = in.readDouble();
        maximumWindSpeed = in.readDouble();
        maximumSpeed = in.readDouble();
        maximumTemp = in.readDouble();
    }

    public static final Creator<StatisticData> CREATOR = new Creator<StatisticData>() {
        @Override
        public StatisticData createFromParcel(Parcel in) {
            return new StatisticData(in);
        }

        @Override
        public StatisticData[] newArray(int size) {
            return new StatisticData[size];
        }
    };

    @Override
    public String toString() {
        return "StatisticData{" +
                "speedStatistic=" + speedStatistic +
                ", tempStatistic=" + tempStatistic +
                ", windSpeedStatistic=" + windSpeedStatistic +
                ", windDirectionStatistic=" + windDirectionStatistic +
                ", maximumWindSpeed=" + maximumWindSpeed +
                ", maximumSpeed=" + maximumSpeed +
                ", maximumTemp=" + maximumTemp +
                '}';
    }

    public StatisticData(double speedStatistic, double tempStatistic, double windSpeedStatistic, double windDirectionStatistic, double maximumWindSpeed, double maximumSpeed, double maximumTemp) {
        this.speedStatistic = speedStatistic;
        this.tempStatistic = tempStatistic;
        this.windSpeedStatistic = windSpeedStatistic;
        this.windDirectionStatistic = windDirectionStatistic;
        this.maximumWindSpeed = maximumWindSpeed;
        this.maximumSpeed = maximumSpeed;
        this.maximumTemp = maximumTemp;
    }

    public StatisticData(List<UserData> userDataList){
        this.speedStatistic = 0.0;
        this.tempStatistic = 0.0;
        this.windSpeedStatistic = 0.0;
        this.windDirectionStatistic = 0.0;
        this.maximumWindSpeed = 0.0;
        this.maximumSpeed = 0.0;
        this.maximumTemp = 0.0;


        for(int i = 0; i<userDataList.size();++i){
            speedStatistic += userDataList.get(i).getSpeed();
            tempStatistic += userDataList.get(i).getTemp();
            windSpeedStatistic += userDataList.get(i).getWindSpeed();
            windDirectionStatistic += userDataList.get(i).getWindDirection();
        }

        for(int j=0;j<userDataList.size();++j){
            if(userDataList.get(j).getWindSpeed()>maximumSpeed){
                maximumSpeed = userDataList.get(j).getWindSpeed();
            }

            if(userDataList.get(j).getSpeed()>maximumSpeed){
                maximumSpeed = userDataList.get(j).getSpeed();
            }

            if(userDataList.get(j).getTemp()>maximumTemp){
                maximumTemp = userDataList.get(j).getTemp();
            }
        }

        int size = userDataList.size();

        speedStatistic /= size;
        tempStatistic /=size;
        windSpeedStatistic /= size;
        windDirectionStatistic /= size;
    }

    public double getMaximumWindSpeed() {
        return maximumWindSpeed;
    }

    public void setMaximumWindSpeed(double maximumWindSpeed) {
        this.maximumWindSpeed = maximumWindSpeed;
    }

    public double getMaximumSpeed() {
        return maximumSpeed;
    }

    public void setMaximumSpeed(double maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public double getMaximumTemp() {
        return maximumTemp;
    }

    public void setMaximumTemp(double maximumTemp) {
        this.maximumTemp = maximumTemp;
    }

    public double getSpeedStatistic() {
        return speedStatistic;
    }

    public void setSpeedStatistic(double speedStatistic) {
        this.speedStatistic = speedStatistic;
    }

    public double getTempStatistic() {
        return tempStatistic;
    }

    public void setTempStatistic(double tempStatistic) {
        this.tempStatistic = tempStatistic;
    }

    public double getWindSpeedStatistic() {
        return windSpeedStatistic;
    }

    public void setWindSpeedStatistic(double windSpeedStatistic) {
        this.windSpeedStatistic = windSpeedStatistic;
    }

    public double getWindDirectionStatistic() {
        return windDirectionStatistic;
    }

    public void setWindDirectionStatistic(double windDirectionStatistic) {
        this.windDirectionStatistic = windDirectionStatistic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(speedStatistic);
        dest.writeDouble(tempStatistic);
        dest.writeDouble(windSpeedStatistic);
        dest.writeDouble(windDirectionStatistic);
        dest.writeDouble(maximumWindSpeed);
        dest.writeDouble(maximumSpeed);
        dest.writeDouble(maximumTemp);
    }
}
