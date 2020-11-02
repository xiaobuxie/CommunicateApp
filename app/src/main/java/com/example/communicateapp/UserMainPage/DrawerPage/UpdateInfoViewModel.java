package com.example.communicateapp.UserMainPage.DrawerPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.communicateapp.BitmapUtil;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;
import com.example.communicateapp.database.UserDatabase;
import com.example.communicateapp.database.UserDatabaseDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateInfoViewModel extends ViewModel {
    public static final int CHANGE_OK = 1;
    public static final int CHANGE_TOO_LAEGE = -1;
    public static final int CHANGE_NOT_OK = 0;

    // 头像限制
    private final long  HEAD_PHOTO_LIMIT = 4294967295L;
    // info
    Context context;
    String userId;

    MutableLiveData<Integer> ChangeHeadPhotoSingal;
    MutableLiveData<Integer> ChangeTextStyleSingal;

    public void setInfo(Context context,String userid) {
        this.context = context;
        userId = userid;
    }

    public MutableLiveData<Integer> getChangeHeadPhotoSingal() {
        if( ChangeHeadPhotoSingal == null){
            ChangeHeadPhotoSingal = new MutableLiveData<>();
        }
        return ChangeHeadPhotoSingal;
    }

    public MutableLiveData<Integer> getChangeTextStyleSingal() {
        if( ChangeTextStyleSingal == null ){
            ChangeTextStyleSingal = new MutableLiveData<>();
        }
        return ChangeTextStyleSingal;
    }

    // 更改聊天气泡
    public void changeTextStyle(final String textStyle){
        Log.d("UpdateInfoViewModel","changeTextStyle: " + textStyle);
        String url = MyApplication.getMyApplicationInstance().ip + "UpdateTextStyle";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("UserId",userId)
                                                        .add("textStyle",textStyle).build();
        Request request = new okhttp3.Request.Builder().url(url).post(requestBody).build();
        Log.d("UpdateInfoViewModel","text style url:" + url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChangeTextStyleSingal.postValue(CHANGE_NOT_OK);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    // 先修改本地
                    try {
                        JSONObject textStyleJson = new JSONObject(textStyle);
                        MyApplication.getMyApplicationInstance().ChatTextColor = Color.parseColor(textStyleJson.getString("ChatTextColor"));
                        MyApplication.getMyApplicationInstance().ChatBackgroundColor = Color.parseColor(textStyleJson.getString("ChatBackgroundColor"));
                        MyApplication.getMyApplicationInstance().ChatBorderColor = Color.parseColor(textStyleJson.getString("ChatBorderColor"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ChangeTextStyleSingal.postValue(CHANGE_OK);
                }
                else ChangeTextStyleSingal.postValue(CHANGE_NOT_OK);
            }
        });
    }
    // 更改自己的头像
    public void changeHeadPhoto(final Bitmap bitmap){
        final String bitmapString = BitmapUtil.getBitmapUtilInstance().BitmapToString(bitmap);
        Log.d("UpdateInfoViewModel","bitmap len:" + bitmapString.length());
        Log.d("UpdateInfoViewModel","userid: " + userId);
        // 假如太大直接返回
        Integer needBytes = bitmapString.length() * 2;
        if( needBytes > HEAD_PHOTO_LIMIT ){
            ChangeHeadPhotoSingal.postValue(CHANGE_TOO_LAEGE);
            return;
        }

        // 更新到服务器上
        String url = MyApplication.getMyApplicationInstance().ip + "UpdateHeadPhoto";
        OkHttpClient client = new OkHttpClient();
        RequestBody  requestBody = new FormBody.Builder().add("UserId",userId)
                                                        .add("BitmapString",bitmapString).build();
        Request request = new okhttp3.Request.Builder().url(url).post(requestBody).build();
        Log.d("UpdateInfoViewModel","ready to send to serve");
        Log.d("UpdateInfoViewModel","head photo url:" + url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if( response.isSuccessful()){
                    ChangeHeadPhotoSingal.postValue(CHANGE_OK);
                    //可以就更新全局
                    MyApplication.getMyApplicationInstance().HeadPhoto = bitmap;
                }
                else ChangeHeadPhotoSingal.postValue(CHANGE_NOT_OK);
            }
        });
    }
}
