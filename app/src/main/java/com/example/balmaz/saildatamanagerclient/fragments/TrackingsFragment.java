package com.example.balmaz.saildatamanagerclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.activities.DataViewActivity;
import com.example.balmaz.saildatamanagerclient.adapter.TrackingDataAdapter;
import com.example.balmaz.saildatamanagerclient.adapter.TrackingTouchHelperCallback;
import com.example.balmaz.saildatamanagerclient.model.TrackingData;

import java.util.ArrayList;

public class TrackingsFragment extends Fragment {
    private TrackingDataAdapter mTrackingDataAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tracking_view_layout,container,false);

        RecyclerView recyclerView = rootView.findViewById(R.id.tracking_RV);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mTrackingDataAdapter = new TrackingDataAdapter((DataViewActivity) getActivity());
        recyclerView.setAdapter(mTrackingDataAdapter);
        TrackingTouchHelperCallback helperCallback = new TrackingTouchHelperCallback(mTrackingDataAdapter);
        new ItemTouchHelper(helperCallback).attachToRecyclerView(recyclerView);

        return rootView;
    }

    public void updateList(ArrayList<TrackingData> trackingData) {
        for (TrackingData t: trackingData) {
            mTrackingDataAdapter.addTrackingData(t);
        }
    }
}
