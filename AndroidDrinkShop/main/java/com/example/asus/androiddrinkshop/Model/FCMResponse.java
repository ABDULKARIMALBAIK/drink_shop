package com.example.asus.androiddrinkshop.Model;

import java.util.List;

public class FCMResponse {

    public long multicast_id;
    public int success , failure , canonical_ids;
    public List<FCMResult> results;
}
