package com.example.asus.androiddrinkshopserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.androiddrinkshopserver.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder {

    public ImageView img_order_item;
    public TextView txt_drink_name , txt_drink_amount , txt_sugar_ice , txt_size , txt_topping;

    public OrderDetailViewHolder(View itemView) {
        super(itemView);

        img_order_item = (ImageView)itemView.findViewById(R.id.img_order_item);
        txt_drink_name = (TextView)itemView.findViewById(R.id.txt_drink_name);
        txt_drink_amount = (TextView)itemView.findViewById(R.id.txt_drink_amount);
        txt_sugar_ice = (TextView)itemView.findViewById(R.id.txt_sugar_ice);
        txt_size = (TextView)itemView.findViewById(R.id.txt_size);
        txt_topping = (TextView)itemView.findViewById(R.id.txt_topping);
    }
}
