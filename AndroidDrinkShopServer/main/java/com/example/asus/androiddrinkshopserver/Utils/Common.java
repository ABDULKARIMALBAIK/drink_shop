package com.example.asus.androiddrinkshopserver.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.asus.androiddrinkshopserver.Model.Category;
import com.example.asus.androiddrinkshopserver.Model.Drink;
import com.example.asus.androiddrinkshopserver.Model.Order;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Retrofit.RetrofitClient;
import com.example.asus.androiddrinkshopserver.Services.FCMClient;
import com.example.asus.androiddrinkshopserver.Services.IFCMServices;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Common {

    public static final String BASE_URL = "http://192.168.1.101:80/drinkshop/";
    public static final String FCM_URL = "https://fcm.googleapis.com/";

    public static Category currentCategory;
    public static Drink currentDrink;
    public static Order currentOrder;

    public static List<Category> menuList = new ArrayList<>();

    public static IDrinkShopAPI getAPI(){

        return RetrofitClient.getInstance(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static IFCMServices getFCMService(){

        return FCMClient.getInstance(FCM_URL).create(IFCMServices.class);
    }

    public static boolean isConnectionToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){

                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String convertCodeToStatus(int orderStatus) {

        switch (orderStatus){

            case 0:{
                return "Placed";
            }
            case 1:{
                return "Processing";
            }
            case 2:{
                return "Shipping";
            }
            case 3:{
                return "Shipped";
            }
            case -1:{
                return "Cancelled";
            }
            default:{
                return "Order Error";
            }

        }
    }
}
