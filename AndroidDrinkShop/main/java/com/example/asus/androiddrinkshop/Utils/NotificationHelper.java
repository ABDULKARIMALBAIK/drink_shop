package com.example.asus.androiddrinkshop.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import com.example.asus.androiddrinkshop.R;

public class NotificationHelper extends ContextWrapper{

    private static final String ABD_CHANNEL_ID = "com.example.asus.androiddrinkshop.ABDULKARIM";
    private static final String ABD_CHANNEL_NAME = "Drink Shop";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){  //only working this function if API is 26 or higher

            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel abdChannel = new NotificationChannel(ABD_CHANNEL_ID,
                ABD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        abdChannel.enableLights(false);
        abdChannel.enableVibration(true);
        abdChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(abdChannel);
    }

    public NotificationManager getManager() {

        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getDrinkShopNotification(String title , String message , Uri soundUri , PendingIntent contentIntent){

        return new android.app.Notification.Builder(getApplicationContext() , ABD_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_start_1)
                .setSound(soundUri)
                .setAutoCancel(true);
    }

}
