package com.example.recyclingapp.network;

import com.example.recyclingapp.network.MistralRequest;
import com.example.recyclingapp.network.MistralResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Retrofit-Interface fÃ¼r die Mistral Chat Completion API.
 */
public interface MistralApiService {
    @POST("v1/chat/completions")
    Call<MistralResponse> chat(
            @Header("Authorization") String authHeader,
            @Body MistralRequest request
    );
}

