package com.example.balmaz.saildatamanagerclient.service;

import com.example.balmaz.saildatamanagerclient.model.StatisticData;
import com.example.balmaz.saildatamanagerclient.model.TrackingData;
import com.example.balmaz.saildatamanagerclient.model.UserData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SailDataApi {
    String API_SERVER_URL = "http://192.168.1.2:8080";

    @POST("/userdata")
    Call<ResponseBody> postUserData(@Body UserData userData);

    @POST("/trackingdata")
    Call<ResponseBody> postTrackingData(@Body TrackingData trackingData);

    @GET("/userdata/{id}")
    Call<StatisticData> getUserDataStatistics(@Path("id") String instrumentId);

    @GET("/trackingdata/{id}")
    Call<ArrayList<TrackingData>> getPastTrackingInfo(@Path("id") String instrumentId);
}
