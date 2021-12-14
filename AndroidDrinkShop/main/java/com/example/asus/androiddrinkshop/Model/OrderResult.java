package com.example.asus.androiddrinkshop.Model;

public class OrderResult {

    private int OrderId;
    private String OrderDate;
    private int OrderStatus;
    private float OrderPrice;
    private String OrderDetail , OrderComment , OrderAddress , UserPhone;

    public OrderResult() {
    }

    public OrderResult(int orderId, String orderDate, int orderStatus, float orderPrice, String orderDetail, String orderComment, String orderAddress, String userPhone) {
        OrderId = orderId;
        OrderDate = orderDate;
        OrderStatus = orderStatus;
        OrderPrice = orderPrice;
        OrderDetail = orderDetail;
        OrderComment = orderComment;
        OrderAddress = orderAddress;
        UserPhone = userPhone;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public float getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        OrderPrice = orderPrice;
    }

    public String getOrderDetail() {
        return OrderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        OrderDetail = orderDetail;
    }

    public String getOrderComment() {
        return OrderComment;
    }

    public void setOrderComment(String orderComment) {
        OrderComment = orderComment;
    }

    public String getOrderAddress() {
        return OrderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        OrderAddress = orderAddress;
    }

    public String getOrderPhone() {
        return UserPhone;
    }

    public void setOrderPhone(String orderPhone) {
        UserPhone = orderPhone;
    }
}
