package com.example.shiro.netCore;

import android.util.Base64;

import com.google.gson.Gson;


/**
 * Created by shiro on 16.10.17.
 */

public  class Base64Converter


{

    String string;
    Gson gson = new Gson();

    public static byte [] encode (String data)
    {
        byte[] encodedBytes = data.getBytes();
        return encodedBytes;
    }

    public  String encodeRGB (JSONEntity data)
    {
        string = gson.toJson(data);
       return string;
    }
}
