package com.example.asus.androiddrinkshop.Database.DataSource;

import com.example.asus.androiddrinkshop.Database.ModelDB.Cart;

import java.util.List;

import io.reactivex.Flowable;

public interface ICartDataSource {

    Flowable<List<Cart>> getCartItems();

    Flowable<List<Cart>> getCartItemById(int cartItemId);

    int countCartItems();

    float sumPrice();

    void emptyCart();

    void insertToCart(Cart... carts);

    void updateToCart(Cart... carts);

    void deleteCartItem(Cart cart);
}
