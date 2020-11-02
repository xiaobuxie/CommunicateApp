package com.example.communicateapp.RegistPage;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.communicateapp.MyApplication;

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

public class RegistViewModel extends ViewModel {
    public final static int ERROR = -2;
    public final static int ALREADY_EXIST = -1;
    public final static int SUCCESSFUL = 1;
    MutableLiveData<Integer> singal;


    public MutableLiveData<Integer> getSingal() {
        if( singal == null ) singal = new MutableLiveData<>();
        return singal;
    }

    // about data
    public void addAccount(final String userId,final String userPassword,final String bitmapStr ,final String username,final String textStyle){
        if(userId == null ) return;
        String url = MyApplication.getMyApplicationInstance().ip +  "RegisterServlet";
        OkHttpClient client = new OkHttpClient();
        // 创建requestBody
        Log.d("RegistViewModel","textStyle: " + textStyle);
        RequestBody requestBody = new FormBody.Builder()
                .add("account",userId)
                .add("password",userPassword)
                .add("headPhoto",bitmapStr)
                .add("username",username)
                .add("textStyle",textStyle)
                .build();
        Request request = new okhttp3.Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                singal.postValue(ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String message = response.body().string();
                    String res = "201";
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        res = jsonObject.get("resCode").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if( res.equals("201")){
                        singal.postValue(ALREADY_EXIST);
                    }
                    else singal.postValue(SUCCESSFUL);
                }
            }
        });
    }
}
