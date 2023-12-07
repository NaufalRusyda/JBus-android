package com.naufalRusydaJBusRD.jbus_android.model;

public class Invoice extends Serializable {

    public PaymentStatus status;

    public enum PaymentStatus {
        /**
         * Payment failed
         */
        FAILED,

        /**
         * Payment is pending
         */
        WAITING,

        /**
         * Payment successful
         */
        SUCCESS
    }
}
