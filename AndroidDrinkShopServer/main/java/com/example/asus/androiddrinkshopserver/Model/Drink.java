package com.example.asus.androiddrinkshopserver.Model;

public class Drink {

    private String ID;
    private String Name;
    private String Link;
    private String MenuId;
    private String Price;

    public Drink() {
    }

    public Drink(String ID, String name, String link, String menuId, String price) {
        this.ID = ID;
        Name = name;
        Link = link;
        MenuId = menuId;
        Price = price;
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

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
