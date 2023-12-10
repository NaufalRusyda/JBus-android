package com.naufalRusydaJBusRD.jbus_android.model;

/**
 * The BaseResponse class represents a generic response structure in the JBus Android application.
 * It contains information about the success of the operation, a message, and a payload of type T.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 *
 * @param <T> The type of payload contained in the response.
 */
public class BaseResponse<T> {

    /**
     * Indicates whether the operation was successful or not.
     */
    public boolean success;

    /**
     * A message providing additional information about the response.
     */
    public String message;

    /**
     * The payload containing the response data of type T.
     */
    public T payload;
}
