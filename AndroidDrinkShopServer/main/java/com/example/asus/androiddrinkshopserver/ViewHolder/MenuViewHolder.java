package com.example.asus.androiddrinkshopserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.androiddrinkshopserver.Interface.IItemClickListener;
import com.example.asus.androiddrinkshopserver.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{

    public ImageView img_product;
    public TextView txt_product;

    IItemClickListener iItemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        img_product = (ImageView)itemView.findViewById(R.id.image_product);
        txt_product = (TextView)itemView.findViewById(R.id.txt_menu_name);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    @Override
    public void onClick(View v) {

        iItemClickListener.onClick(v , false);

    }

    @Override
    public boolean onLongClick(View v) {

        iItemClickListener.onClick(v , true);
        return true;
    }
}
