package com.example.asus.androiddrinkshopserver.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMClient {

    public static Retrofit instance = null;

    public static Retrofit getInstance(String baseUrl){

        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return instance;
    }
}
