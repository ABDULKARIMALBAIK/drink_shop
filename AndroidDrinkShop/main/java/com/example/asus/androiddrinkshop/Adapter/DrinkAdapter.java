package com.example.asus.androiddrinkshop.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.asus.androiddrinkshop.Database.ModelDB.Cart;
import com.example.asus.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.asus.androiddrinkshop.Interface.IItemClickListener;
import com.example.asus.androiddrinkshop.Model.Drink;
import com.example.asus.androiddrinkshop.R;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.ViewHolder.DrinkViewHolder;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinks;

    public DrinkAdapter(Context context, List<Drink> drinks) {
        this.context = context;
        this.drinks = drinks;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.drink_item_layout , parent , false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, final int position) {

        holder.txt_drink_name.setText(drinks.get(position).getName());
        holder.txt_price.setText(new StringBuilder("$").append(drinks.get(position).getPrice()).toString());

        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddToCartDialog(position);
            }
        });

        Picasso.with(context)
                .load(drinks.get(position).getLink())
                .into(holder.img_product);

        //set favorites
        if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinks.get(position).ID)) == 1)
            holder.btn_favorites.setImageResource(R.drawable.ic_favorite_white_24dp);
        else
            holder.btn_favorites.setImageResource(R.drawable.ic_favorite_border_white_24dp);

        final DrinkViewHolder favorite_holder = holder;

        holder.btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinks.get(position).ID)) != 1){

                    addOrRemoveToFavorite(drinks.get(position) , true);  //true -> isAdd: mean you must add it to room DB
                    favorite_holder.btn_favorites.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
                else {

                    addOrRemoveToFavorite(drinks.get(position) , false);
                    favorite_holder.btn_favorites.setImageResource(R.drawable.ic_favorite_border_white_24dp);

                }
            }
        });
        
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, drinks.get(position).getName() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrRemoveToFavorite(Drink drink, boolean isAdd) {

        Favorite favorite = new Favorite();
        favorite.id = drink.ID;
        favorite.link = drink.Link;
        favorite.name = drink.Name;
        favorite.price = drink.Price;
        favorite.menuId = drink.MenuId;

        if (isAdd){

            Common.favoriteRepository.insertFav(favorite);
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        else{

            Common.favoriteRepository.delete(favorite);
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
        }


    }

    private void showAddToCartDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_to_cart_layout , null);

        //Init views of add_tp_cart
        ImageView img_product_dialog = (ImageView)view.findViewById(R.id.img_cart_product);
        final ElegantNumberButton txt_count = (ElegantNumberButton)view.findViewById(R.id.txt_count);
        TextView txt_product_dialog = (TextView)view.findViewById(R.id.txt_cart_product_name);
        EditText edt_comment = (EditText)view.findViewById(R.id.edt_comment);

        RadioButton rdi_sizeM = (RadioButton)view.findViewById(R.id.rdi_sizeM);
        RadioButton rdi_sizeL = (RadioButton)view.findViewById(R.id.rdi_sizeL);

        rdi_sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sizeOfCup = 0;
            }
        });
        rdi_sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sizeOfCup = 1;
            }
        });

        RadioButton rdi_sugar_100 = (RadioButton)view.findViewById(R.id.rdi_suger_100);
        RadioButton rdi_sugar_70 = (RadioButton)view.findViewById(R.id.rdi_suger_70);
        RadioButton rdi_sugar_50 = (RadioButton)view.findViewById(R.id.rdi_suger_50);
        RadioButton rdi_sugar_30 = (RadioButton)view.findViewById(R.id.rdi_suger_30);
        RadioButton rdi_sugar_free = (RadioButton)view.findViewById(R.id.rdi_suger_free);

        rdi_sugar_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sugar = 100;
            }
        });
        rdi_sugar_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sugar = 70;
            }
        });
        rdi_sugar_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sugar = 50;
            }
        });
        rdi_sugar_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sugar = 30;
            }
        });
        rdi_sugar_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.sugar = 0;
            }
        });

        RadioButton rdi_ice_100 = (RadioButton)view.findViewById(R.id.rdi_ice_100);
        RadioButton rdi_ice_70 = (RadioButton)view.findViewById(R.id.rdi_ice_70);
        RadioButton rdi_ice_50 = (RadioButton)view.findViewById(R.id.rdi_ice_50);
        RadioButton rdi_ice_30 = (RadioButton)view.findViewById(R.id.rdi_ice_30);
        RadioButton rdi_ice_free = (RadioButton)view.findViewById(R.id.rdi_ice_free);

        rdi_ice_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.ice = 100;
            }
        });
        rdi_ice_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.ice = 70;
            }
        });
        rdi_ice_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.ice = 50;
            }
        });
        rdi_ice_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.ice = 30;
            }
        });
        rdi_ice_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    Common.ice = 0;
            }
        });

        RecyclerView recycler_topping = (RecyclerView)view.findViewById(R.id.recycler_topping);
        recycler_topping.setLayoutManager(new LinearLayoutManager(context));
        recycler_topping.setHasFixedSize(true);

        MultiChoiceAdapter adapter = new MultiChoiceAdapter(context , Common.toppingList);
        recycler_topping.setAdapter(adapter);

        //set Data
        Picasso.with(context)
                .load(drinks.get(position).getLink())
                .into(img_product_dialog);
        txt_product_dialog.setText(drinks.get(position).getName());


        builder.setView(view);
        builder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                if (Common.sizeOfCup == -1){

                    Toast.makeText(context, "Please choose size of cup", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Common.sugar == -1){

                    Toast.makeText(context, "Please choose sugar", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Common.ice == -1){

                    Toast.makeText(context, "Please choose ice", Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(position , txt_count.getNumber());
                //dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showConfirmDialog(final int position, final String number) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.confirm_add_to_cart_layout , null);

        //View
        ImageView img_product_dialog = (ImageView)view.findViewById(R.id.img_product);
        final TextView txt_product_dialog = (TextView)view.findViewById(R.id.txt_cart_product_name);
        final TextView txt_product_price = (TextView)view.findViewById(R.id.txt_cart_product_price);
        TextView txt_ice = (TextView)view.findViewById(R.id.txt_ice);
        TextView txt_sugar = (TextView)view.findViewById(R.id.txt_sugar);
        final TextView txt_topping_extra = (TextView)view.findViewById(R.id.txt_topping_extra);

        ///////set values
        Picasso.with(context)
                .load(drinks.get(position).getLink())
                .into(img_product_dialog);

        txt_product_dialog.setText(new StringBuilder(drinks.get(position).getName()).append(" x")
                .append((Common.sizeOfCup == 0)? " Size M" : " Size L")
                .append(number).toString());

        txt_ice.setText(new StringBuilder("Ice: ").append(Common.ice).append("%").toString());
        txt_sugar.setText(new StringBuilder("Sugar: ").append(Common.sugar).append("%").toString());

        double price = (Double.parseDouble(drinks.get(position).getPrice()) * Double.parseDouble(number)) + Common.toppingPrice;
        if (Common.sizeOfCup == 1)  //Size L
            price += (3.0 * Double.parseDouble(number));

        StringBuilder topping_final_comment = new StringBuilder("");
        for (String line : Common.toppingAdded)
            topping_final_comment.append(line).append("\n");

        txt_topping_extra.setText(topping_final_comment);
        //////

        final double finalPrice = Math.round(price);

        txt_product_price.setText(new StringBuilder("$").append(finalPrice));

        builder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {

                    //Add to Room database
                    dialog.dismiss();

                    Cart cartItem = new Cart();
                    cartItem.name = drinks.get(position).getName();
                    cartItem.amount = Integer.parseInt(number);
                    cartItem.ice = Common.ice;
                    cartItem.sugar = Common.sugar;
                    cartItem.price = finalPrice;
                    cartItem.size = Common.sizeOfCup;
                    cartItem.toppingExtras = txt_topping_extra.getText().toString();
                    cartItem.link = drinks.get(position).getLink();

                    //Add to DB
                    Common.cartRepository.insertToCart(cartItem);

                    Log.d("Room_Debug", new Gson().toJson(cartItem));
                    Toast.makeText(context, "Save item to cart successfully !!!", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(view);
        builder.show();

    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }
}
