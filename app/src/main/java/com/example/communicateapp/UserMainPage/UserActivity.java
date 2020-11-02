package com.example.communicateapp.UserMainPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communicateapp.LoginPage.LoginActivity;
import com.example.communicateapp.LoginPage.LoginViewModel;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.MyView.CircleHeadPhotoView;
import com.example.communicateapp.R;
import com.example.communicateapp.UserMainPage.DrawerPage.ChoseColorDialog;
import com.example.communicateapp.UserMainPage.DrawerPage.UpdateInfoViewModel;
import com.example.communicateapp.UserMainPage.NewFriendPage.NewFriendsFragment;
import com.example.communicateapp.UserMainPage.NowFriendPage.NowFriendsFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;

public class UserActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    public final static int NOWFRIEND_ID = 0;
    public final static int NEWFRIEND_ID = 1;
    public final static int PERSONINFO_ID = 2;

    public final static int CHANGE_HEAD_PHOTO_ID = 0;
    public final static int CHANGE_CHAT_STYLE_ID = 1;
    public final static int LOGOUT_ID = 2;

    private final int PHOTO_REQUEST_GALLERY = 1;
    private final int PHOTO_REQUEST_CUT = 2;
    // view
    private FrameLayout frameLayout;
    RadioGroup radioGroup;
    RadioButton nowFriendRadioButton;
    RadioButton newFriendRadioButton;
    // Fragment
    NewFriendsFragment newFriendsFragment;
    NowFriendsFragment nowFriendsFragment;
    // Nav
    NavigationView navigationView;
    View navigationHeadView;
    CircleHeadPhotoView headPhotoView;
    TextView headUserName;

    Bitmap chosBitmap;

    // viewmodel
    UpdateInfoViewModel updateInfoViewModel;
    LoginViewModel loginViewModel;
    private Context mContext;
    private LifecycleOwner mLifecycleOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mContext = this;
        mLifecycleOwner = this;
        initViewModel();
        checkRememberAccount();
    }
    // 检查是否有账号，有就直接初始化，没有就跳到登陆页面
    private void checkRememberAccount(){
        Log.d("UserActivity","checkRemm");
        SharedPreferences sharedPreferences = getSharedPreferences("CommunicateApp",Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId",null);
        String userPassword = sharedPreferences.getString("userPassword",null);
        if(userId == null){
            Log.d("UserActivity","userId is null");
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            loginViewModel.loginAccount(userId,userPassword);
        }
    }
    private void initView(){
        Log.d("UserActivity","initviewModel");
        radioGroup = findViewById(R.id.radio_group);
        nowFriendRadioButton = findViewById(R.id.radio_button_nowfriend);
        newFriendRadioButton = findViewById(R.id.radio_button_newfriend);
        nowFriendRadioButton.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);

        // nav
        navigationView = findViewById(R.id.navigation);
        navigationHeadView = navigationView.getHeaderView(0);
        headPhotoView = navigationHeadView.findViewById(R.id.head_headphoto);
        headUserName = navigationHeadView.findViewById(R.id.head_username);
        headPhotoView.setImageBitmap(MyApplication.getMyApplicationInstance().HeadPhoto);
        headUserName.setText(MyApplication.getMyApplicationInstance().UserName);

        frameLayout = findViewById(R.id.fragment_frame_layout);
        // fragments
        newFriendsFragment = new NewFriendsFragment();
        nowFriendsFragment = new NowFriendsFragment();
        // 一开始是nowfriend
        updateFragment(nowFriendsFragment);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radio_button_nowfriend:
                updateFragment(nowFriendsFragment);
                break;
            case R.id.radio_button_newfriend:
                updateFragment(newFriendsFragment);
                break;
        }
    }

    private void updateFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame_layout,fragment);
        fragmentTransaction.commit();
    }

    // init view model
    private void initViewModel(){
        updateInfoViewModel = new UpdateInfoViewModel();
        updateInfoViewModel.setInfo(this,MyApplication.getMyApplicationInstance().UserId);
        // 获取改变头像是否成功的信息
        updateInfoViewModel.getChangeHeadPhotoSingal().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case UpdateInfoViewModel.CHANGE_TOO_LAEGE:
                        Toast.makeText(getApplicationContext(),"选择头像太大",Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateInfoViewModel.CHANGE_NOT_OK:
                        Toast.makeText(getApplicationContext(),"上传头像失败",Toast.LENGTH_SHORT).show();
                        break;
                    case  UpdateInfoViewModel.CHANGE_OK:
                        headPhotoView.setImageBitmap(chosBitmap);
                        Toast.makeText(getApplicationContext(),"上传头像成功",Toast.LENGTH_SHORT).show();
                        // 更新本地
                        break;
                }
            }
        });

        // 自动登录
        loginViewModel = new LoginViewModel();
        loginViewModel.setInfo(this);
        loginViewModel.getSignal().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == LoginViewModel.SUCCESSFUL){
                  //成功才进行接下来操作
                    // 再次更新userid
                    updateInfoViewModel.setInfo(mContext,MyApplication.getMyApplicationInstance().UserId);
                    initView();
                    initItemClickListener();
                }
                else{
                    //不成功则跳回登录界面
                    Toast.makeText(mContext,"自动登录失效，请重新登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    // 为每个item设置点击函数
    private void initItemClickListener(){

        // 更改头像item
        navigationView.getMenu().getItem(CHANGE_HEAD_PHOTO_ID).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 从文件中获取头像
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory("android.intent.category.DEFAULT");
                startActivityForResult(intent,PHOTO_REQUEST_GALLERY);
                return true;
            }
        });

        // 更改text style item
        navigationView.getMenu().getItem(CHANGE_CHAT_STYLE_ID).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ChoseColorDialog choseColorDialog = new ChoseColorDialog(mContext,"设置你的聊天气泡风格",mLifecycleOwner);
                choseColorDialog.show();
                return true;
            }
        });

        // logout item
        navigationView.getMenu().getItem(LOGOUT_ID).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences sharedPreferences = getSharedPreferences("CommunicateApp",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId",null);
                editor.putString("userPassword",null);
                editor.commit();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
    }

    //从文件获取头像
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 从相册返回回来的图片
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        chosBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        updateInfoViewModel.changeHeadPhoto(chosBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // viewmodel修改完，然后前端再改
//                    viewModel.changeHeadPhoto(chosBitmap);
                }
                break;
            default:
                break;
        }
    }

}