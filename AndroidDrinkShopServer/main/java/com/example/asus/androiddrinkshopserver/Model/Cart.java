package com.example.asus.androiddrinkshopserver.Model;

public class Cart {

    private int id , amount , size;
    private String name , link , toppingExtras;
    private double price;
    private short ice , sugar;

    public Cart() {
    }

    public Cart(int id, int amount, int size, String name, String link, String toppingExtras, double price, short ice, short sugar) {
        this.id = id;
        this.amount = amount;
        this.size = size;
        this.name = name;
        this.link = link;
        this.toppingExtras = toppingExtras;
        this.price = price;
        this.ice = ice;
        this.sugar = sugar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getToppingExtras() {
        return toppingExtras;
    }

    public void setToppingExtras(String toppingExtras) {
        this.toppingExtras = toppingExtras;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public short getIce() {
        return ice;
    }

    public void setIce(short ice) {
        this.ice = ice;
    }

    public short getSugar() {
        return sugar;
    }

    public void setSugar(short sugar) {
        this.sugar = sugar;
    }
}
