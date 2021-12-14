package com.example.asus.androiddrinkshop.Model;

public class Drink {

    public String ID;
    public String Name;
    public String Link;
    public String Price;
    public String MenuId;

    public Drink() {
    }

    public Drink(String ID, String name, String link, String price, String menuId) {
        this.ID = ID;
        Name = name;
        Link = link;
        Price = price;
        MenuId = menuId;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
