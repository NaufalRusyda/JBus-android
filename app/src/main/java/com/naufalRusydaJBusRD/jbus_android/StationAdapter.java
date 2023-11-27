package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naufalRusydaJBusRD.jbus_android.model.Station;

import java.util.List;

public class StationAdapter extends ArrayAdapter<Station> {

    public StationAdapter(Context context, List<Station> stations) {
        super(context, android.R.layout.simple_list_item_1, stations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(getItem(position).stationName);
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(getItem(position).stationName);
        return textView;
    }
}
