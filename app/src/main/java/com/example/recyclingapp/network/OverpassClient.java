package com.example.recyclingapp.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OverpassClient {
    private static final String[] BASE_URLS = {
        "https://overpass-api.de/api/",
        "https://overpass.kumi.systems/api/",
        "https://overpass.osm.ch/api/"
    };
    private static int currentUrlIndex = 0;
    private static OverpassApiService apiService;

    public static synchronized OverpassApiService getApiService() {
        if (apiService == null) {
            rebuildService();
        }
        return apiService;
    }

    private static void rebuildService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URLS[currentUrlIndex])
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(OverpassApiService.class);
    }

    public static synchronized void switchToNextServer() {
        currentUrlIndex = (currentUrlIndex + 1) % BASE_URLS.length;
        rebuildService();
    }
}
