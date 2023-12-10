package com.naufalRusydaJBusRD.jbus_android.model;

/**
 * The Account class represents user account information in the JBus Android application.
 * It implements the Serializable interface to support serialization.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */

public class Account extends Serializable {
    /**
     * The name associated with the user account.
     */
    public String name;

    /**
     * The email associated with the user account.
     */
    public String email;

    /**
     * The password associated with the user account.
     */
    public String password;

    /**
     * The balance associated with the user account.
     */
    public double balance;

    /**
     * The Renter object representing the company associated with the user account.
     */
    public Renter company;
}
