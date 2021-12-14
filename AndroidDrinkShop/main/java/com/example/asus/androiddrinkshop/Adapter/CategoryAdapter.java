package com.example.asus.androiddrinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.androiddrinkshop.DrinkActivity;
import com.example.asus.androiddrinkshop.Interface.IItemClickListener;
import com.example.asus.androiddrinkshop.Model.Category;
import com.example.asus.androiddrinkshop.R;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    Context context;
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_layout , parent , false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {

        holder.txt_menu_name.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getLink())
                .placeholder(R.color.colorPrimary)
                .into(holder.img_product);

        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {

                Common.currentCategory = categories.get(position);

                //start new Activity
                context.startActivity(new Intent(context , DrinkActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
