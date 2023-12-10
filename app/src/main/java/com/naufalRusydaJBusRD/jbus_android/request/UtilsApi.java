package com.naufalRusydaJBusRD.jbus_android.request;

/**
 * The UtilsApi class provides utility methods for obtaining the API service instance.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class UtilsApi {

    /**
     * The base URL for the API.
     */
    public static final String BASE_URL_API = "http://10.0.2.2:5000/";

    /**
     * Gets the API service instance using the RetrofitClient with the base URL.
     *
     * @return The API service instance.
     */
    public static BaseApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }
}
