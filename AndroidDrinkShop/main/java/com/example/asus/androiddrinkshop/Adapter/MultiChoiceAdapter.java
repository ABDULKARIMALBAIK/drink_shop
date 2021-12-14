package com.example.asus.androiddrinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.asus.androiddrinkshop.Model.Drink;
import com.example.asus.androiddrinkshop.R;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.ViewHolder.MultiChoiceViewHolder;

import java.util.List;

public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceViewHolder> {

    Context context;
    List<Drink> optionList;

    public MultiChoiceAdapter(Context context, List<Drink> optionList) {
        this.context = context;
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public MultiChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.multi_check_layout , null);

        return new MultiChoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiChoiceViewHolder holder, final int position) {

        holder.checkBox.setText(optionList.get(position).getName());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    Common.toppingAdded.add(buttonView.getText().toString());
                    Common.toppingPrice += Double.parseDouble(optionList.get(position).getPrice());
                }
                else {

                    Common.toppingAdded.remove(buttonView.getText().toString());
                    Common.toppingPrice -= Double.parseDouble(optionList.get(position).getPrice());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }
}
