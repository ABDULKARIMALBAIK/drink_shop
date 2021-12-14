package com.example.asus.androiddrinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.asus.androiddrinkshop.ViewHolder.FavoriteViewHolder;
import com.example.asus.androiddrinkshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    Context context;
    List<Favorite> favoriteList;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.fav_item_layout , parent , false);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {

        Picasso.with(context)
                .load(favoriteList.get(position).link)
                .into(holder.img_product);
        holder.txt_product_name.setText(favoriteList.get(position).name);
        holder.txt_price.setText(new StringBuilder("$").append(favoriteList.get(position).price).toString());

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public void removeItem(int position){

        favoriteList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favorite item , int position){

        favoriteList.add(position , item);
        notifyItemInserted(position);
    }
}
