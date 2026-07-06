package com.example.recyclingapp.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OverpassApiService {
    @Headers({
        "User-Agent: RecyclingAppProject/1.0",
        "Accept: */*"
    })
    @FormUrlEncoded
    @POST("interpreter")
    Call<OverpassResponse> getRecyclingPoints(@Field("data") String query);
}
