package com.example.asus.androiddrinkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.asus.androiddrinkshop.Adapter.DrinkAdapter;
import com.example.asus.androiddrinkshop.Model.Drink;
import com.example.asus.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.asus.androiddrinkshop.Utils.Common;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    IDrinkShopAPI mService;

    MaterialSearchBar searchBar;
    RecyclerView recycler_search;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DrinkAdapter searchAdapter , adapter;
    List<String> suggestList = new ArrayList<>();
    List<Drink> localDataSource = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mService = Common.getAPI();

        recycler_search = (RecyclerView)findViewById(R.id.recycler_search);
        recycler_search.setLayoutManager(new GridLayoutManager(this , 2));

        searchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        searchBar.setHint("Enter your Drink...");

        loadAllDrinks();

        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest  =new ArrayList<>();

                for (String search : suggestList){

                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);

                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (!enabled)
                    recycler_search.setAdapter(adapter);  //Restore full list of drink
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }



    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    private void loadAllDrinks() {

        compositeDisposable.add(mService.getAllDrinks()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<Drink>>() {
                                    @Override
                                    public void accept(List<Drink> drinks) throws Exception {

                                        displayListDrink(drinks);
                                        buildSuggestList(drinks);

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(SearchActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
    }

    private void startSearch(CharSequence text) {

        List<Drink> result = new ArrayList<>();

        for (Drink drink : localDataSource)
            if (drink.getName().toLowerCase().contains(String.valueOf(text).toLowerCase()))
                result.add(drink);

        searchAdapter = new DrinkAdapter(this , result);
        recycler_search.setAdapter(searchAdapter);

    }

    private void buildSuggestList(List<Drink> drinks) {

        for (Drink drink : drinks)
            suggestList.add(drink.Name);

        searchBar.setLastSuggestions(suggestList);
    }

    private void displayListDrink(List<Drink> drinks) {

        localDataSource = drinks;
        adapter = new DrinkAdapter(this , drinks);
        recycler_search.setAdapter(adapter);

    }
}
