package com.example.asus.androiddrinkshopserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.androiddrinkshopserver.Adapter.DrinkAdapter;
import com.example.asus.androiddrinkshopserver.Interface.UploadCallBack;
import com.example.asus.androiddrinkshopserver.Model.Drink;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.Utils.ProgressRequestBody;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrinkListActivity extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_IMAGE_CODE = 7777;
    IDrinkShopAPI mService;
    RecyclerView recycler_drinks;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    FloatingActionButton btn_add;
    ImageView img_browser;
    EditText edt_drink_name , edt_drink_price;

    Uri uri_image_selected = null;
    String upload_img_path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_list);

        mService = Common.getAPI();

        btn_add = (FloatingActionButton)findViewById(R.id.btn_add_product);

        recycler_drinks = (RecyclerView)findViewById(R.id.recycler_drinks);
        recycler_drinks.setHasFixedSize(true);
        recycler_drinks.setLayoutManager(new GridLayoutManager(this , 2));

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddDrinkDialog();
            }
        });

        loadListDrink(Common.currentCategory.getID());

    }

    private void showAddDrinkDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Select New Product");

        View view = getLayoutInflater().inflate(R.layout.add_product_layout , null);
        edt_drink_name = (EditText)view.findViewById(R.id.edt_drink_name);
        edt_drink_price = (EditText)view.findViewById(R.id.edt_drink_price);
        img_browser = (ImageView)view.findViewById(R.id.img_drink);

        img_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Image") , PICK_IMAGE_CODE);
            }
        });

        builder.setView(view);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                uri_image_selected = null;
                upload_img_path = "";
            }
        });
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (edt_drink_name.getText().toString().isEmpty()){

                    Toast.makeText(DrinkListActivity.this, "Please enter name of product", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_drink_price.getText().toString().isEmpty()){

                    Toast.makeText(DrinkListActivity.this, "Please enter price of product", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (upload_img_path.isEmpty()){

                    Toast.makeText(DrinkListActivity.this, "Please select image of product", Toast.LENGTH_SHORT).show();
                    return;
                }

                compositeDisposable.add(mService.addNewProduct(
                        edt_drink_name.getText().toString(),
                        upload_img_path,
                        edt_drink_price.getText().toString(),
                        Common.currentCategory.getID()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        Toast.makeText(DrinkListActivity.this, s, Toast.LENGTH_SHORT).show();
                        loadListDrink(Common.currentCategory.getID());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(DrinkListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

            }
        });

        builder.show();
    }

    private void loadListDrink(String id) {

        compositeDisposable.add(mService.getDrink(id)
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
                Toast.makeText(DrinkListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void displayDrinkList(List<Drink> drinks) {

        DrinkAdapter adapter = new DrinkAdapter(this , drinks);
        recycler_drinks.setAdapter(adapter);
    }

    private void uploadFileToServer() {

        if (uri_image_selected != null){

            File image = FileUtils.getFile(this , uri_image_selected);
            String imageName =  new StringBuilder(UUID.randomUUID().toString())
                    .append(FileUtils.getExtension(image.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(image , this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" , imageName , requestFile);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    mService.uploadProductFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    //After upload , we will get file name and return String contain link of image
                                    upload_img_path = new StringBuilder(Common.BASE_URL)
                                            .append("Server/Product/new_product_images/")
                                            .append(response.body().toString())
                                            .toString();

                                    Log.d("ImagePath" , upload_img_path);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(DrinkListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_CODE){
            if (resultCode == RESULT_OK){
                if (data != null){

                    uri_image_selected = data.getData();
                    if (uri_image_selected != null && !uri_image_selected.getPath().isEmpty()){

                        img_browser.setImageURI(uri_image_selected);
                        uploadFileToServer();
                    }
                    else
                        Toast.makeText(this, "Can't upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadListDrink(Common.currentCategory.getID());
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
    public void onProgressUpdate(int pertantage) {

    }
}
