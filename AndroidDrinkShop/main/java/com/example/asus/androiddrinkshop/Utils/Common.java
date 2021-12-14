package com.example.asus.androiddrinkshop.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.asus.androiddrinkshop.Database.DataSource.CartRepository;
import com.example.asus.androiddrinkshop.Database.DataSource.FavoriteRepository;
import com.example.asus.androiddrinkshop.Database.Local.ABDRoomDatabase;
import com.example.asus.androiddrinkshop.Model.Category;
import com.example.asus.androiddrinkshop.Model.Drink;
import com.example.asus.androiddrinkshop.Model.Order;
import com.example.asus.androiddrinkshop.Model.User;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Retrofit.RetrofitClient;
import com.example.asus.androiddrinkshop.Retrofit.RetrofitScalarsClient;
import com.example.asus.androiddrinkshop.Services.FCMClient;
import com.example.asus.androiddrinkshop.Services.IFCMService;

import java.util.ArrayList;
import java.util.List;

public class Common {

    /*
    * Very important steps:
    * 1-to able to using WebServices , must run your PC, run network and XAMPP    (always)
    * 2-then change some things , do this:
    *  go to Control Panel-> All Control Panel Items-> Windows Defender Firewall-> Allowed apps on features...
    *  search on Apache HTTP Server (twice) and mysqld (twice) THEN make sure are checked to both (public and private) THEN click OK
    *3-Open XAMPP Control Panel then click on (config -> Apache(httpd.conf) ) you will see notepad file and do this:
    *  search on <Directory /> you must see Form like this
    *
    *  <Directory />
    *  AllowOverride none
    *  Require all denied
    *  </Directory>
    *
    *  change this information to:
    *
    *  <Directory />
    *  AllowOverride All
    *  Require all granted
    *  </Directory>
    *
    * 4-to making this URL below: open command prompt then write (ipconfig) to get ip address and catch it(IPv4 Address 192.168.X.X)
     * and port is 80 from XAMPP then put
    * them in the URL like url form below (OR) make IP address is static by go to control panel-> Networks-> network adapter-> my network->
    * properties-> ivp4 right then properties button-> set IPV4 and subnset mask ...
    * */

    //to put Card Number for VISA Card put 4111 1111 1111 1111

    public static final String BASE_URL = "http://192.168.1.101:80/drinkshop/";

    public static final String API_TOKEN_URL = "http://192.168.1.101:80/drinkshop/braintree/main.php";
    public static final String Tokenization_Key_Braintree= "sandbox_kttyn7ts_4cpx96q4xxm83hq8";

    public static final String SITE_KEY_RECAPTCHA = "6LeC0Z8UAAAAAPuJwRHmP4S7gczFKIBuHM9q5kdD";
    private static final String FCM_API = "https://fcm.googleapis.com/";

    public static List<Drink> toppingList = new ArrayList<>();
    public static final String TOPPING_MENU_ID = "7";
    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded = new ArrayList<>();

    public static int sizeOfCup = -1; //-1 : no chose (error) , 0: M , 1: L
    public static int sugar = -1; //-1 : no chose (error) , 0: free ,
    public static int ice = -1;

    public static User currentUser = null;
    public static Category currentCategory = null;
    public static Order currentOrder = null;

    //Room Database
    public static ABDRoomDatabase abdRoomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;

    public static IDrinkShopAPI getAPI(){

        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static IDrinkShopAPI getScalarsAPI(){

        return RetrofitScalarsClient.getScalarsClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static IFCMService getFCMService(){

        return FCMClient.getInstance(FCM_API).create(IFCMService.class);
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
