package com.naufalRusydaJBusRD.jbus_android.request;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The RetrofitClient class provides a singleton instance of Retrofit for making API requests.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class RetrofitClient {

    /**
     * The Retrofit instance for making API requests.
     */
    private static Retrofit retrofit = null;

    /**
     * Gets the Retrofit client instance with the specified base URL.
     *
     * @param baseUrl The base URL of the API.
     * @return The Retrofit client instance.
     */
    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Creates and configures an OkHttpClient instance with a network interceptor to add custom headers.
     *
     * @return The OkHttpClient instance.
     */
    private static OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().addNetworkInterceptor(chain -> {
            Request originalRequest = chain.request();
            Request newRequest = originalRequest.newBuilder().addHeader("Naufal-Rusyda", "changemepls").build();
            return chain.proceed(newRequest);
        }).build();
    }
}
