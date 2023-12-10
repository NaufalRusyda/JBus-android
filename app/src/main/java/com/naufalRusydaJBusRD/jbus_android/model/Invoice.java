package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

/**
 * The Invoice class represents an invoice with payment status in the JBus Android application.
 * It extends the Serializable class for serialization.
 *
 *  @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class Invoice extends Serializable {

    /**
     * Enumeration representing the payment status of the invoice.
     */
    public PaymentStatus status;

    /**
     * Enumeration for payment status in an Invoice.
     */
    public enum PaymentStatus {
        /**
         * Payment failed.
         */
        FAILED,

        /**
         * Payment is pending.
         */
        WAITING,

        /**
         * Payment successful.
         */
        SUCCESS
    }
    @NonNull
    @Override
    public String toString() {
        return "Invoice{" +
                "status=" + status +
                '}';
    }
}
