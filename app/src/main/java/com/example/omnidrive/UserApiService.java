package com.example.omnidrive;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    @GET("users/{email}")
    Call<User> getUser(@Path("email") String id);

    @POST("users/{email}")
    Call<User> updateUser(@Path("email") String id, @Body UserRequest body);

    @POST("users")
    Call<User> newUser(@Body UserRequest body);

}

