package com.example.communicateapp.LoginPage;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

public class LoginViewModel extends ViewModel{
    public final static int ERROR = -1;
    public final static int PASSWORD_ERROR = -2;
    public final static int NOT_EXIST = -3;
    public final static int SUCCESSFUL = 1;
    private Context context;

    public void setInfo(Context context){
        this.context = context;
    }
    // livedata
    private MutableLiveData<Integer> signal;
    // livedata getter
    public MutableLiveData<Integer> getSignal() {
        if( signal == null){
            signal = new MutableLiveData<>();
        }
        return signal;
    }

    public void loginAccount(final  String userId,final  String userPassword){

        String url = MyApplication.getMyApplicationInstance().ip + "LoginSrevlet";
        Log.d("LoginViewModel",url);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("account",userId)
                .add("password",userPassword).build();
        Request request = new okhttp3.Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                signal.postValue(ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if( response.isSuccessful()){
                    String message = response.body().string();
                    String resCode = "201";
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        resCode = jsonObject.get("resCode").toString();
                        if( "100".equals(resCode)){
                            String username = jsonObject.getString("username");
                            String headPhotoStr = jsonObject.getString("headPhoto");
                            MyApplication.getMyApplicationInstance().UserName = username;
                            MyApplication.getMyApplicationInstance().HeadPhoto = BitmapUtil.getBitmapUtilInstance().StringToBitmap(headPhotoStr);

                            String textStyleJsonStr = jsonObject.getString("textStyle");
                            Log.d("LoginViewModel","text style: " + textStyleJsonStr);
                            JSONObject textStyleJson = new JSONObject(textStyleJsonStr);
                            MyApplication.getMyApplicationInstance().ChatTextColor = Color.parseColor(textStyleJson.getString("ChatTextColor"));
                            MyApplication.getMyApplicationInstance().ChatBackgroundColor = Color.parseColor(textStyleJson.getString("ChatBackgroundColor"));
                            MyApplication.getMyApplicationInstance().ChatBorderColor = Color.parseColor(textStyleJson.getString("ChatBorderColor"));
                            MyApplication.getMyApplicationInstance().UserId = userId;

                            // 保存密码
                            SharedPreferences sharedPreferences = context.getSharedPreferences("CommunicateApp",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId",userId);
                            editor.putString("userPassword",userPassword);
                            editor.commit();
                            signal.postValue(SUCCESSFUL);
                        }
                        else if ( "202".equals(resCode)){
                            signal.postValue(PASSWORD_ERROR);
                        }
                        else if( "201".equals(resCode)){
                            signal.postValue(NOT_EXIST);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
