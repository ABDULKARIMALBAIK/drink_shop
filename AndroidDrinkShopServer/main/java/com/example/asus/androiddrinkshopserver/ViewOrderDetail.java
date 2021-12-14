package com.example.asus.androiddrinkshopserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.androiddrinkshopserver.Adapter.OrderDetailAdapter;
import com.example.asus.androiddrinkshopserver.Model.DataMessage;
import com.example.asus.androiddrinkshopserver.Model.FCMResponse;
import com.example.asus.androiddrinkshopserver.Model.Order;
import com.example.asus.androiddrinkshopserver.Model.Token;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Services.IFCMServices;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderDetail extends AppCompatActivity {

    TextView txt_order_id , txt_order_price , txt_order_comment , txt_order_address;
    MaterialSpinner spinner;
    RecyclerView recycler_order_detail;

    //Declare value for spinner
    String[] spinner_source = new String[]{
            "Cancelled", //index 0
            "Placed", //index 1
            "Processed", //index 2
            "Shipping", //index 3
            "Shipped" //index 4
    };

    IDrinkShopAPI mService;
    IFCMServices ifcmServices;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_detail);

        mService = Common.getAPI();
        ifcmServices = Common.getFCMService();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Order Detail");
        setSupportActionBar(toolbar);

        txt_order_id = (TextView)findViewById(R.id.txt_order_id);
        txt_order_price = (TextView)findViewById(R.id.txt_order_price);
        txt_order_comment = (TextView)findViewById(R.id.txt_order_comment);
        txt_order_address = (TextView)findViewById(R.id.txt_order_address);

        spinner = (MaterialSpinner)findViewById(R.id.spinner_order_status);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_item , spinner_source);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        recycler_order_detail = (RecyclerView)findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        recycler_order_detail.setLayoutManager(new LinearLayoutManager(this));
        recycler_order_detail.setAdapter(new OrderDetailAdapter(this));

        //Set Date to order
        txt_order_id.setText(new StringBuilder("#").append(Common.currentOrder.getOrderId()));
        txt_order_price.setText(new StringBuilder("$").append(Common.currentOrder.getOrderPrice()));
        txt_order_address.setText(Common.currentOrder.getOrderAddress());
        txt_order_comment.setText(Common.currentOrder.getOrderComment());

        setSpinnerSelectedBaseOnOrderStatus();

    }

    private void setSpinnerSelectedBaseOnOrderStatus() {

        switch (Common.currentOrder.getOrderStatus()){

            case -1:{
                spinner.setSelectedIndex(0); //Cancelled
                break;
            }
            case 0:{
                spinner.setSelectedIndex(1); //Placed
                break;
            }
            case 1:{
                spinner.setSelectedIndex(2); //Processed
                break;
            }
            case 2:{
                spinner.setSelectedIndex(3); //Shipping
                break;
            }
            case 3:{
                spinner.setSelectedIndex(4); //Shipped
                break;
            }


        }
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_order_detail , menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_save_order_detail){

            saveUpdateOrder();
        }


        return true;
    }

    private void saveUpdateOrder() {

        final int order_status = spinner.getSelectedIndex() - 1;

        compositeDisposable.add(mService.updateOrderStatus(Common.currentOrder.getUserPhone(),
                Common.currentOrder.getOrderId(),
                order_status)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {


                sendOrderUpdateNotification(Common.currentOrder , order_status);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ViewOrderDetail.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void sendOrderUpdateNotification(final Order currentOrder , final int orderStatus) {

        //Get Token fo owner Order
        mService.getToken(currentOrder.getUserPhone() , "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {

                        Token userToken = response.body();

                        Map<String , String> dataSend = new HashMap<>();
                        dataSend.put("title" , "Your order has been update");
                        dataSend.put("message" , "Order #" + currentOrder.getOrderId() + " has been update to " + Common.convertCodeToStatus(orderStatus));

                        DataMessage dataMessage = new DataMessage();
                        dataMessage.setTo(userToken.getToken());
                        dataMessage.setData(dataSend);

                        ifcmServices.sendNotification(dataMessage)
                                .enqueue(new Callback<FCMResponse>() {
                                    @Override
                                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                                        if (response.body().success == 1){

                                            Toast.makeText(ViewOrderDetail.this, "Order Updated !", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                                        Toast.makeText(ViewOrderDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Toast.makeText(ViewOrderDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
