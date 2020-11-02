package com.example.communicateapp.LoginPage;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communicateapp.BitmapUtil;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;
import com.example.communicateapp.RegistPage.RegistActivity;
import com.example.communicateapp.UserMainPage.UserActivity;

public class LoginActivity extends AppCompatActivity {

    // layout
    private Button RegistButton;
    private Button LoginButton;
    private TextView UserIdView;
    private TextView UserPasswordView;



    // viewmodel
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserIdView = findViewById(R.id.user_id);
        UserPasswordView = findViewById(R.id.user_password);
        RegistButton = findViewById(R.id.regist_button);
        LoginButton = findViewById(R.id.login_button);

        initViewModel();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id_str = UserIdView.getText().toString();
                String user_password = UserPasswordView.getText().toString();
                if( user_id_str.trim().isEmpty() || user_password.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    loginViewModel.loginAccount(user_id_str,user_password);
                }
            }
        });
        RegistButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivity(intent);
            }
        });
        // 记录已经登录的账号

        // test
//        UserIdView.setText("123");
//        UserPasswordView.setText("123");
//        LoginButton.performClick();
    }
    private void initViewModel(){
        // init viewmodel
//        myViewModel = new ViewModelProvider.AndroidViewModelFactory();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.setInfo(this);
        // create obser
        loginViewModel.getSignal().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer singal) {
//                Log.d("hjj",""+nowsignal);
                if(singal == loginViewModel.ERROR){
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(singal == loginViewModel.NOT_EXIST){
                        Toast.makeText(getApplicationContext(),"无此用户",Toast.LENGTH_SHORT).show();
                    }
                    else if( singal == loginViewModel.PASSWORD_ERROR){
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        GoToUserActivity();
                    }
                }
            }
        });
    }
    private void GoToUserActivity(){
        // 进去先把全局写好
        // 启动
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }
}