package com.example.asus.androiddrinkshopserver;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.androiddrinkshopserver.Interface.UploadCallBack;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.Utils.ProgressRequestBody;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCategoryActivity extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_IMAGE_CODE = 7777;
    ImageView img_browser;
    EditText edt_name;
    Button btn_update , btn_delete;

    IDrinkShopAPI mService;
    CompositeDisposable compositeDisposable;

    Uri selected_img_uri = null;
    String uploaded_img_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        //init views
        edt_name = (EditText)findViewById(R.id.edt_name);
        img_browser = (ImageView)findViewById(R.id.img_category);
        btn_update = (Button)findViewById(R.id.btn_update);
        btn_delete = (Button)findViewById(R.id.btn_delete);

        //Init API
        mService = Common.getAPI();
        //Rxjava
        compositeDisposable = new CompositeDisposable();

        img_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Image") , PICK_IMAGE_CODE);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateCategory();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory();
            }
        });

        displayData();
    }

    private void deleteCategory() {

        compositeDisposable.add(mService.deleteCategory(Common.currentCategory.getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        Toast.makeText(UpdateCategoryActivity.this, s, Toast.LENGTH_SHORT).show();
                        uploaded_img_path = "";
                        selected_img_uri = null;
                        Common.currentCategory = null;
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(UpdateCategoryActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void updateCategory() {

        if (!edt_name.getText().toString().isEmpty()){

            compositeDisposable.add(mService.updateCategory(Common.currentCategory.getID() , edt_name.getText().toString() , uploaded_img_path)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {

                            Toast.makeText(UpdateCategoryActivity.this, s, Toast.LENGTH_SHORT).show();
                            uploaded_img_path = "";
                            selected_img_uri = null;
                            Common.currentCategory = null;
                            finish();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(UpdateCategoryActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
            
        }
        else
            Toast.makeText(this, "Please enter name of category", Toast.LENGTH_SHORT).show();
       
    }

    private void displayData() {

        if (Common.currentCategory != null){

            Picasso.with(this)
                    .load(Common.currentCategory.getLink())
                    .into(img_browser);

            edt_name.setText(Common.currentCategory.getName());
            uploaded_img_path = Common.currentCategory.getLink();
        }
    }

    private void uploadFileToServer() {

        if (selected_img_uri != null){

            File image = FileUtils.getFile(this , selected_img_uri);
            String imageName =  new StringBuilder(UUID.randomUUID().toString())
                    .append(FileUtils.getExtension(image.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(image , UpdateCategoryActivity.this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" , imageName , requestFile);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    mService.uploadCategoryFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    //After upload , we will get file name and return String contain link of image
                                    uploaded_img_path = new StringBuilder(Common.BASE_URL)
                                            .append("Server/Category/new_category_images/")
                                            .append(response.body().toString())
                                            .toString();

                                    Log.d("ImagePath" , uploaded_img_path);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(UpdateCategoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
