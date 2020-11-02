package com.example.communicateapp.UserMainPage.NowFriendPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.communicateapp.MyApplication;
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

public class FriendShipViewModel extends ViewModel {
    private MutableLiveData<Boolean> AddFriendRequestSingal;
    private MutableLiveData<List<FriendShip> > LocalFriendsListSingal;
    private MutableLiveData<List<FriendShip> > ServeFriendsListSingal;

    UserDatabase userDatabase;
    UserDatabaseDAO userDatabaseDAO;

    Context context;
    String UserId;

    public void setInfo(Context context,String userId){
        this.context = context;
        this.UserId = userId;
        if(userDatabase == null){
            userDatabase = Room.databaseBuilder(context.getApplicationContext(),UserDatabase.class,"user_" + UserId).build();
            userDatabaseDAO = userDatabase.getDao();
        }
    }

    public MutableLiveData<List<FriendShip>> getLocalFriendsListSingal() {
        if(LocalFriendsListSingal == null){
            LocalFriendsListSingal = new MutableLiveData<>();
        }
        return LocalFriendsListSingal;
    }

    public MutableLiveData<List<FriendShip>> getServeFriendsListSingal() {
        if(ServeFriendsListSingal == null){
            ServeFriendsListSingal = new MutableLiveData<>();
        }
        return ServeFriendsListSingal;
    }

    public MutableLiveData<Boolean> getAddFriendRequestSingal() {
        if(AddFriendRequestSingal == null ){
            AddFriendRequestSingal = new MutableLiveData<>();
        }
        return AddFriendRequestSingal;
    }

    // 查询本地好友
    public void queryLocalFriends(){
        new Thread(){
            @Override
            public void run(){
                List<FriendShip> friendShipList = userDatabaseDAO.queryAllFriends();
                LocalFriendsListSingal.postValue(friendShipList);
            }
        }.start();
    }

    // 查询服务器好友
    public void queryServeFriends(){
        String url = MyApplication.getMyApplicationInstance().ip + "QueryFriendShip";
        String lasTime = getLastUpdateNowFriendTime( MyApplication.getMyApplicationInstance().getNowTime() );
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("user",UserId).add("time",lasTime).build();
        Request request = new okhttp3.Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if( response.isSuccessful()){
                    String resStr = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(resStr);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
                        // 添加到本地数据库
                        List<FriendShip> friendShipList = new ArrayList<>();
                        for(int i = 0;i<jsonArray.length();++i){
                            JSONObject json = (JSONObject) jsonArray.get(i);
                            String friendUserId = json.getString("user_id");
                            String friendUserName = json.getString("username");
                            String friendHeadPhoto = json.getString("headPhoto");
                            String friendTextStyle = json.getString("textStyle");
                            String time = json.getString("time");

                            FriendShip friendShip = new FriendShip(friendUserId,friendUserName,friendHeadPhoto,time,friendTextStyle);
                            // 先加进列表
                            friendShipList.add(friendShip);
                            //查询本地数据库有无此好友，没有则 添加到本地
                            FriendShip friendShip_tem = null;
                            friendShip_tem = userDatabaseDAO.searchFriend(friendShip.getUserId());
                            if( friendShip_tem == null ){
                                userDatabaseDAO.InsertFriend(friendShip);
                            }
                        }
                        ServeFriendsListSingal.postValue(friendShipList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 获取最后更新当前好友的时间，并且更新最后更新的时间
    private String getLastUpdateNowFriendTime(String nowTime){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_" + UserId,Context.MODE_PRIVATE);
        String lasTime = sharedPreferences.getString("LastUpdateNowFriendTime","1970-1-1 00:00:00");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LastUpdateNowFriendTime",nowTime);
        editor.apply();
        Log.d("lastime: ",nowTime);
        return lasTime;
    }

    // 添加好友请求
    public void addFriendRequest(final String add_friendId, final String nowTime){
        String url = MyApplication.getMyApplicationInstance().ip + "AddFriendRequest";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("from_user",UserId)
                                                        .add("to_user",add_friendId)
                                                        .add("time",MyApplication.getMyApplicationInstance().getNowTime()).build();
        Request request = new okhttp3.Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AddFriendRequestSingal.postValue(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String resStr = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(resStr);
                        String res = jsonObject.getString("res");
                        if( "ok".equals(res) ){
                            AddFriendRequestSingal.postValue(true);
                        }
                        else AddFriendRequestSingal.postValue(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
