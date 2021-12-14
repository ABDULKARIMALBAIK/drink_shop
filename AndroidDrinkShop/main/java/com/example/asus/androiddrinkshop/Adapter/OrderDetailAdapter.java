package com.example.asus.androiddrinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.androiddrinkshop.Database.ModelDB.Cart;
import com.example.asus.androiddrinkshop.R;
import com.example.asus.androiddrinkshop.ViewHolder.OrderDetailViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {

    private Context context;
    private List<Cart> cartList;

    public OrderDetailAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_layout , parent , false);

        return new OrderDetailViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailViewHolder holder, final int position) {

        Picasso.with(context)
                .load(cartList.get(position).link)
                .into(holder.img_product);

        holder.txt_price.setText(new StringBuilder("$").append(cartList.get(position).price));

        holder.txt_product_name.setText(new StringBuilder(cartList.get(position).name)
                .append(" x").append(cartList.get(position).amount).append(" ")
                .append((cartList.get(position).size == 0)? " Size M":" Size L"));

        holder.txt_sugar_ice.setText(
                new StringBuilder("Sugar: ").append(cartList.get(position).sugar).append("%").append("\n")
                        .append("Ice: ").append(cartList.get(position).ice).append("%").toString() );

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position){

        cartList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Cart item , int position){

        cartList.add(position , item);
        notifyItemInserted(position);
    }
}
