package com.example.balmaz.saildatamanagerclient.adapter;

public interface LeDeviceTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemRightMove(LeDeviceListAdapter.ViewHolder viewHolder,int position);

    void onItemLeftMove(LeDeviceListAdapter.ViewHolder viewHolder, int position);
}
