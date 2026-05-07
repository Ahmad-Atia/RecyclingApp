package com.example.recyclingapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit-Client fÃ¼r die Mistral-API.
 * Stellt eine einzige Instanz des MistralApiService bereit.
 */
public class MistralClient {
    private static final String BASE_URL = "https://api.mistral.ai/";

    public static MistralApiService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(MistralApiService.class);
    }
}

