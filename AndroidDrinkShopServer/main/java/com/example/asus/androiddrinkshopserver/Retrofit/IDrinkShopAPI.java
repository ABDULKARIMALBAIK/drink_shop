package com.example.asus.androiddrinkshopserver.Retrofit;

import com.example.asus.androiddrinkshopserver.Model.Category;
import com.example.asus.androiddrinkshopserver.Model.Drink;
import com.example.asus.androiddrinkshopserver.Model.Order;
import com.example.asus.androiddrinkshopserver.Model.Token;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDrinkShopAPI {

    /*
    Category Management
    * */
    @GET("getmenu.php")
    Observable<List<Category>> getMenu();

    @FormUrlEncoded
    @POST("Server/Category/add_category.php")
    Observable<String> addNewCategory(@Field("name") String name , @Field("imgPath") String imgPath);

    @Multipart
    @POST("Server/Category/upload_category_img.php")
    Call<String> uploadCategoryFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("Server/Category/update_category.php")
    Observable<String> updateCategory(@Field("id") String id,
                                      @Field("name") String name,
                                      @Field("imgPath") String imgPath);

    @FormUrlEncoded
    @POST("Server/Category/delete_category.php")
    Observable<String> deleteCategory(@Field("id") String id);

    /*
    Drink Management
    * */
    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuID);

    @FormUrlEncoded
    @POST("Server/Product/add_Product.php")
    Observable<String> addNewProduct(@Field("name") String name,
                                   @Field("imgPath") String imgPath,
                                   @Field("price") String price,
                                   @Field("menuId") String menuId);

    @Multipart
    @POST("Server/Product/upload_product_img.php")
    Call<String> uploadProductFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("Server/Product/update_Product.php")
    Observable<String> updateProduct(@Field("id") String id,
                                      @Field("name") String name,
                                      @Field("imgPath") String imgPath,
                                      @Field("Price") String price,
                                      @Field("menuId") String menuId);

    @FormUrlEncoded
    @POST("Server/Product/delete_Product.php")
    Observable<String> deleteProduct(@Field("id") String id);

    /*
     Order Management
     * */
    @FormUrlEncoded
    @POST("Server/Order/getorderserver.php")
    Observable<List<Order>> getAllOrder(@Field("status") String status);

    @FormUrlEncoded
    @POST("Server/Order/update_order_status.php")
    Observable<String> updateOrderStatus(@Field("phone") String phone,
                                         @Field("order_id") long orderId,
                                         @Field("status") int status );

    /*
    * Update Token
    * */
    @FormUrlEncoded
    @POST("updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") String isServerToken);

}
