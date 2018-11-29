package com.example.balmaz.saildatamanagerclient.adapter;

public interface TrackingTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemSwipe(TrackingDataAdapter.ViewHolder viewHolder,int position);

}
