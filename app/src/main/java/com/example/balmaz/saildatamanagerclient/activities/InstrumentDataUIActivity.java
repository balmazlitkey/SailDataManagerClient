package com.example.balmaz.saildatamanagerclient.activities;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.location.MapHelper;
import com.example.balmaz.saildatamanagerclient.model.TrackingData;
import com.example.balmaz.saildatamanagerclient.model.UserData;
import com.example.balmaz.saildatamanagerclient.service.SailDataInteractorService;
import com.example.balmaz.saildatamanagerclient.service.UserDataBluetoothService;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.balmaz.saildatamanagerclient.adapter.LeDeviceListAdapter.BLUETOOTH_DEVICE_ID;
import static com.example.balmaz.saildatamanagerclient.location.MapHelper.BR_NEW_LOCATION;
import static com.example.balmaz.saildatamanagerclient.location.MapHelper.KEY_LOCATION;

/**
 * Created by balmaz on 2018. 04. 16..
 */

public class InstrumentDataUIActivity extends AppCompatActivity {
    private Intent mServiceIntent;
    private BluetoothDevice mDevice;

    private TextView tvWS;
    private TextView tvWD;
    private TextView tvTemp;
    private TextView tvHeading;
    private TextView tvLat;
    private TextView tvLon;
    private TextView tvSpeed;
    private TextView tvBattery;

    private ArrayList<UserData> mUserDataList = new ArrayList<>();

    private TrackingData mTrackingData = new TrackingData();
    private boolean mIsTrackingEnabled;


    public boolean isTrackingEnabled() {
        return mIsTrackingEnabled;
    }

    public void enableTracking(boolean trackingEnabled) {
        mIsTrackingEnabled = trackingEnabled;
    }


