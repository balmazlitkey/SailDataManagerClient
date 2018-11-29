package com.example.balmaz.saildatamanagerclient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.balmaz.saildatamanagerclient.model.StatisticData;
import com.example.balmaz.saildatamanagerclient.model.TrackingData;
import com.example.balmaz.saildatamanagerclient.model.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class SailDataInteractorService extends IntentService {
    public static final String ACTION_POST_USERDATA = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_POST_USERDATA";
    public static final String ACTION_POST_TRACKINGDATA = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_POST_TRACKINGDATA";
    public static final String ACTION_GET_STATISTICS = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_GET_STATISTICS";
    public static final String ACTION_GET_TRACKINGDATA = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_GET_TRACKINGDATA";
    public static final String ACTION_POST_RESPONSE = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_REST_POST_RESPONSE";
    public static final String EXTRA_USERDATA = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_USERDATA";
    public static final String EXTRA_TRACKINGDATA = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_TRACKINGDATA";
    public static final String EXTRA_INSTRUMENT_ID = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_INSTRUMENT_ID";
    public static final String EXTRA_ACTION_POST_RESPONSE = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_ACTION_POST_RESPONSE";
    public static final String ACTION_GET_STATISTICS_RESPONSE = "com.example.balmaz.saildatamanagerclient.Service.extra.ACTION_GET_STATISTIC_RESPONSE";;
    public static final String ACTION_GET_TRACKINGDATA_RESPONSE = "com.example.balmaz.saildatamanagerclient.Service.extra.ACTION_GET_TRACKINGDATA_RESPONSE";;
    public static final String EXTRA_STATISTICDATA = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_STATISTICDATA";;
    public static final String EXTRA_TRACKINGDATA_LIST = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_TRACKINGDATA_LIST";;

    private Gson mGson;
    private SailDataApi mSailDataApi;

    public SailDataInteractorService() {
        super("SailDataInteractorService");
    }

    public static void startPostTrackingData(Context context,TrackingData trackingData) {
        Intent intent = new Intent(context,SailDataInteractorService.class);
        intent.setAction(ACTION_POST_TRACKINGDATA);
        intent.putExtra(EXTRA_TRACKINGDATA, trackingData);
        context.startService(intent);
    }

    public static void startPostUserData(Context context, UserData  userData){
        Intent intent = new Intent(context,SailDataInteractorService.class);
        intent.setAction(ACTION_POST_USERDATA);
        intent.putExtra(EXTRA_USERDATA, userData);
        context.startService(intent);
    }

    public static void getUserDataStatistics(Context context, String instrumentId){
        Intent intent = new Intent(context,SailDataInteractorService.class);
        intent.setAction(ACTION_GET_STATISTICS);
        intent.putExtra(EXTRA_INSTRUMENT_ID, instrumentId);
        context.startService(intent);
    }

    public static void getTrackingData(Context context, String instrumentId) {
        Intent intent = new Intent(context,SailDataInteractorService.class);
        intent.setAction(ACTION_GET_TRACKINGDATA);
        intent.putExtra(EXTRA_INSTRUMENT_ID, instrumentId);
        context.startService(intent);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mGson = new Gson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SailDataApi.API_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
        mSailDataApi = retrofit.create(SailDataApi.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            final String action = intent.getAction();

            if(ACTION_POST_USERDATA.equals(action)){
                final UserData userData = intent.getParcelableExtra(EXTRA_USERDATA);
                if (userData != null){
                    handleActionPostUserData(userData);
                }
            }
            else if (ACTION_POST_TRACKINGDATA.equals(action)){
                final TrackingData trackingData = intent.getParcelableExtra(EXTRA_TRACKINGDATA);
                if (trackingData != null){
                    handleActionPostTrackingData(trackingData);
                }
            }
            else if(ACTION_GET_STATISTICS.equals(action)){
                final String instrumentId = intent.getStringExtra(EXTRA_INSTRUMENT_ID);
                if(instrumentId != null){
                    handleActionGetStatistics(instrumentId);
                }
            }
            else if(ACTION_GET_TRACKINGDATA.equals(action)){
                final String instrumentId = intent.getStringExtra(EXTRA_INSTRUMENT_ID);
                if(instrumentId != null){
                    handleActionGetTrackingData(instrumentId);
                }
            }
        }
    }

    private void handleActionGetTrackingData(String instrumentId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SailDataApi.API_SERVER_URL)
                .addConverterFactory(buildGsonConverter())
                .build();
        SailDataApi sailDataApi = retrofit.create(SailDataApi.class);
        sailDataApi.getPastTrackingInfo(instrumentId).enqueue(new Callback<ArrayList<TrackingData>>() {
            @Override
            public void onResponse(Call<ArrayList<TrackingData>> call, Response<ArrayList<TrackingData>> response) {
                Log.d(TAG, "REST response code: " + response.code());
                sendGetTrackingDataResponse(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<TrackingData>> call, Throwable t) {
                Log.d(TAG, "REST call failed: " + Log.getStackTraceString(t));
            }
        });
    }

    private GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TrackingData.class,new TrackingDataDeserializer());
        Gson gson = gsonBuilder.create();

        return GsonConverterFactory.create(gson);
    }

    private void handleActionGetStatistics(String instrumentId) {
        mSailDataApi.getUserDataStatistics(instrumentId).enqueue(new Callback<StatisticData>() {
            @Override
            public void onResponse(Call<StatisticData> call, Response<StatisticData> response) {
                Log.d(TAG, "REST response code: " + response.code());
                sendGetStatisticsResponse(response.body());
            }

            @Override
            public void onFailure(Call<StatisticData> call, Throwable t) {
                Log.d(TAG, "REST call failed: " + Log.getStackTraceString(t));
            }
        });
    }

    private void handleActionPostTrackingData(TrackingData trackingData) {
        mSailDataApi.postTrackingData(trackingData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "REST response code: " + response.code());
                sendPostResponse(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "REST call failed: " + Log.getStackTraceString(t));
            }
        });
    }

    private void handleActionPostUserData(UserData userData) {
        mSailDataApi.postUserData(userData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "REST response code: " + response.code());
                sendPostResponse(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "REST call failed: " + Log.getStackTraceString(t));
            }
        });
    }

    private void sendPostResponse(ResponseBody response) {
        if(response != null){
            Intent responseIntent = new Intent(ACTION_POST_RESPONSE);
            responseIntent.putExtra(EXTRA_ACTION_POST_RESPONSE,response.toString());
            sendBroadcast(responseIntent);
        }
    }

    private void sendGetStatisticsResponse(StatisticData statistics) {
        if(statistics != null){
            Intent responseIntent = new Intent(ACTION_GET_STATISTICS_RESPONSE);
            responseIntent.putExtra(EXTRA_STATISTICDATA,statistics);
            sendBroadcast(responseIntent);
        }
    }

    private void sendGetTrackingDataResponse(List<TrackingData> trackingData){
        if (trackingData.size()>0){
            Intent responseIntent = new Intent(ACTION_GET_TRACKINGDATA_RESPONSE);
            responseIntent.putParcelableArrayListExtra(EXTRA_TRACKINGDATA_LIST, new ArrayList<>(trackingData));
            sendBroadcast(responseIntent);
        }
    }
}
