package com.example.balmaz.saildatamanagerclient.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.balmaz.saildatamanagerclient.activities.DataViewActivity;
import com.example.balmaz.saildatamanagerclient.activities.InstrumentDataUIActivity;
import com.example.balmaz.saildatamanagerclient.activities.MainActivity;
import com.example.balmaz.saildatamanagerclient.R;

import java.util.ArrayList;
import java.util.Collections;

public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> implements LeDeviceTouchHelperAdapter {

    public static final String ACTION_VIEW_STATISTICS = "com.example.balmaz.saildatamanagerclient.Activites.action.ACTION_VIEW_STATISTICS";
    public static final String ACTION_VIEW_TRACKINGS = "com.example.balmaz.saildatamanagerclient.Activites.action.ACTION_VIEW_TRACKINGS";
    public static final String EXTRA_INSTRUMENT_ID = "com.example.balmaz.saildatamanagerclient.Activities.extra.EXTRA_INSTRUMENT_ID";
    public static final String BLUETOOTH_DEVICE_ID = "BLUETOOTH_DEVICE_ID";
    private ArrayList<BluetoothDevice> mDevices;
    private MainActivity mContext;

    public LeDeviceListAdapter(MainActivity context) {
        this.mDevices = new ArrayList<>();
        this.mContext =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BluetoothDevice mDevice = mDevices.get(position);

        if (mDevice != null) {
            holder.nameTV.setText(mDevice.getName());
            holder.macTV.setText(mDevice.getAddress());
            holder.connectBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext,InstrumentDataUIActivity.class);
                    intent.putExtra(BLUETOOTH_DEVICE_ID,mDevice);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition){
            for (int i=fromPosition; i < toPosition; ++i){
                Collections.swap(mDevices,i,i+1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDevices, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemRightMove(ViewHolder viewHolder, int position) {
        Intent intent= new Intent(mContext, DataViewActivity.class);
        intent.setAction(ACTION_VIEW_STATISTICS);
        intent.putExtra(EXTRA_INSTRUMENT_ID,mDevices.get(position).getAddress());
        mContext.startActivity(intent);
    }

    @Override
    public void onItemLeftMove(ViewHolder viewHolder, int position) {
        Intent intent= new Intent(mContext, DataViewActivity.class);
        intent.setAction(ACTION_VIEW_TRACKINGS);
        intent.putExtra(EXTRA_INSTRUMENT_ID,mDevices.get(position).getAddress());
        mContext.startActivity(intent);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View scanView;
        final TextView nameTV;
        final TextView macTV;
        final TextView statTV;
        final TextView trackTV;
        final Button connectBT;
        final RelativeLayout viewForeground;
        final RelativeLayout viewBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            scanView = itemView;
            nameTV = scanView.findViewById(R.id.name_tv);
            macTV = scanView.findViewById(R.id.mac_tv);
            connectBT = scanView.findViewById(R.id.button);
            statTV = scanView.findViewById(R.id.tv_view_stat);
            trackTV = scanView.findViewById(R.id.tv_view_track);
            viewForeground = scanView.findViewById(R.id.view_foreground);
            viewBackground = scanView.findViewById(R.id.view_background);
        }
    }

    public void addDevice(BluetoothDevice device) {
        boolean unique=true;

        for (int i=0; i< mDevices.size();i++){
            if (device.equals(mDevices.get(i))){
                unique=false;
            }
        }

        if (unique) {
            mDevices.add(device);
            notifyDataSetChanged();
        }
    }
}