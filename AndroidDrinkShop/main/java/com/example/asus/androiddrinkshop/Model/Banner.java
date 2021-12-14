package com.example.asus.androiddrinkshop.Model;

public class Banner {

    private String ID , Name , Link;

    public Banner() {
    }

    public Banner(String ID, String name, String link) {
        this.ID = ID;
        Name = name;
        Link = link;
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
}
