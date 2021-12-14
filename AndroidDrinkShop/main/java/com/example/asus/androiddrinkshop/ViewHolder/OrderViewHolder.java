package com.example.asus.androiddrinkshop.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.asus.androiddrinkshop.Interface.IItemClickListener;
import com.example.asus.androiddrinkshop.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_order_id , txt_order_price , txt_order_address , txt_order_comment , txt_order_status;
    IItemClickListener iItemClickListener;

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    public OrderViewHolder(View itemView) {
        super(itemView);

        txt_order_id = (TextView)itemView.findViewById(R.id.txt_order_id);
        txt_order_price = (TextView)itemView.findViewById(R.id.txt_order_price);
        txt_order_address = (TextView)itemView.findViewById(R.id.txt_order_address);
        txt_order_comment = (TextView)itemView.findViewById(R.id.txt_order_comment);
        txt_order_status = (TextView)itemView.findViewById(R.id.txt_order_status);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        iItemClickListener.onClick(v);
    }
}
