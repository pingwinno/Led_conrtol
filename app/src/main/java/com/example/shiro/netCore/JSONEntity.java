package com.example.shiro.netCore;

import com.google.gson.Gson;

/**
 * Created by shiro on 26.10.17.
 */

public class JSONEntity {


    private int ledNumber;
    private int r;
    private int g;
    private int b;

    public  void setJSON ( int ledNumber, int r, int g, int b)
    {
        this.ledNumber = ledNumber;
        this.r = r;
        this.g = g;
        this.b = b;
    }







}
