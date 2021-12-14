package com.example.asus.androiddrinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.asus.androiddrinkshop.Database.ModelDB.Cart;
import com.example.asus.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.ViewHolder.CartViewHolder;

import java.util.List;
import com.example.asus.androiddrinkshop.R;
import com.squareup.picasso.Picasso;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private Context context;
    private List<Cart> cartList;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout , parent , false);

        return new CartViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        Picasso.with(context)
                .load(cartList.get(position).link)
                .into(holder.img_product);

        holder.txt_amount.setNumber(String.valueOf(cartList.get(position).amount));
        holder.txt_price.setText(new StringBuilder("$").append(cartList.get(position).price));

        holder.txt_product_name.setText(new StringBuilder(cartList.get(position).name)
            .append(" x").append(cartList.get(position).amount)
            .append((cartList.get(position).size == 0)? " Size M":" Size L"));

        holder.txt_sugar_ice.setText(
                new StringBuilder("Sugar: ").append(cartList.get(position).sugar).append("%").append("\n")
                .append("Ice: ").append(cartList.get(position).ice).append("%").toString() );

        //Get Price of one cup with all options
        final double priceOneCup = cartList.get(position).price / cartList.get(position).amount;

        //Auto save item when user change amount
        holder.txt_amount.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Cart cart = cartList.get(position);
                cart.amount = newValue;
                cart.price = Math.round(priceOneCup * newValue);

                Common.cartRepository.updateToCart(cart);

                holder.txt_price.setText(new StringBuilder("$").append(cartList.get(position).price));
            }
        });
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
