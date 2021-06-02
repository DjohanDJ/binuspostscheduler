package com.example.binuspostscheduler.twitter;

import com.google.gson.annotations.SerializedName;

public class TwitterModel {

    @SerializedName("access_token")
    private String access_token;
    @SerializedName("statuscode")
    private String access_token_secret;
    @SerializedName("statuscode")
    public final String API_KEY = "t0zwc1GRA4mjoHsa3HwM1Q8fJ";
    @SerializedName("statuscode")
    public final String API_SECRET = "pXwXPViI3q2eIwTPZmoNhDHBvGqSl6gPFuCI8WjxYLKLWb6kPJ";

    public TwitterModel(String access_token,String access_token_secret){
        this.access_token = access_token;
        this.access_token_secret = access_token_secret;

    }



    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token_secret() {
        return access_token_secret;
    }

    public void setAccess_token_secret(String access_token_secret) {
        this.access_token_secret = access_token_secret;
    }

    //convert a data class to a map

}
