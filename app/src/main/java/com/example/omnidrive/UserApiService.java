package com.example.omnidrive;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    @GET("users/{email}")
    Call<User> getUser(@Path("email") String id);
}
