package com.example.communicateapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyApplication extends Application {
    public String UserId;
    public String UserName;
    public Bitmap HeadPhoto;
    public Bitmap TalkerHeadPhoto;
    public int ChatTextColor;
    public int ChatBorderColor;
    public int ChatBackgroundColor;

    public String ip = "http://47.94.252.50:8080/xbxServe/";
//public String ip = "http://10.0.2.2:8080/xbxServe/";
//    public Context context;

    public static MyApplication myApplicationInstance =null;
    public static MyApplication getMyApplicationInstance(){
        return myApplicationInstance;
    }
    public MyApplication(){
        myApplicationInstance = this;
    }
    public String getNowTime(){
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = dff.format(new Date());
        Log.d("MyApplication",ee);
        return ee;
    }

    public String colorToString(int color) {
        String s = "#";
        int colorStr = (color & 0xff000000) | (color & 0x00ff0000) | (color & 0x0000ff00) | (color & 0x000000ff);
        s = s + Integer.toHexString(colorStr);
        return s;
    }

}
