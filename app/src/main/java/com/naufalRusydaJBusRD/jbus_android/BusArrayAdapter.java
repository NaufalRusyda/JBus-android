package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.naufalRusydaJBusRD.jbus_android.model.Bus;

import java.util.List;

public class BusArrayAdapter extends ArrayAdapter<Bus> {

    public BusArrayAdapter(Context context, List<Bus> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_view, parent, false);
        }

        TextView busNameTextView = convertView.findViewById(R.id.bus_name);
        Bus bus = getItem(position);
        busNameTextView.setText(bus.name);

        return convertView;
    }
}
