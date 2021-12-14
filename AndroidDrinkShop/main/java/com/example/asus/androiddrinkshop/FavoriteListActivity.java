package com.example.asus.androiddrinkshop;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.asus.androiddrinkshop.Adapter.FavoriteAdapter;
import com.example.asus.androiddrinkshop.Database.ModelDB.Favorite;
import com.example.asus.androiddrinkshop.Interface.RecyclerItemTouchHelperListener;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.example.asus.androiddrinkshop.Utils.RecyclerItemTouchHelper;
import com.example.asus.androiddrinkshop.ViewHolder.FavoriteViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoriteListActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    RecyclerView recycler_fav;
    CompositeDisposable compositeDisposable;
    RelativeLayout rootLayout;

    FavoriteAdapter adapter;
    List<Favorite> localFavorites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        compositeDisposable = new CompositeDisposable();

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);

        recycler_fav = (RecyclerView)findViewById(R.id.recycler_fav);
        recycler_fav.setHasFixedSize(true);
        recycler_fav.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0 , ItemTouchHelper.LEFT , FavoriteListActivity.this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_fav);

        loadFavoritesItem();

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFavoritesItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }

    private void loadFavoritesItem() {

        compositeDisposable.add(Common.favoriteRepository.getFavItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<Favorite>>() {
                @Override
                public void accept(List<Favorite> favorites) throws Exception {

                    displayFavoritesItem(favorites);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(FavoriteListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
    }

    private void displayFavoritesItem(List<Favorite> favorites) {

        localFavorites = favorites;
        adapter = new FavoriteAdapter(this , favorites);
        recycler_fav.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof FavoriteViewHolder) {

            String name = localFavorites.get(viewHolder.getAdapterPosition()).name;

            final Favorite deleteItem = localFavorites.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            //Delete Item from adapter
            adapter.removeItem(deleteIndex);
            //Delete Item from Room database
            Common.favoriteRepository.delete(deleteItem);

            //Make Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + " is removed from favorites !", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapter.restoreItem(deleteItem, deleteIndex);
                    Common.favoriteRepository.insertFav(deleteItem);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
