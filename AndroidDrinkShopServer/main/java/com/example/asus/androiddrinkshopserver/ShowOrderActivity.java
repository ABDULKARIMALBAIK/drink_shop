package com.example.asus.androiddrinkshopserver;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.androiddrinkshopserver.Adapter.OrderAdapter;
import com.example.asus.androiddrinkshopserver.Model.Order;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Utils.Common;

import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ShowOrderActivity extends AppCompatActivity {

    RecyclerView recycler_orders;
    BottomNavigationView bottomNavigationView;

    IDrinkShopAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);

        mService = Common.getAPI();

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.order_new:{
                        loadOrder("0");
                        break;
                    }
                    case R.id.order_cancel:{
                        loadOrder("-1");
                        break;
                    }
                    case R.id.order_processing:{
                        loadOrder("1");
                        break;
                    }
                    case R.id.order_shipping:{
                        loadOrder("2");
                        break;
                    }
                    case R.id.order_shipped:{
                        loadOrder("3");
                        break;
                    }
                }

                return true;
            }
        });

        recycler_orders = (RecyclerView)findViewById(R.id.recycler_orders);
        recycler_orders.setHasFixedSize(true);
        recycler_orders.setLayoutManager(new LinearLayoutManager(this));

        loadOrder("0");
    }

    private void loadOrder(String statusCode) {

        compositeDisposable.add(mService.getAllOrder(statusCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Order>>() {
                    @Override
                    public void accept(List<Order> orders) throws Exception {
                        displayOrder(orders);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(ShowOrderActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void displayOrder(List<Order> orders) {

        Collections.reverse(orders); //for show newest order first
        OrderAdapter adapter = new OrderAdapter(this , orders);
        recycler_orders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (bottomNavigationView.getSelectedItemId()){

            case R.id.order_new:{
                loadOrder("0");
                break;
            }
            case R.id.order_cancel:{
                loadOrder("-1");
                break;
            }
            case R.id.order_processing:{
                loadOrder("1");
                break;
            }
            case R.id.order_shipping:{
                loadOrder("2");
                break;
            }
            case R.id.order_shipped:{
                loadOrder("3");
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
