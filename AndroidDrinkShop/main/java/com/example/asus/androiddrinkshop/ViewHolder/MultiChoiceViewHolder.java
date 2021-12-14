package com.example.asus.androiddrinkshop.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.example.asus.androiddrinkshop.R;

public class MultiChoiceViewHolder extends RecyclerView.ViewHolder {

    public CheckBox checkBox;

    public MultiChoiceViewHolder(View itemView) {
        super(itemView);

        checkBox = (CheckBox)itemView.findViewById(R.id.ckb_topping);
    }
}
