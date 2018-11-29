package com.example.balmaz.saildatamanagerclient.fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.activities.DataViewActivity;
import com.example.balmaz.saildatamanagerclient.model.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackingDetailDialogFragment extends DialogFragment implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnMarkerClickListener {
    private DataViewActivity mContext;
    private GoogleMap mMap;
    private ArrayList<LatLng> mPositions = new ArrayList<>();
    private ArrayList<UserData> mUserData = new ArrayList<>();
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(20);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    public TrackingDetailDialogFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(R.id.map_detail);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.map_detail));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tracking_detail, container, false);

        return rootView;
    }

    public boolean setPositions(LatLng position) {
        if(mPositions.size()==0){
            mPositions.add(position);
            return true;
        } else {
            if(position != mPositions.get(mPositions.size()-1)){
                mPositions.add(position);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if(mUserData.size()>0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mUserData.get(0).getLat(),mUserData.get(0).getLon())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            for (UserData u: mUserData) {
                LatLng actualLatLang = new LatLng(u.getLat(),u.getLon());
                if(setPositions(actualLatLang)){
                    placeLocationMarker(u);
                }
            }
            drawPolyLines(mPositions);
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnPolylineClickListener(this);
    }

    private void drawPolyLines(ArrayList<LatLng> points) {
        PolylineOptions options = new PolylineOptions()
                .clickable(true)
                .width(10)
                .color(Color.MAGENTA)
                .geodesic(true)
                .addAll(points);
        mMap.addPolyline(options);
        mMap.setOnPolylineClickListener(this);
    }

    public void setContext(DataViewActivity context) {
        mContext = context;
    }

    public void setUserData(ArrayList<UserData> userData){
        mUserData = userData;
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            polyline.setPattern(null);
        }
    }

    private void placeLocationMarker(UserData userData) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(userData.getLat(),userData.getLon()));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        markerOptions.snippet(userData.getTimestamp());
        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mUserData.size()>0){
            for (UserData u: mUserData) {
                if(marker.getSnippet().equals(u.getTimestamp())){
                    UserDataWindowAdapter windowAdapter = new UserDataWindowAdapter(u);
                    mMap.setInfoWindowAdapter(windowAdapter);
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(u.getLat(),u.getLon())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }
            }
        }
        return true;
    }

    private class UserDataWindowAdapter implements GoogleMap.InfoWindowAdapter{
        private UserData userData;

        public UserDataWindowAdapter(UserData userData) {
            this.userData = userData;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = mContext.getLayoutInflater().inflate(R.layout.tracking_data_info_window,null);

            TextView tvWs = view.findViewById(R.id.tv_WindSpeed);
            TextView tvWd = view.findViewById(R.id.tv_WindDirection);
            TextView tvSp = view.findViewById(R.id.tv_Speed);

            if(userData != null){
                tvWs.setText("AWS"+" "+Long.toString(Math.round(userData.getWindSpeed())) +" kts");
                tvWd.setText("AWD"+" "+Long.toString(Math.round(userData.getWindDirection())) +" kts");
                tvSp.setText("Speed"+" "+Long.toString(Math.round(userData.getSpeed()))+ " kts");

            }

            return view;
        }
    }
}