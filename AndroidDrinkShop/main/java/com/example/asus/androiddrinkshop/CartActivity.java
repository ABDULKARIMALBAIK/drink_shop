package com.example.asus.androiddrinkshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.asus.androiddrinkshop.Adapter.CartAdapter;
import com.example.asus.androiddrinkshop.Database.ModelDB.Cart;
import com.example.asus.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.asus.androiddrinkshop.Interface.RecyclerItemTouchHelperListener;
import com.example.asus.androiddrinkshop.Model.DataMessage;
import com.example.asus.androiddrinkshop.Model.FCMResponse;
import com.example.asus.androiddrinkshop.Model.OrderResult;
import com.example.asus.androiddrinkshop.Model.Token;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Services.IFCMService;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.Utils.RecyclerItemTouchHelper;
import com.example.asus.androiddrinkshop.ViewHolder.CartViewHolder;
import com.example.asus.androiddrinkshop.ViewHolder.FavoriteViewHolder;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    private static final int REQUEST_PAYMENT_CODE = 7777;
    RecyclerView recycler_cart;
    Button btn_place_order;

    CartAdapter adapter;
    List<Cart> cartList = new ArrayList<>();

    RelativeLayout rootLayout;

    CompositeDisposable compositeDisposable;
    IDrinkShopAPI mService;
    IDrinkShopAPI mServiceScalars;

    String token , amount , orderAddress , orderComment;
    int cartSize;
    Map<String,String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();

        mService = Common.getAPI();
        mServiceScalars = Common.getScalarsAPI();

        rootLayout =(RelativeLayout)findViewById(R.id.root_Layout);

        recycler_cart = (RecyclerView)findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);
        btn_place_order = (Button)findViewById(R.id.btn_place_order);

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
                if (cartSize > 0) 
                    placeOrder();
                else
                    Toast.makeText(CartActivity.this, "Cart is empty !", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0 , ItemTouchHelper.LEFT , CartActivity.this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

        //Set token
        token = Common.Tokenization_Key_Braintree;

        loadCartItems();

        //Not necessary this method
        //loadToken();

    }

    private void loadToken() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Common.API_TOKEN_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                waitingDialog.dismiss();
                btn_place_order.setEnabled(true);
                token = responseBody.toString();
                Toast.makeText(CartActivity.this, token, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                waitingDialog.dismiss();
                btn_place_order.setEnabled(false);
                Toast.makeText(CartActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeOrder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Submit Order");

        View view = LayoutInflater.from(this).inflate(R.layout.submit_order_layout , null);

        final EditText edt_comment = (EditText)view.findViewById(R.id.edt_comment);
        final EditText edt_other_address = (EditText)view.findViewById(R.id.edt_other_address);
        final RadioButton rdi_user_address = (RadioButton)view.findViewById(R.id.rdi_user_address);
        final RadioButton rdi_other_address = (RadioButton)view.findViewById(R.id.rdi_other_address);

        final RadioButton rdi_credit_card = (RadioButton)view.findViewById(R.id.rdi_credit_card);
        final RadioButton rdi_cod = (RadioButton)view.findViewById(R.id.rdi_cod);

        rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    edt_other_address.setEnabled(false);
            }
        });
        rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    edt_other_address.setEnabled(true);
            }
        });

        builder.setView(view);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (rdi_credit_card.isChecked()){

                    orderComment = edt_comment.getText().toString();

                    if (rdi_user_address.isChecked())
                        orderAddress = Common.currentUser.getAddress();
                    else if (rdi_other_address.isChecked())
                        orderAddress = edt_other_address.getText().toString();
                    else
                        orderAddress = "";

                    //Payment
                    DropInRequest dropInRequest = new DropInRequest().clientToken(token);
                    startActivityForResult(dropInRequest.getIntent(CartActivity.this) , REQUEST_PAYMENT_CODE);
                }
                else if (rdi_cod.isChecked()){

                    orderComment = edt_comment.getText().toString();

                    if (rdi_user_address.isChecked())
                        orderAddress = Common.currentUser.getAddress();
                    else if (rdi_other_address.isChecked())
                        orderAddress = edt_other_address.getText().toString();
                    else
                        orderAddress = "";

                    //Submit Order
                    compositeDisposable.add(
                            Common.cartRepository.getCartItems()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<List<Cart>>() {
                                        @Override
                                        public void accept(List<Cart> carts) throws Exception {

                                            if (!TextUtils.isEmpty(orderAddress))
                                                sendOrderToServer(Common.cartRepository.sumPrice() , carts , orderComment , orderAddress , "COD");
                                            else
                                                Toast.makeText(CartActivity.this, "Order Address can't null", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(CartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }));
                }

            }
        });

        builder.show();
    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress , String paymentMethod) {

        if (carts.size() > 0){

            String orderDetail = new Gson().toJson(carts);  //we have details of cart as List, so we convert List of cart to JSON

            mService.submitOrder(sumPrice , orderDetail , orderComment , orderAddress , Common.currentUser.getPhone() , paymentMethod)
                    .enqueue(new Callback<OrderResult>() {
                        @Override
                        public void onResponse(Call<OrderResult> call, Response<OrderResult> response) {

                            sendNotificationToServer(response.body());
                        }

                        @Override
                        public void onFailure(Call<OrderResult> call, Throwable t) {
                            Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();


    }

    private void sendNotificationToServer(final OrderResult orderResult) {

        //Get Server Token
        mService.getToken("server_app_01" , "1")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {

                        Map<String , String> contentSend = new HashMap<>();
                        contentSend.put("title" , "Drink Shop");
                        contentSend.put("message" , "You have new order" + orderResult.getOrderId());

                        DataMessage dataMessage = new DataMessage();

                        if (response.body().getToken() != null)
                            dataMessage.setTo(response.body().getToken());
                        dataMessage.setData(contentSend);

                        IFCMService ifcmService = Common.getFCMService();
                        ifcmService.sendNotification(dataMessage)
                                .enqueue(new Callback<FCMResponse>() {
                                    @Override
                                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                        if (response.code() == 200){
                                            if (response.body().success == 1){

                                                Toast.makeText(CartActivity.this, "Thank you , Order place", Toast.LENGTH_SHORT).show();

                                                //Clear Cart
                                                Common.cartRepository.emptyCart();
                                                finish();
                                            }
                                            else
                                                Toast.makeText(CartActivity.this, "Send notification failed !", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(CartActivity.this, "connectoin has failed !", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                                        Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {

                    }
                });
    }

    private void loadCartItems() {

        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        
                        cartSize = carts.size();
                        displayCartItem(carts);
                        
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(CartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
        
        
    }

    private void displayCartItem(List<Cart> carts) {

        cartList = carts;
        adapter = new CartAdapter(this , carts);
        recycler_cart.setAdapter(adapter);
    }

    private void sendPayment() {

        mServiceScalars.payment(params.get("nonce") , params.get("amount"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        
                        if (response.isSuccessful()){

                            Toast.makeText(CartActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                            //Submit Order
                            compositeDisposable.add(
                                    Common.cartRepository.getCartItems()
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<List<Cart>>() {
                                                @Override
                                                public void accept(List<Cart> carts) throws Exception {

                                                    if (!TextUtils.isEmpty(orderAddress))
                                                        sendOrderToServer(Common.cartRepository.sumPrice() , carts , orderComment , orderAddress , "Braintree");
                                                    else
                                                        Toast.makeText(CartActivity.this, "Order Address can't null", Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Consumer<Throwable>() {
                                                @Override
                                                public void accept(Throwable throwable) throws Exception {
                                                    Toast.makeText(CartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }));

                        }
                        else {

                            Toast.makeText(CartActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("PAYMENT_INFO" , response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("PAYMENT_INFO" , t.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT_CODE){
            if (resultCode == RESULT_OK){

                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();

                if (Common.cartRepository.sumPrice() > 0){

                    amount = String.valueOf(Common.cartRepository.sumPrice());
                    params = new HashMap<>();

                    params.put("amount" , amount);
                    params.put("nonce" , strNonce);

                    sendPayment();
                }
                else {

                    Toast.makeText(this, "Payment amount is 0", Toast.LENGTH_SHORT).show();
                }
            }
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();

            else {

                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("PAYMENT_ERROR" , error.getMessage());
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

    @Override
    protected void onResume() {
        super.onResume();

        loadCartItems();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartViewHolder) {

            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deleteItem = cartList.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            //Delete Item from adapter
            adapter.removeItem(deleteIndex);
            //Delete Item from Room database
            Common.cartRepository.deleteCartItem(deleteItem);

            //Make Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + " is removed from favorites !", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapter.restoreItem(deleteItem, deleteIndex);
                    Common.cartRepository.insertToCart(deleteItem);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
