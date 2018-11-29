package com.example.balmaz.saildatamanagerclient.service;

import com.example.balmaz.saildatamanagerclient.model.TrackingData;
import com.example.balmaz.saildatamanagerclient.model.UserData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

class TrackingDataDeserializer implements JsonDeserializer<TrackingData> {

    @Override
    public TrackingData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();

        final TrackingData trackingDataResponse = gson.fromJson(json,TrackingData.class);

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement jsonElement = jsonObject.getAsJsonArray("userData");

        ArrayList<UserData> userData = new ArrayList<>();
        for (int i=0;i<((JsonArray) jsonElement).size();++i){
            userData.add(gson.fromJson(((JsonArray) jsonElement).get(i),UserData.class));
        }

        trackingDataResponse.setTrackedUserData(userData);

        return trackingDataResponse;
    }
}
