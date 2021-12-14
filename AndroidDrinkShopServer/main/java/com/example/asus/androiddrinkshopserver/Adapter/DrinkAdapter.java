package com.example.asus.androiddrinkshopserver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.androiddrinkshopserver.Interface.IItemClickListener;
import com.example.asus.androiddrinkshopserver.Model.Drink;
import com.example.asus.androiddrinkshopserver.R;
import com.example.asus.androiddrinkshopserver.UpdateCategoryActivity;
import com.example.asus.androiddrinkshopserver.UpdateProductActivity;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.ViewHolder.DrinkViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.drink_item_layout , parent , false);

        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, final int position) {

        Picasso.with(context)
                .load(drinkList.get(position).getLink())
                .into(holder.img_product);

        holder.txt_price.setText(new StringBuilder("$").append(drinkList.get(position).getPrice()).toString());
        holder.txt_drink_name.setText(drinkList.get(position).getName());

        //Event anti crush null item click !!!!
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v, boolean isLongClick) {

                Common.currentDrink = drinkList.get(position);
                context.startActivity(new Intent(context , UpdateProductActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}
