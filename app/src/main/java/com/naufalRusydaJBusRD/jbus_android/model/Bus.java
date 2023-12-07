package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Bus extends Serializable {
    public int accountId;
    public String name;
    public List<Facility> facilities;
    public Price price;
    public int capacity;
    public BusType busType;
    public Station departure;
    public Station arrival;
    public List<Schedule> schedules;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