    private Location lastLocation;
    private MapHelper mapHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument_data_ui);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.disconnect);
        setSupportActionBar(myToolbar);

        initField(R.id.fieldHeading, this.getString(R.string.heading));
        initField(R.id.fieldLat, this.getString(R.string.lattitude));
        initField(R.id.fieldLng, this.getString(R.string.longitude));
        initField(R.id.fieldTempreature, this.getString(R.string.tempreature));
        initField(R.id.fieldWindDirection, this.getString(R.string.winddirection));
        initField(R.id.fieldWindSpeed, this.getString(R.string.windspeed));
        initField(R.id.fieldBattery, this.getString(R.string.battery));
        initField(R.id.fieldSpeed, this.getString(R.string.speed));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //start location monitoring with google map
        mapHelper = new MapHelper(InstrumentDataUIActivity.this, mapFragment);


        startBluetoothService(getIntent());

    }

    private void initField(int fieldId, String headText) {
        View viewField = findViewById(fieldId);
        TextView tvHead = viewField.findViewById(R.id.tvHead);
        tvHead.setText(headText);
        tvHead.setTextColor(Color.parseColor("#E1B16A"));

        switch (fieldId) {
            case R.id.fieldHeading:
                tvHeading = viewField.findViewById(R.id.tvValue);
                tvHeading.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldLat:
                tvLat = viewField.findViewById(R.id.tvValue);
                tvLat.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldLng:
                tvLon = viewField.findViewById(R.id.tvValue);
                tvLon.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldTempreature:
                tvTemp = viewField.findViewById(R.id.tvValue);
                tvTemp.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldWindDirection:
                tvWD = viewField.findViewById(R.id.tvValue);
                tvWD.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldWindSpeed:
                tvWS = viewField.findViewById(R.id.tvValue);
                tvWS.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldBattery:
                tvBattery = viewField.findViewById(R.id.tvValue);
                tvBattery.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldSpeed:
                tvSpeed = viewField.findViewById(R.id.tvValue);
                tvSpeed.setTextColor(Color.parseColor("#E1B16A"));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UserDataBluetoothService.EXTRA_DATA);
        filter.addAction(UserDataBluetoothService.ACTION_DATA_AVAILABLE);
        filter.addAction(UserDataBluetoothService.ACTION_GATT_CONNECTED);
        filter.addAction(UserDataBluetoothService.ACTION_GATT_DISCONNECTED);
        filter.addAction(UserDataBluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BR_NEW_LOCATION);
        this.registerReceiver(mUserDataUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mapHelper != null){
            mapHelper.onPause();
        }
        this.unregisterReceiver(mUserDataUpdateReceiver);
        disconnect();
    }

    private void startBluetoothService(Intent intent) {
        if (intent != null) {
            mDevice = intent.getParcelableExtra(BLUETOOTH_DEVICE_ID);
            mServiceIntent = new Intent(this, UserDataBluetoothService.class);
            mServiceIntent.putExtra(BLUETOOTH_DEVICE_ID, mDevice);

            this.startService(mServiceIntent);
        }
    }

    private void displayData(String rawData) {
        UserData userData = new UserData();

        if (rawData != null && rawData.replaceAll(" ", "").length() == 20 && lastLocation !=null) {

            String s = rawData.replaceAll(" ", "");

            userData.setWindSpeed(((hex2decimal(s.substring(2, 4) + s.substring(0, 2)) / (double) 100) * 1.94));
            double windSpeed = userData.getWindSpeed();

            userData.setWindDirection(hex2decimal(s.substring(6, 8) + s.substring(4, 6)));
            int windDirection = userData.getWindDirection();

            userData.setBatteryLevel(hex2decimal(s.substring(8, 10)) * 10);
            int batteryLevel = userData.getBatteryLevel();

            userData.setTemp(hex2decimal(s.substring(10, 12)) - 100);
            int temperature = userData.getTemp();

            userData.setHeading(360 - hex2decimal(s.substring(18,20) + s.substring(16, 18)));
            int heading = userData.getHeading();
            userData.setLat(lastLocation.getLatitude());
            userData.setLon(lastLocation.getLongitude());
            userData.setSpeed(lastLocation.getSpeed() * 1.94);
            userData.setTimestamp(new Date(lastLocation.getTime()).toString());
            userData.setInstrumentID(mDevice.getAddress());

            userData.setTimestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));

            tvWS.setText(" " + String.format("%.2f",windSpeed) + " " + getString(R.string.kts_sign));
            tvWD.setText(" " + windDirection + " " + getString(R.string.deg_sign));
            tvTemp.setText(" " + temperature + " " + getString(R.string.celsius_sign));
            tvHeading.setText(" " + heading + " " + getString(R.string.deg_sign));
            tvBattery.setText(" " + batteryLevel + " " + getString(R.string.percent_sign));
            tvSpeed.setText(" " + String.format("%.2f",lastLocation.getSpeed() * 1.94) + " " + getString(R.string.kts_sign));
            tvLon.setText(String.format("%.2f ", lastLocation.getLatitude()));
            tvLat.setText(String.format("%.2f ", lastLocation.getLongitude()));

            mapHelper.setActualUserData(userData);
            mUserDataList.add(userData);
        } else if(rawData.length() < 20) {
            Toast.makeText(this, rawData, Toast.LENGTH_SHORT).show();
        }

        if (mUserDataList.size() == 10)
            aggregateUserDatas();
    }


    private void aggregateUserDatas() {
        double sumSpeed = 0;
        int sumTemp = 0;
        double sumLat = 0;
        double sumLon = 0;
        double sumWindSpeed = 0;
        int sumWindDirection = 0;
        int sumHeading = 0;

        for (int i = 0; i < mUserDataList.size(); i++) {
            sumSpeed += mUserDataList.get(i).getSpeed();
            sumTemp += mUserDataList.get(i).getTemp();
            sumLat += mUserDataList.get(i).getLat();
            sumLon += mUserDataList.get(i).getLon();
            sumWindSpeed += mUserDataList.get(i).getWindSpeed();
            sumWindDirection += mUserDataList.get(i).getWindDirection();
            sumHeading += mUserDataList.get(i).getHeading();
        }

        UserData averageUserData = new UserData();

        averageUserData.setInstrumentID(mUserDataList.get(9).getInstrumentID());
        averageUserData.setSpeed((sumSpeed / 10));
        averageUserData.setTimestamp(mUserDataList.get(9).getTimestamp());
        averageUserData.setBatteryLevel(mUserDataList.get(9).getBatteryLevel());
        averageUserData.setHeading((sumHeading / 10));
        averageUserData.setLat((sumLat / 10));
        averageUserData.setLon((sumLon / 10));
        averageUserData.setTemp((sumTemp / 10));
        averageUserData.setWindDirection((sumWindDirection / 10));
        averageUserData.setWindSpeed((sumWindSpeed / 10));

        SailDataInteractorService.startPostUserData(this,averageUserData);

        if(isTrackingEnabled()){
            mTrackingData.addUserData(averageUserData);
        }
        mUserDataList.clear();
    }


    public static int hex2decimal(String source) {
        String digits = "0123456789ABCDEF";
        source = source.toUpperCase();
        int retVal = 0;

        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            int d = digits.indexOf(c);
            retVal = 16 * retVal + d;
        }
        return retVal;
    }

    private void disconnect() {
        //ha leall a service akkor disconnectal a bluetoothGatt (az onDestryban)
        this.stopService(new Intent(InstrumentDataUIActivity.this, UserDataBluetoothService.class));
        this.stopService(new Intent(InstrumentDataUIActivity.this, SailDataInteractorService.class));
        //vissza terunka kezdokepernyore
        startActivity(new Intent(InstrumentDataUIActivity.this, MainActivity.class));
    }

    @Override
    protected void onStop() {
        disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.instrument_data_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.disconnect) {
            disconnect();
            return true;
        } else if(item.getItemId() == R.id.start_routing){
            if(!isTrackingEnabled()){
                mTrackingData = new TrackingData();
                mTrackingData.setStartTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                enableTracking(true);
            }
        } else if(item.getItemId() == R.id.stop_routing){
            if(isTrackingEnabled()){
                enableTracking(false);
                mTrackingData.setEndTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                mTrackingData.setInstrumentId(mTrackingData.getUserData().get(0).getInstrumentID());
                SailDataInteractorService.startPostTrackingData(this,mTrackingData);
            }
        }

        return false;
    }

    private final BroadcastReceiver mUserDataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (UserDataBluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(UserDataBluetoothService.EXTRA_DATA));
            }
            if (UserDataBluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                displayData(intent.getAction());
            }
            if (UserDataBluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                displayData(intent.getAction());
            }
            if (UserDataBluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayData(intent.getAction());
            }
            //if (CoAPPostService.ACTION_COAP_POST_RESPONSE.equals(action)) {
            //    displayData(intent.getStringExtra(CoAPPostService.EXTRA_COAP_POST_RESPONSE));
           // }
            if (BR_NEW_LOCATION.equals(action)) {

                lastLocation = (Location) intent.getParcelableExtra(KEY_LOCATION);
            }

        }
    };
}
