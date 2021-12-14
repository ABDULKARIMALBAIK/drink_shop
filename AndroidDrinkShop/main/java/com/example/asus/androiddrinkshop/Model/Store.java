package com.example.asus.androiddrinkshop.Model;

public class Store {

    private int id;
    private String name;
    private double lat , lng , distance_in_km;

    public Store() {
    }

    public Store(int id, String name, double lat, double lng, double distance_in_km) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.distance_in_km = distance_in_km;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistance_in_km() {
        return distance_in_km;
    }

    public void setDistance_in_km(double distance_in_km) {
        this.distance_in_km = distance_in_km;
    }
}
