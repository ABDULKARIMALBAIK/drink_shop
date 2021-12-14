package com.example.asus.androiddrinkshopserver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.androiddrinkshopserver.DrinkListActivity;
import com.example.asus.androiddrinkshopserver.Interface.IItemClickListener;
import com.example.asus.androiddrinkshopserver.Model.Category;
import com.example.asus.androiddrinkshopserver.UpdateCategoryActivity;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.ViewHolder.MenuViewHolder;

import java.util.List;
import com.example.asus.androiddrinkshopserver.R;
import com.squareup.picasso.Picasso;

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    Context context;
    List<Category> categoryList;

    public MenuAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_layout , parent , false);

        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, final int position) {

        holder.txt_product.setText(categoryList.get(position).getName());
        Picasso.with(context)
                .load(categoryList.get(position).getLink())
                .into(holder.img_product);

        //Implement item click
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v, boolean isLongClick) {

                if (isLongClick){

                    //Assign this category to variable global
                    Common.currentCategory = categoryList.get(position);
                    //Start new Activity
                    context.startActivity(new Intent(context , UpdateCategoryActivity.class));
                }
                else {

                    //Assign this category to variable global
                    Common.currentCategory = categoryList.get(position);
                    //Start new Activity
                    context.startActivity(new Intent(context , DrinkListActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
