package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * The Payment class represents a payment associated with an invoice and additional payment details
 * in the JBus Android application. It extends the Invoice class.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class Payment extends Invoice {

    /**
     * The ID of the bus associated with the payment.
     */
    public int busId;

    /**
     * The departure date timestamp of the bus associated with the payment.
     */
    public Timestamp departureDate;

    /**
     * The list of bus seats associated with the payment.
     */
    public List<String> busSeat;

    /**
     * Returns a string representation of the Payment object.
     * The string includes information about the departure date, bus seats, and payment status.
     *
     * @return A formatted string containing payment details.
     */
    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
        return dateFormat.format(this.departureDate.getTime()) + "\t\t" + "Seat: " + busSeat + "Status: " + status;
    }
}
