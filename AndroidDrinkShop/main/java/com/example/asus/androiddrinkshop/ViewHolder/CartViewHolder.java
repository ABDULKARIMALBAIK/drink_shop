package com.example.asus.androiddrinkshop.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.asus.androiddrinkshop.R;

public class CartViewHolder extends RecyclerView.ViewHolder {

    public ImageView img_product;
    public TextView txt_product_name , txt_sugar_ice , txt_price;
    public ElegantNumberButton txt_amount;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public CartViewHolder(View itemView) {
        super(itemView);

        img_product = (ImageView)itemView.findViewById(R.id.img_product);
        txt_amount = (ElegantNumberButton)itemView.findViewById(R.id.txt_amount);
        txt_product_name = (TextView)itemView.findViewById(R.id.txt_product_name);
        txt_sugar_ice = (TextView)itemView.findViewById(R.id.txt_sugar_ice);
        txt_price = (TextView)itemView.findViewById(R.id.txt_price);

        view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground);
    }
}
