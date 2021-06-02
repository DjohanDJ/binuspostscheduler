package com.example.binuspostscheduler.twitter;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface TwitterAPI {

    @POST("statuses/updates.json")
    void postTweet(
            @QueryMap Map<String, String> options,
            @HeaderMap Map<String, String> headers
    );
}
