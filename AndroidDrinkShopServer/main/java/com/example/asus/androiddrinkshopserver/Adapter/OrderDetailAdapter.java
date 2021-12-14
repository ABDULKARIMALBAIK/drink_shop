package com.example.asus.androiddrinkshopserver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.androiddrinkshopserver.Model.Cart;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.ViewHolder.OrderDetailViewHolder;
import com.example.asus.androiddrinkshopserver.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {

    Context context;
    List<Cart> cartList;

    public OrderDetailAdapter(Context context) {
        this.context = context;
        this.cartList = new Gson().fromJson(Common.currentOrder.getOrderDetail() , new TypeToken<List<Cart>>(){}.getType());
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_layout , parent , false);

        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {

        holder.txt_drink_amount.setText("" + cartList.get(position).getAmount());
        holder.txt_drink_name.setText(new StringBuilder(cartList.get(position).getName()));
        holder.txt_size.setText(cartList.get(position).getSize() == 0 ? "Size M" : "Size L");

        holder.txt_sugar_ice.setText(new StringBuilder("Sugar: ").append(cartList.get(position).getSugar())
        .append(" , Ice: ").append(cartList.get(position).getIce()));

        //Fix error if topping is null -> appear crush , else we have we have topping in this order
        if (cartList.get(position).getToppingExtras() != null && !cartList.get(position).getToppingExtras().isEmpty()){

            String topping_format = cartList.get(position).getToppingExtras().replace("\\n" , ",");
            topping_format = topping_format.substring(0 , topping_format.length() - 1);
            holder.txt_topping.setText(topping_format);
        }
        else
            holder.txt_topping.setText("None");


        Picasso.with(context)
                .load(cartList.get(position).getLink())
                .into(holder.img_order_item);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
