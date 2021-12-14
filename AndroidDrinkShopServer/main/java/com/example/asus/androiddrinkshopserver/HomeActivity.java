package com.example.asus.androiddrinkshopserver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.androiddrinkshopserver.Adapter.MenuAdapter;
import com.example.asus.androiddrinkshopserver.Interface.UploadCallBack;
import com.example.asus.androiddrinkshopserver.Model.Category;
import com.example.asus.androiddrinkshopserver.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshopserver.Utils.Common;
import com.example.asus.androiddrinkshopserver.Utils.ProgressRequestBody;
import com.google.firebase.iid.FirebaseInstanceId;
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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , UploadCallBack{

    private static final int REQUEST_PERMISSION_CODE = 1111;
    private static final int PICK_IMAGE_CODE = 2222;
    RecyclerView recyclerVie_menu;
    CompositeDisposable compositeDisposable;
    IDrinkShopAPI mService;

    EditText edt_name;
    ImageView img_browser;

    Uri uri_image_selected = null;
    String upload_img_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(HomeActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    if (Build.VERSION.SDK_INT >= 23)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , REQUEST_PERMISSION_CODE);
                }
                else {

                    showAddCategoryDialog();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerVie_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recyclerVie_menu.setHasFixedSize(true);
        recyclerVie_menu.setLayoutManager(new GridLayoutManager(this , 2));

        mService = Common.getAPI();
        compositeDisposable = new CompositeDisposable();

        //get Menu
        getMenu();

        //update server's token
        updateTokenToFirebase();
    }

    private void showAddCategoryDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Select New Category");

        View view = getLayoutInflater().inflate(R.layout.add_category_layout , null);
        edt_name = (EditText)view.findViewById(R.id.edt_name);
        img_browser = (ImageView)view.findViewById(R.id.img_category);

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
                
                if (edt_name.getText().toString().isEmpty()){

                    Toast.makeText(HomeActivity.this, "Please enter name of category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (upload_img_path.isEmpty()){

                    Toast.makeText(HomeActivity.this, "Please select image of category", Toast.LENGTH_SHORT).show();
                    return;
                }

                compositeDisposable.add(mService.addNewCategory(edt_name.getText().toString() , upload_img_path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        Toast.makeText(HomeActivity.this, s, Toast.LENGTH_SHORT).show();
                        upload_img_path = "";
                        uri_image_selected = null;

                        //Refresh the date
                        getMenu();
                    }
                },
                         new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HomeActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
            }
        });

        builder.show();

    }

    private void getMenu() {

        compositeDisposable.add(mService.getMenu()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<Category>>() {
                                    @Override
                                    public void accept(List<Category> categories) throws Exception {

                                        displayMenuList(categories);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {

                                    }
                                }));
    }

    private void displayMenuList(List<Category> categories) {

        Common.menuList = categories;

        MenuAdapter adapter = new MenuAdapter(this , categories);
        recyclerVie_menu.setAdapter(adapter);
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

                    mService.uploadCategoryFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    
                                    //After upload , we will get file name and return String contain link of image
                                    upload_img_path = new StringBuilder(Common.BASE_URL)
                                            .append("Server/Category/new_category_images/")
                                            .append(response.body().toString())
                                            .toString();

                                    Log.d("ImagePath" , upload_img_path);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }

    private void updateTokenToFirebase() {

        IDrinkShopAPI mService = Common.getAPI();
        mService.updateToken("server_app_01",
                FirebaseInstanceId.getInstance().getToken(),
                "1")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.d("DEBUG" , response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("DEBUG" , t.getMessage());
                    }
                });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        switch (requestCode){
            
            case REQUEST_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    showAddCategoryDialog();
                }
                else
                    Toast.makeText(this, "You can't access to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        getMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_show_order) {

            startActivity(new Intent(HomeActivity.this , ShowOrderActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //////////When upload image of category
    @Override
    public void onProgressUpdate(int pertantage) {

    }
}
