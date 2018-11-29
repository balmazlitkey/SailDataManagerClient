package com.example.balmaz.saildatamanagerclient.location;

import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.activities.InstrumentDataUIActivity;
import com.example.balmaz.saildatamanagerclient.model.UserData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class WindInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private InstrumentDataUIActivity context;
    private UserData mUserData;
    private Location mLocation;

    public WindInfoWindowAdapter(InstrumentDataUIActivity context, UserData userData, Location location){
        this.context = context;
        this.mLocation = location;
        this.mUserData = userData;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.wind_info_window, null);

        TextView tvTWS = view.findViewById(R.id.tv_TWS);
        TextView tvTWD = view.findViewById(R.id.tv_TWA);
        TextView tvAWA = view.findViewById(R.id.tv_AWA);

        if (mUserData != null) {
            tvTWS.setText(context.getString(R.string.windspeed_true)+" "+Long.toString(calculateTrueWindSpeed(mUserData,mLocation))+" " +context.getString(R.string.kts_sign));
            tvTWD.setText(context.getString(R.string.winddirection_true)+" "+Long.toString(calculateTrueWindDirection(mUserData,mLocation))+ " " + context.getString(R.string.deg_sign));
            tvAWA.setText(context.getString(R.string.windangle_apparent)+" "+Integer.toString(calculateApparentWindAngle(mUserData)));
        }

        return view;
    }

    public Long calculateTrueWindSpeed(UserData userData, Location location){
        Double apparentWindVelocity = userData.getWindSpeed();
        Float boatSpeed = location.getSpeed();
        Double trueWindSpeed = Math.sqrt(Math.pow(apparentWindVelocity,2.0)+Math.pow(boatSpeed,2.0)-2*apparentWindVelocity*boatSpeed*Math.cos(Math.abs((userData.getHeading()-userData.getWindDirection()))));
        return Math.round(trueWindSpeed);
    }

    public Long calculateTrueWindDirection(UserData userData, Location location){
        double result;
        if(location.getSpeed()>0){
            double u = location.getSpeed()*Math.sin(userData.getHeading())-userData.getWindSpeed()*Math.sin(userData.getWindDirection());
            double v = location.getSpeed()*Math.cos(userData.getHeading())-userData.getWindSpeed()*Math.cos(userData.getWindDirection());
            result = Math.atan(u/v);

        } else{
            result = (double) userData.getWindDirection();
        }
        return Math.round(result);
    }

    public int calculateApparentWindAngle(UserData userData){
        return Math.abs(userData.getWindDirection()-userData.getHeading());
    }
}