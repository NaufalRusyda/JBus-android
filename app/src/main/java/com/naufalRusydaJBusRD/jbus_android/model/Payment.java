package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class Payment extends Invoice {
    public int busId;
    public Timestamp departureDate;
    public List<String> busSeat;
    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
        return dateFormat.format(this.departureDate.getTime()) + "\t\t" +"Seat: "+busSeat+"Status: "+status;
    }

}
