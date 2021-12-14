package com.example.asus.androiddrinkshop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.androiddrinkshop.Adapter.OrderAdapter;
import com.example.asus.androiddrinkshop.Model.CheckUserResponse;
import com.example.asus.androiddrinkshop.Model.Order;
import com.example.asus.androiddrinkshop.Model.User;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        if (Common.currentUser.getPhone() != null){ //we access to this activity from inside the app

            compositeDisposable.add(mService.getOrder(Common.currentUser.getPhone() , statusCode)
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

        else{ //we access to this activity from Notification

//            Toast.makeText(this, "Please logging again !", Toast.LENGTH_SHORT).show();
//            finish();

            if (AccountKit.getCurrentAccessToken() != null){  //He have an facebook account kit

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {

                        final String numberPhone = account.getPhoneNumber().toString();

                        mService.checkUserExists(numberPhone)
                                .enqueue(new Callback<CheckUserResponse>() {
                                    @Override
                                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                        CheckUserResponse userResponse = response.body();
                                        if (userResponse.isExists()){

                                            //Fetch information (this status is like Login)
                                            mService.getUserInformation(numberPhone)
                                                    .enqueue(new Callback<User>() {
                                                        @Override
                                                        public void onResponse(Call<User> call, Response<User> response) {

                                                            //If user already exists , load orders
                                                            Common.currentUser = response.body();
                                                            loadOrder("0");
                                                        }

                                                        @Override
                                                        public void onFailure(Call<User> call, Throwable t) {
                                                            Toast.makeText(ShowOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                        else {

                                            Toast.makeText(ShowOrderActivity.this, "Please register first !", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                        Toast.makeText(ShowOrderActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("Big ERROR" , t.getMessage());
                                    }
                                });

                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Toast.makeText(ShowOrderActivity.this, accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


    }

    private void displayOrder(List<Order> orders) {

        OrderAdapter adapter = new OrderAdapter(this , orders);
        recycler_orders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrder("0");
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
