package com.example.shiro.netCore;

/**
 * Created by shiro on 26.10.17.
 */

public class JSONEntity {


    private int r;
    private int g;
    private int b;



    public  JSONEntity()
    {

    }

    public JSONEntity ( int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public  void setJSON ( int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR ()
    {
        return this.r;
    }

    public int getG ()
    {
        return this.g;
    }

    public int getB ()
    {
        return this.b;
    }


}
