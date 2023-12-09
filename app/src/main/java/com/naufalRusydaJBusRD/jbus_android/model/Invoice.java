package com.naufalRusydaJBusRD.jbus_android.model;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
