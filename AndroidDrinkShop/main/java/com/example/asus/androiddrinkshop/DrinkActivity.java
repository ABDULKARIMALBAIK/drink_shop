package com.example.asus.androiddrinkshop;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.androiddrinkshop.Adapter.DrinkAdapter;
import com.example.asus.androiddrinkshop.Model.Drink;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DrinkActivity extends AppCompatActivity {

    RecyclerView lst_drink;
    TextView txt_banner_name;
    SwipeRefreshLayout swipeRefreshLayout;

    IDrinkShopAPI mService;


    //Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        mService = Common.getAPI();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swip_to_refresh);

        lst_drink = (RecyclerView)findViewById(R.id.recycler_drink);
        lst_drink.setLayoutManager(new GridLayoutManager(this , 2));
        lst_drink.setHasFixedSize(true);

        txt_banner_name = (TextView)findViewById(R.id.txt_menu_name);
        txt_banner_name.setText(Common.currentCategory.getName());

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(true);
                loadListDrink(Common.currentCategory.getID());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                loadListDrink(Common.currentCategory.getID());
            }
        });
    }

    private void loadListDrink(String menuId) {

        compositeDisposable.add(mService.getDrink(menuId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<Drink>>() {
                                    @Override
                                    public void accept(List<Drink> drinks) throws Exception {

                                        displayDrinkList(drinks);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(DrinkActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
    }

    private void displayDrinkList(List<Drink> drinks) {

        DrinkAdapter adapter = new DrinkAdapter(this , drinks);
        lst_drink.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }
}
