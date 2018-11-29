package com.example.balmaz.saildatamanagerclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.model.StatisticData;

public class StatisticFragment extends Fragment {
    private TextView tvSpeedStatistics;
    private TextView tvWindSpeedStatistics;
    private TextView tvTempStatistics;
    private TextView tvWindDirStatistics;
    private TextView tvMaxSpeedStatistics;
    private TextView tvMaxWindSpeedStatistics;
    private TextView tvMaxTempStatistics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_view_layout,container,false);

        tvSpeedStatistics = view.findViewById(R.id.tvAvgSpVal);
        tvWindSpeedStatistics = view.findViewById(R.id.tvAvgWspVal);
        tvTempStatistics = view.findViewById(R.id.tvAvgTmVal);
        tvWindDirStatistics = view.findViewById(R.id.tvAvgWDirVal);
        tvMaxSpeedStatistics = view.findViewById(R.id.tvMaxSpVal);
        tvMaxWindSpeedStatistics = view.findViewById(R.id.tvMaxWSVal);
        tvMaxTempStatistics = view.findViewById(R.id.tvMaxTmVal);

        return view;
    }

    public void updateView(StatisticData statisticData){
        tvWindSpeedStatistics.setText(Long.toString(Math.round(statisticData.getWindSpeedStatistic())));
        tvWindDirStatistics.setText(Long.toString(Math.round(statisticData.getWindDirectionStatistic())));
        tvTempStatistics.setText(Long.toString(Math.round(statisticData.getTempStatistic())));
        tvSpeedStatistics.setText(Long.toString(Math.round(statisticData.getSpeedStatistic())));
        tvMaxSpeedStatistics.setText(Long.toString(Math.round(statisticData.getMaximumSpeed())));
        tvMaxTempStatistics.setText(Long.toString(Math.round(statisticData.getMaximumTemp())));
        tvMaxWindSpeedStatistics.setText(Long.toString(Math.round(statisticData.getMaximumWindSpeed())));
    }

}
