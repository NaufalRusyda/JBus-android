package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

/**
 * The Station class represents information about a bus station in the JBus Android application.
 * It extends the Serializable class for serialization.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class Station extends Serializable {

    /**
     * The name of the bus station.
     */
    public String stationName;

    /**
     * The city where the bus station is located.
     */
    public City city;

    /**
     * The address of the bus station.
     */
    public String address;

    /**
     * Returns a string representation of the Station object.
     *
     * @return A formatted string containing information about the station name and city.
     */
    @NonNull
    @Override
    public String toString() {
        return stationName + " - " + city;
    }
}
