package com.example.asus.androiddrinkshopserver;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.androiddrinkshopserver.Interface.UploadCallBack;
import com.example.asus.androiddrinkshopserver.Model.Category;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.Utils.ProgressRequestBody;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductActivity extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_IMAGE_CODE = 7777;
    MaterialSpinner spinner_menu;

    ImageView img_browser;
    EditText edt_name , edt_price;
    Button btn_update , btn_delete;

    IDrinkShopAPI mService;
    CompositeDisposable compositeDisposable;

    Uri selected_img_uri = null;
    String uploaded_img_path = "";
    String selected_category = "";

    Map<String , String> menu_data_for_get_key = new HashMap<>();
    Map<String , String> menu_data_for_get_value = new HashMap<>();
    List<String> menu_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        if (Common.currentDrink != null){

            uploaded_img_path = Common.currentDrink.getLink();
            selected_category = Common.currentDrink.getMenuId();
        }

        //Init API
        mService = Common.getAPI();
        //Rxjava
        compositeDisposable = new CompositeDisposable();

        //Set category of drink
        selected_category = Common.currentDrink.getMenuId();

        //init views
        edt_name = (EditText)findViewById(R.id.edt_drink_name);
        edt_price = (EditText)findViewById(R.id.edt_drink_price);
        img_browser = (ImageView)findViewById(R.id.img_drink);
        btn_update = (Button)findViewById(R.id.btn_update);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        spinner_menu = (MaterialSpinner)findViewById(R.id.spinner_menu_id);

        //spinner_menu.setText(selected_category);

        img_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Image") , PICK_IMAGE_CODE);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProduct();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        spinner_menu.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selected_category = menu_data_for_get_key.get(menu_data.get(position));
            }
        });

        setSpinnerMenu();
        setProductInfo();
    }

    private void setProductInfo() {

        if (Common.currentDrink != null){

            edt_name.setText(Common.currentDrink.getName());
            edt_price.setText(Common.currentDrink.getPrice());

            Picasso.with(this)
                    .load(Common.currentDrink.getLink())
                    .into(img_browser);

            spinner_menu.setSelectedIndex(menu_data.indexOf(menu_data_for_get_value.get(Common.currentCategory.getID())));
        }
    }

    private void deleteProduct() {

        compositeDisposable.add(mService.deleteProduct(Common.currentDrink.getID())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {

                                        Toast.makeText(UpdateProductActivity.this, s, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(UpdateProductActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }));
    }

    private void updateProduct() {

        compositeDisposable.add(mService.updateProduct(
                Common.currentDrink.getID(),
                edt_name.getText().toString(),
                uploaded_img_path,
                edt_price.getText().toString(),
                selected_category
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                Toast.makeText(UpdateProductActivity.this, s, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(UpdateProductActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }));
    }

    private void setSpinnerMenu() {

        for (Category category : Common.menuList){

            menu_data_for_get_key.put(category.getName() , category.getID());
            menu_data_for_get_value.put(category.getID() , category.getName());

            menu_data.add(category.getName());
        }
        spinner_menu.setItems(menu_data);
    }

    private void uploadFileToServer() {

        if (selected_img_uri != null){

            File image = FileUtils.getFile(this , selected_img_uri);
            String imageName =  new StringBuilder(UUID.randomUUID().toString())
                    .append(FileUtils.getExtension(image.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(image , UpdateProductActivity.this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" , imageName , requestFile);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    mService.uploadProductFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    //After upload , we will get file name and return String contain link of image
                                    uploaded_img_path = new StringBuilder(Common.BASE_URL)
                                            .append("Server/Product/new_product_images/")
                                            .append(response.body().toString())
                                            .toString();

                                    Log.d("ImagePath" , uploaded_img_path);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(UpdateProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_CODE){
            if (resultCode == RESULT_OK){
                if (data != null){

                    selected_img_uri = data.getData();
                    if (selected_img_uri != null && !selected_img_uri.getPath().isEmpty()){

                        img_browser.setImageURI(selected_img_uri);
                        uploadFileToServer();
                    }
                    else
                        Toast.makeText(this, "Can't upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onProgressUpdate(int pertantage) {

    }
}
