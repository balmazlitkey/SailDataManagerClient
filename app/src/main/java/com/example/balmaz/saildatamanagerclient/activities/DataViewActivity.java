package com.example.balmaz.saildatamanagerclient.activities;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.adapter.LeDeviceListAdapter;
import com.example.balmaz.saildatamanagerclient.fragments.StatisticFragment;
import com.example.balmaz.saildatamanagerclient.fragments.TrackingsFragment;
import com.example.balmaz.saildatamanagerclient.model.StatisticData;
import com.example.balmaz.saildatamanagerclient.model.TrackingData;
import com.example.balmaz.saildatamanagerclient.service.SailDataInteractorService;

import java.util.ArrayList;

public class DataViewActivity extends AppCompatActivity {

    private final BroadcastReceiver mGetDataBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(SailDataInteractorService.ACTION_GET_STATISTICS_RESPONSE.equals(action)){
                if(mStatisticFragment != null && mStatisticFragment.isAdded()){
                    mStatisticFragment.updateView((StatisticData) intent.getParcelableExtra(SailDataInteractorService.EXTRA_STATISTICDATA));
                }
            }
            if(SailDataInteractorService.ACTION_GET_TRACKINGDATA_RESPONSE.equals(action)){
                if(mTrackingsFragment != null && mTrackingsFragment.isAdded()){
                    ArrayList<TrackingData> trackingDataArrayList = intent.getParcelableArrayListExtra(SailDataInteractorService.EXTRA_TRACKINGDATA_LIST);
                    mTrackingsFragment.updateList(trackingDataArrayList);
                }
            }
        }
    };

    private StatisticFragment mStatisticFragment;
    private TrackingsFragment mTrackingsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        String extra = getIntent().getStringExtra(LeDeviceListAdapter.EXTRA_INSTRUMENT_ID);
        String action = getIntent().getAction();
        analyzeBundle(extra, action);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SailDataInteractorService.ACTION_GET_STATISTICS_RESPONSE);
        filter.addAction(SailDataInteractorService.ACTION_GET_TRACKINGDATA_RESPONSE);
        filter.addAction(SailDataInteractorService.EXTRA_STATISTICDATA);
        filter.addAction(SailDataInteractorService.EXTRA_TRACKINGDATA_LIST);
        this.registerReceiver(mGetDataBroadcastReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mGetDataBroadcastReceiver);
    }

    private void analyzeBundle(String instrumentId, String action) {
        if (action == LeDeviceListAdapter.ACTION_VIEW_STATISTICS){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            mStatisticFragment = new StatisticFragment();
            ft.replace(R.id.ui_placeholder, mStatisticFragment);
            ft.commit();
            SailDataInteractorService.getUserDataStatistics(this,instrumentId);
        }
        else if(action == LeDeviceListAdapter.ACTION_VIEW_TRACKINGS){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            mTrackingsFragment = new TrackingsFragment();
            ft.replace(R.id.ui_placeholder, mTrackingsFragment);
            ft.commit();
            SailDataInteractorService.getTrackingData(this,instrumentId);
        }
    }
}
