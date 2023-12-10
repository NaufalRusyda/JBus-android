package com.naufalRusydaJBusRD.jbus_android.model;

/**
 * The Renter class represents information about the renting company associated with a bus
 * in the JBus Android application. It extends the Serializable class for serialization.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class Renter extends Serializable {

    /**
     * The phone number of the renting company.
     */
    public String phoneNumber;

    /**
     * The address of the renting company.
     */
    public String address;

    /**
     * The name of the renting company.
     */
    public String companyName;
}
