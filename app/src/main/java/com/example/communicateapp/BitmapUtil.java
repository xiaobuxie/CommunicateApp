package com.example.communicateapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {
    public static BitmapUtil bitmapUtilInstance;
    public static BitmapUtil getBitmapUtilInstance(){
        if( bitmapUtilInstance == null ){
            bitmapUtilInstance = new BitmapUtil();
        }
        return bitmapUtilInstance;
    }
    public Bitmap StringToBitmap(String string){
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        } catch (Exception e){
            e.printStackTrace();
        }
//        return imageScale(bitmap,200,200);
        return bitmap;
    }


    public String BitmapToString(Bitmap bitmap){
        String string = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(bitmap == null){
            Log.d("BitmapUtil","bitmap is null");
        }
        if( byteArrayOutputStream == null){
            Log.d("BitmapUtil","byteArray is null");
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        string = Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

//    public Bitmap imageScale(Bitmap bitmap,int dst_w,int dst_h){
//        int src_w = bitmap.getWidth();
//        int src_h = bitmap.getHeight();
//        float scale_w = ( (float)dst_w ) / src_w;
//        float scale_h = ( (float)dst_h ) / src_h;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale_w,scale_h);
//        Bitmap dstbmp = Bitmap.createBitmap(bitmap,0,0,src_w,src_h,matrix,true);
//        return dstbmp;
//    }
}
