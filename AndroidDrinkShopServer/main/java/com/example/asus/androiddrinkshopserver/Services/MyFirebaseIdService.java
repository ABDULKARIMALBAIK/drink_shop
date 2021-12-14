package com.example.asus.androiddrinkshopserver.Services;

import android.util.Log;

import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

            updateTokenToFirebase();
    }

    private void updateTokenToFirebase() {

        IDrinkShopAPI mService = Common.getAPI();
        mService.updateToken("server_app_01",
                FirebaseInstanceId.getInstance().getToken(),
                "1")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.d("DEBUG" , response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("DEBUG" , t.getMessage());
                    }
                });
    }
}
