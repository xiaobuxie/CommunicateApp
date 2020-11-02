package com.example.communicateapp.UserMainPage.NewFriendPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.database.FriendRequest;
import com.example.communicateapp.database.FriendShip;
import com.example.communicateapp.database.UserDatabase;
import com.example.communicateapp.database.UserDatabaseDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestFriendViewModel extends ViewModel {
    MutableLiveData< List<FriendRequest> > localListSingal;
    MutableLiveData<List<FriendRequest> > serveListSingal;
    // info
    Context context;
    String UserId;
    // local databases
    UserDatabase userDatabase;
    UserDatabaseDAO userDatabaseDAO;

    public MutableLiveData<List<FriendRequest> > getLocalListSingal() {
        if( localListSingal == null){
            localListSingal = new MutableLiveData<>();
        }
        return localListSingal;
    }

    public MutableLiveData<List< FriendRequest> > getServeListSingal() {
        if( serveListSingal == null){
            serveListSingal = new MutableLiveData<>();
        }
        return serveListSingal;
    }

    public void setInfo(Context context,String userId) {
        this.context = context;
        this.UserId = userId;
        if(userDatabase == null){
            userDatabase = Room.databaseBuilder(context,UserDatabase.class,"user_"+userId).build();
            userDatabaseDAO = userDatabase.getDao();
        }
    }

    // 查询本地好友请求
    public void queryLocalRequestList(){
        new Thread(){
            @Override
            public void run(){
                Log.d("RequestFriendViewModel","get local");
                List<FriendRequest> localRequestList = userDatabaseDAO.queryAllRequest();
                Log.d("RequestFriendViewModel","local size " + localRequestList.size());
                localListSingal.postValue(localRequestList);
            }
        }.start();
    }

    //在服务器获取 申请好友请求
    public void queryServeRequestList(final String nowTime){
        String last_update_time = getLastUpdateRequestFriendTime(nowTime);
        String url = MyApplication.getMyApplicationInstance().ip +"QuerFriendRequest";
        OkHttpClient client = new OkHttpClient();
        Log.d("RequestFriendViewModel","query Serve Request user: " + UserId);
        RequestBody requestBody = new FormBody.Builder().add("user",UserId).add("time",last_update_time).build();
        Request request = new okhttp3.Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseStr = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
                        Log.d("RequestFriendViewModel","queryServeRequst  len: " + jsonArray.length());
                        List< FriendRequest > ServeRequestAndHeadList = new ArrayList<>();

                        for(int i = 0;i<jsonArray.length();++i){
                            JSONObject json = (JSONObject) jsonArray.get(i);
                            String userId = json.getString("user_id");
                            String sendTime = json.getString("time");
                            String username = json.getString("username");
                            String headPhoto = json.getString("headPhoto");
                            String textStyleStr = json.getString("textStyle");
                            Log.d("RequestFriendViewModel","queryServeRequst  i: " + i + " id: " + userId);

                            FriendRequest friendRequest = new FriendRequest(userId,username,headPhoto,sendTime,textStyleStr);

                            // 在本地加入 好友请求
                            FriendRequest friendRequest_tem = null;
                            friendRequest_tem = userDatabaseDAO.searchFriendRequest(friendRequest.getUserId());
                            if( friendRequest_tem == null){
                                userDatabaseDAO.InsertFriendRequest(friendRequest);
                            }
                            // 加入list
                            ServeRequestAndHeadList.add(friendRequest);
                        }

                        serveListSingal.postValue(ServeRequestAndHeadList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    // 获取最后更新 请求好友 的时间
    private String getLastUpdateRequestFriendTime(String nowTime){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_" + UserId,Context.MODE_PRIVATE);
        String last_update_time = sharedPreferences.getString("LastUpdateRequestFriendTime","1970-1-1 00:00:00");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LastUpdateRequestFriendTime",nowTime);
        editor.commit();
        return last_update_time;
    }

    // 处理好友请求
    public void handleRequestFriend(final FriendRequest friendRequest , Boolean accept){
        // 先删除本地数据库的请求
        new Thread(){
            @Override
            public void run(){
                userDatabaseDAO.deleteFriendRequest(friendRequest.getUserId());
            }
        }.start();

        if( accept ){
            // 在本地数据库加上好友
            new Thread(){
                @Override
                public void run() {
                    String friend_id = friendRequest.getUserId();
                    FriendShip friendShip_tem = null;
                    friendShip_tem = userDatabaseDAO.searchFriendShip(friend_id);
                    // 假如本地没有这个好友才加入
                    if( friendShip_tem == null) {
                        userDatabaseDAO.InsertFriend( new FriendShip(friend_id,friendRequest.getUsername(),
                                friendRequest.getHeadPhoto(),MyApplication.getMyApplicationInstance().getNowTime(),friendRequest.getTextStyle()) );
                    }
                }
            }.start();
        }

        // 发送给服务端处理 ， 服务器会删除请求，如果同意则会加上好友
        String res = "no";
        if( accept ) res = "yes";
        String url = MyApplication.getMyApplicationInstance().ip +"HandleFriendRequest";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("from_user",friendRequest.getUserId())
                .add("to_user",UserId)
                .add("time", MyApplication.getMyApplicationInstance().getNowTime())
                .add("res",res).build();
        Request request = new okhttp3.Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException { }
        });
    }

}
