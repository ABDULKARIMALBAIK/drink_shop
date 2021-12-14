package com.example.asus.androiddrinkshop;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.asus.androiddrinkshop.Adapter.CategoryAdapter;
import com.example.asus.androiddrinkshop.Database.DataSource.CartRepository;
import com.example.asus.androiddrinkshop.Database.DataSource.FavoriteRepository;
import com.example.asus.androiddrinkshop.Database.Local.ABDRoomDatabase;
import com.example.asus.androiddrinkshop.Database.Local.CartDataSource;
import com.example.asus.androiddrinkshop.Database.Local.FavoriteDataSource;
import com.example.asus.androiddrinkshop.Model.Banner;
import com.example.asus.androiddrinkshop.Model.Category;
import com.example.asus.androiddrinkshop.Model.Drink;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Services.MyFirebaseIdService;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.Utils.ProgressRequestBody;
import com.example.asus.androiddrinkshop.Interface.UploadCallBack;
import com.facebook.accountkit.AccountKit;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , UploadCallBack {

    private static final int REQUEST_PERMISSION_CODE = 10001;
    private static final int PICK_FILE_REQUEST = 10002;
    TextView txt_name , txt_phone;
    SliderLayout sliderLayout;
    RecyclerView lst_menu;
    ImageView cart_icon;
    CircleImageView img_avatar;
    SwipeRefreshLayout swipeRefreshLayout;

    Uri selectedFileUri;

    NotificationBadge badge;

    IDrinkShopAPI mService;

    //Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mService = Common.getAPI();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swip_to_refresh);

        lst_menu = (RecyclerView)findViewById(R.id.lst_menu);
        lst_menu.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
        lst_menu.setHasFixedSize(true);


        sliderLayout = (SliderLayout)findViewById(R.id.slider);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Welcome in DrinkShop app !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txt_name = (TextView)headerView.findViewById(R.id.txt_name);
        txt_phone = (TextView)headerView.findViewById(R.id.txt_phone);
        img_avatar = (CircleImageView)headerView.findViewById(R.id.img_avatar);

        //set information
        txt_name.setText(Common.currentUser.getName());
        txt_phone.setText(Common.currentUser.getPhone());

        //Click img_avatar
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(HomeActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= 23)
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , REQUEST_PERMISSION_CODE);
                }
                else
                    chooseImage();
            }
        });

        //Set avatar
        if (!TextUtils.isEmpty(Common.currentUser.getAvatarUrl())){

            Picasso.with(this)
                    .load(new StringBuilder(Common.BASE_URL)
                        .append("user_avatar/").append(Common.currentUser.getAvatarUrl()).toString())
                    .into(img_avatar);
        }

        //Event Swip
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                //get Banners
                getBannerImage();

                //get Menu
                getMenu();

                //Save newest Topping List
                getToppingList();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);

                //Fix error adding more items where every time refreshing
                sliderLayout = new SliderLayout(HomeActivity.this);

                //get Banners
                getBannerImage();

                //get Menu
                getMenu();

                //Save newest Topping List
                getToppingList();
            }
        });

        //Init Database
        initDB();

        //Update user's token to receive and send notifications
        updateTokenToFirebase();
    }


    //Exit Application when click Back button
    boolean isBackButtonDoubleClicked = false;
    //ctrl + O

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (isBackButtonDoubleClicked){

                super.onBackPressed();
                return;
            }

            this.isBackButtonDoubleClicked = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);

        View view = menu.findItem(R.id.cart_menu).getActionView();
        badge = (NotificationBadge)view.findViewById(R.id.badge);

        cart_icon = (ImageView)view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this , CartActivity.class));

            }
        });

        updateCartCount();

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_PERMISSION_CODE:{

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    chooseImage();
                else
                    Toast.makeText(this, "you can't use read galary !!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST){
            if (resultCode == RESULT_OK){
                if (data != null){

                    selectedFileUri = data.getData();
                    if (selectedFileUri != null && !selectedFileUri.getPath().isEmpty()){

                        img_avatar.setImageURI(selectedFileUri);
                        uploadFile();
                    }
                    else
                        Toast.makeText(this, "you can't upload file to server !!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        }
        else if (id == R.id.search_menu) {
            startActivity(new Intent(HomeActivity.this , SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {

            //Create Confirm Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Exit Application")
                    .setMessage("Do you want to exit this application ?");

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AccountKit.logOut();

                    Intent intent = new Intent(HomeActivity.this , MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        }
        else if(id == R.id.nav_favorite){

            startActivity(new Intent(HomeActivity.this , FavoriteListActivity.class));
        }
        else if(id == R.id.nav_show_orders){

            startActivity(new Intent(HomeActivity.this , ShowOrderActivity.class));
        }
        else if(id == R.id.nav_nearby_store){

            startActivity(new Intent(HomeActivity.this , NearbyStore.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {

        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sliderLayout.startAutoCycle();
        updateCartCount();
        this.isBackButtonDoubleClicked = false;
    }


    private void updateTokenToFirebase() {

        IDrinkShopAPI mService = Common.getAPI();
        mService.updateToken(Common.currentUser.getPhone(),
                FirebaseInstanceId.getInstance().getToken(),
                "0")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.d("DEBUGS" , response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("DEBUG" , t.getMessage());
                    }
                });
    }

    private void uploadFile() {

        if (selectedFileUri != null){

            File file = FileUtils.getFile(this , selectedFileUri);  //avatar image

            String fileName = new StringBuilder(Common.currentUser.getPhone())   //avatar name (mix phone and extension of avatar)
                    .append(FileUtils.getExtension(file.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file , this);

            final MultipartBody.Part body =  MultipartBody.Part.createFormData("uploaded_file" , fileName , requestFile);

            final MultipartBody.Part userPhone =  MultipartBody.Part.createFormData("phone" , Common.currentUser.getPhone());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    mService.uploadFile(userPhone , body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    Toast.makeText(HomeActivity.this, response.body(), Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {

        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Picture"),
                PICK_FILE_REQUEST);
    }

    private void initDB() {

        Common.abdRoomDatabase = ABDRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.abdRoomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDataSource.getInstance(Common.abdRoomDatabase.favoriteDAO()));
    }

    private void getToppingList() {

        compositeDisposable.add(
                mService.getDrink(Common.TOPPING_MENU_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Drink>>() {
                            @Override
                            public void accept(List<Drink> drinks) throws Exception {

                                Common.toppingList = drinks;
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(HomeActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    private void updateCartCount() {

        if (badge == null)
            return;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (Common.cartRepository.countCartItems() == 0)
                    badge.setVisibility(View.INVISIBLE);
                else {

                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.cartRepository.countCartItems()));
                }
            }
        });
    }

    public void getBannerImage() {

        compositeDisposable.add(
                mService.getBanners()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Banner>>() {
                            @Override
                            public void accept(List<Banner> banners) throws Exception {
                                dislayImage(banners);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(HomeActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    private void dislayImage(List<Banner> banners) {

        Map<String , String> bannerMap = new HashMap<>();
        for (Banner item : banners)
           bannerMap.put(item.getName() , item.getLink());

        for (String name : bannerMap.keySet()){

            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }
    }

    private void getMenu() {

        compositeDisposable.add(
                mService.getMenu()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Category>>() {
                            @Override
                            public void accept(List<Category> categories) throws Exception {
                                displayMenu(categories);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(HomeActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    private void displayMenu(List<Category> categories) {

        CategoryAdapter adapter = new CategoryAdapter(this , categories);
        lst_menu.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onProgressUpdate(int pertantage) {

    }

}
