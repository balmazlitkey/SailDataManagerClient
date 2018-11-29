package com.example.balmaz.saildatamanagerclient.adapter;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.activities.DataViewActivity;
import com.example.balmaz.saildatamanagerclient.fragments.TrackingDetailDialogFragment;
import com.example.balmaz.saildatamanagerclient.model.TrackingData;
import com.example.balmaz.saildatamanagerclient.model.UserData;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

public class TrackingDataAdapter extends RecyclerView.Adapter<TrackingDataAdapter.ViewHolder> implements TrackingTouchHelperAdapter {
    private ArrayList<TrackingData> mTrackings;
    private DataViewActivity mContext;

    public TrackingDataAdapter(DataViewActivity context){
        mContext = context;
        mTrackings = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TrackingData trackingData = mTrackings.get(position);
        if (trackingData != null){
            holder.tvTrackingId.setText(trackingData.getStartTime());
        }
    }

    @Override
    public int getItemCount() {
        return mTrackings.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition){
            for (int i=fromPosition; i < toPosition; ++i){
                Collections.swap(mTrackings,i,i+1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mTrackings, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemSwipe(ViewHolder viewHolder, int position) {
        TrackingDetailDialogFragment mapDialog = new TrackingDetailDialogFragment();
        mapDialog.setContext(mContext);
        mapDialog.setUserData(mTrackings.get(position).getUserData());
        mapDialog.show(mContext.getFragmentManager(),null);
    }

    public void addTrackingData(TrackingData trackingData){
        mTrackings.add(trackingData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View scanView;
        final TextView tvTrackingId;
        final RelativeLayout foreground;
        final RelativeLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            scanView = itemView;
            tvTrackingId = scanView.findViewById(R.id.tracking_id_tv);
            foreground = scanView.findViewById(R.id.view_foreground_tracking);
            background = scanView.findViewById(R.id.view_background_tracking);
        }
    }
}