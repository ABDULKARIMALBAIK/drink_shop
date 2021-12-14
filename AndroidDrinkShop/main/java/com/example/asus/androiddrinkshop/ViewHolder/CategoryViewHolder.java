package com.example.asus.androiddrinkshop.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.androiddrinkshop.Interface.IItemClickListener;
import com.example.asus.androiddrinkshop.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView img_product;
    public TextView txt_menu_name;

    IItemClickListener iItemClickListener;

    public CategoryViewHolder(View itemView) {
        super(itemView);

        img_product = (ImageView)itemView.findViewById(R.id.image_product);
        txt_menu_name = (TextView)itemView.findViewById(R.id.txt_menu_name);

        itemView.setOnClickListener(this);
    }

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    @Override
    public void onClick(View v) {

        iItemClickListener.onClick(v);

    }
}
