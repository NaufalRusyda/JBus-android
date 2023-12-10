package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * The Schedule class represents the schedule information for a bus departure
 * in the JBus Android application.
 */
public class Schedule {

    /**
     * The timestamp indicating the departure schedule for the bus.
     */
    public Timestamp departureSchedule;

    /**
     * A map containing seat availability information, where the key is the seat identifier
     * and the value is a boolean indicating whether the seat is available (true) or occupied (false).
     */
    public Map<String, Boolean> seatAvailability;

    /**
     * Returns a string representation of the Schedule object.
     * The string includes information about the departure schedule, the count of occupied seats,
     * and the total number of seats.
     *
     * @author Naufal Rusyda Santosa
     * @version 1.0
     *
     * @return A formatted string containing schedule details.
     */
    @NonNull
    @Override
    public String toString() {
        int countOccupied = 0;
        for (boolean val : seatAvailability.values()) {
            if (!val) countOccupied++;
        }
        int totalSeat = seatAvailability.size();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
        return dateFormat.format(this.departureSchedule.getTime()) + "\t\t" + "[ " + countOccupied + "/" + totalSeat + " ] ";
    }
}
