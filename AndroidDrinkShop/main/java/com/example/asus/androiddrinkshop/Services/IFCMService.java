package com.example.asus.androiddrinkshop.Services;

import com.example.asus.androiddrinkshop.Model.DataMessage;
import com.example.asus.androiddrinkshop.Model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAAMACQvis:APA91bGocxM_T3uzqg-amwSrqifaxyZDdhYouL3w3Tdym4wALeh-9zHptUzzWhyCmyEWdQIM4FI0p8Z6YdvrTddoSq-Svnxy9UW0gme-ViF2Nh5_x6BanXuoPM_BKmt_y4qeq4ffrtzu"
    })

    @POST("fcm/send")
    Call<FCMResponse> sendNotification(@Body DataMessage body);
}
