package com.example.asus.androiddrinkshop.Database.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.asus.androiddrinkshop.Database.ModelDB.Cart;
import com.example.asus.androiddrinkshop.Database.ModelDB.Favorite;

@Database(entities = {Cart.class , Favorite.class} , version = 1 , exportSchema = false)
public abstract class ABDRoomDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();

    private static ABDRoomDatabase instance;

    public static ABDRoomDatabase getInstance(Context context){

        if (instance == null)
            instance = Room.databaseBuilder(context , ABDRoomDatabase.class , "Abd_DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }

}
