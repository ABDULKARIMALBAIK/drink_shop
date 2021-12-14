package com.example.asus.androiddrinkshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.androiddrinkshop.Model.CheckUserResponse;
import com.example.asus.androiddrinkshop.Model.ReCaptchaResponse;
import com.example.asus.androiddrinkshop.Model.User;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private static final int PERMISSION_CODE =1001 ;

    Button btnContinue;

    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = Common.getAPI();

        btnContinue = (Button)findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (ActivityCompat.checkSelfPermission(MainActivity.this , Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this , Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
                    
                    if (Build.VERSION.SDK_INT >= 23)
                        requestPermissions(new String[]{Manifest.permission.INTERNET , Manifest.permission.ACCESS_NETWORK_STATE} , PERMISSION_CODE);
                }
                else {

                    if (Common.isConnectionToInternet(MainActivity.this))
                        //startLoginPage(LoginType.PHONE);
                        validate_reCaptcha();
                    else
                        Toast.makeText(MainActivity.this, "Please check connection WiFi !!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        printKeyHash();

    }

    private void validate_reCaptcha(){

        SafetyNet.getClient(this)
                .verifyWithRecaptcha(Common.SITE_KEY_RECAPTCHA)
                .addOnSuccessListener(this , new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {

                        if (!recaptchaTokenResponse.getTokenResult().isEmpty())
                            verifyTokenOnServer(recaptchaTokenResponse.getTokenResult());
                        else
                            Toast.makeText(MainActivity.this, "Token result is null", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(this , new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if (e instanceof ApiException){

                            ApiException apiException = (ApiException)e;
                            Log.d("ERROR_reCaptcha" , "ERROR" + CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                            Toast.makeText(MainActivity.this, "1- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Log.d("ERROR" , "UnKnown error");
                            Toast.makeText(MainActivity.this, "2- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }});

    }

    private void verifyTokenOnServer(String tokenResult) {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");

        mService.validate(tokenResult)
                .enqueue(new Callback<ReCaptchaResponse>() {
                    @Override
                    public void onResponse(Call<ReCaptchaResponse> call, Response<ReCaptchaResponse> response) {

                        waitingDialog.dismiss();

                        if (response.body().isSuccess()){
                            Toast.makeText(MainActivity.this, "reCaptcha is verified", Toast.LENGTH_SHORT).show();
                            startLoginPage(LoginType.PHONE);
                        }
                        else
                            Toast.makeText(MainActivity.this,"3- " +  response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<ReCaptchaResponse> call, Throwable t) {

                        waitingDialog.dismiss();
                        Toast.makeText(MainActivity.this, "4 " +  t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR" , t.getMessage());
                    }
                });
    }

    private void startLoginPage(LoginType loginType) {

            if (AccountKit.getCurrentAccessToken() != null){  //he have an account in Facebook Account kit -> auto login

                final android.app.AlertDialog alertDialog = new SpotsDialog(this);
                alertDialog.show();
                alertDialog.setMessage("Please waiting...");

                //Get User phone and check exists on server
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {

                        mService.checkUserExists(account.getPhoneNumber().toString())
                                .enqueue(new Callback<CheckUserResponse>() {
                                    @Override
                                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                        CheckUserResponse userResponse = response.body();
                                        if (userResponse.isExists()){

                                            //Fetch information (this status is like Login)
                                            mService.getUserInformation(account.getPhoneNumber().toString())
                                                    .enqueue(new Callback<User>() {
                                                        @Override
                                                        public void onResponse(Call<User> call, Response<User> response) {

                                                            //If user already exists , just start new Activity
                                                            alertDialog.dismiss();

                                                            Common.currentUser = response.body();

                                                            //start new Activity
                                                            startActivity(new Intent(MainActivity.this , HomeActivity.class));
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<User> call, Throwable t) {
                                                            Toast.makeText(MainActivity.this, "Server is close !", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                        else {

                                            //Else , need Register
                                            alertDialog.dismiss();
                                            showRegisterDialog(account.getPhoneNumber().toString());
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                        Toast.makeText(MainActivity.this, "Server is close !", Toast.LENGTH_LONG).show();
                                        Log.e("Big ERROR" , t.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Log.e("ERROR", accountKitError.getErrorType().getMessage());
                    }
                });
            }
            else {

                Intent intent = new Intent(MainActivity.this , AccountKitActivity.class);
                AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                        new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType , AccountKitActivity.ResponseType.TOKEN);

                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION , builder.build());
                startActivityForResult(intent , REQUEST_CODE);
            }



    }

    //Ctrl + O
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode ==RESULT_OK){

            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null){
                
                Toast.makeText(this, result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }
            else if (result.wasCancelled()){

                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
            else {

                if (result.getAccessToken() != null){

                    final android.app.AlertDialog alertDialog = new SpotsDialog(this);
                    alertDialog.show();
                    alertDialog.setMessage("Please waiting...");

                    //Get User phone and check exists on server
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {

                            mService.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                            CheckUserResponse userResponse = response.body();
                                            if (userResponse.isExists()){

                                                //Fetch information (this status is like Login)
                                                mService.getUserInformation(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {

                                                                //If user already exists , just start new Activity
                                                                alertDialog.dismiss();

                                                                Common.currentUser = response.body();  //Fix error

                                                                //start new Activity
                                                                startActivity(new Intent(MainActivity.this , HomeActivity.class));
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this,"Server is close !", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }
                                            else {

                                                //Else , need Register
                                                alertDialog.dismiss();
                                                showRegisterDialog(account.getPhoneNumber().toString());
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                            Toast.makeText(MainActivity.this, "Server is close !", Toast.LENGTH_LONG).show();
                                            Log.e("Big ERROR" , t.getMessage());
                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.e("ERROR", accountKitError.getErrorType().getMessage());
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        switch (requestCode){
            
            case PERMISSION_CODE: {
                
                if (grantResults[0] ==PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED){


                    if (Common.isConnectionToInternet(MainActivity.this))
                        //startLoginPage(LoginType.PHONE);
                        validate_reCaptcha();
                    else
                        Toast.makeText(MainActivity.this, "Please check connection WiFi !!!", Toast.LENGTH_SHORT).show();
                }

                else
                    Toast.makeText(this, "You can't use network service", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRegisterDialog(final String phone) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("REGISTER");

        View view = getLayoutInflater().inflate(R.layout.register_layout , null);

        //Init views from register layout
        final MaterialEditText edt_name = (MaterialEditText)view.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = (MaterialEditText)view.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthdate = (MaterialEditText)view.findViewById(R.id.edt_birthdate);
        Button btn_register = (Button)view.findViewById(R.id.btn_register);

        edt_birthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(view);
        builder.setCancelable(false);


        final AlertDialog dialog = builder.create();

        //Event register layout
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Close alertDialog
                dialog.dismiss();

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please waiting...");

                if (TextUtils.isEmpty(edt_address.getText().toString())){

                    Toast.makeText(MainActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_birthdate.getText().toString())){

                    Toast.makeText(MainActivity.this, "Please enter your birthdate", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_name.getText().toString())){

                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                mService.registerNewUser(phone,
                        edt_name.getText().toString(),
                        edt_address.getText().toString(),
                        edt_birthdate.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {

                                waitingDialog.dismiss();
                                User user = response.body();
                                if (TextUtils.isEmpty(user.getError_msg())){

                                    Toast.makeText(MainActivity.this, "User register successfully !!!", Toast.LENGTH_SHORT).show();

                                    Common.currentUser = response.body();

                                    //start new Activity
                                    startActivity(new Intent(MainActivity.this , HomeActivity.class));
                                    finish();
                                }

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                waitingDialog.dismiss();
                            }
                        });

            }
        });

        dialog.show();

    }

    private void printKeyHash() {

        try {

            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo("com.example.asus.androiddrinkshop",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures){

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash" , Base64.encodeToString(md.digest() , Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    //Exit Application when click Back button
    boolean isBackButtonDoubleClicked = false;
    //ctrl + O

    @Override
    public void onBackPressed() {

        if (isBackButtonDoubleClicked){

            super.onBackPressed();
            return;
        }

        this.isBackButtonDoubleClicked = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {

        this.isBackButtonDoubleClicked = false;
        super.onResume();
    }
}
