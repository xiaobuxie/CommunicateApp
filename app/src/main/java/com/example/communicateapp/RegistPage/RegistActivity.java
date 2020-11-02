package com.example.communicateapp.RegistPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communicateapp.BitmapUtil;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.MyView.ChatTextView;
import com.example.communicateapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class
RegistActivity extends AppCompatActivity {

    private final int PHOTO_REQUEST_GALLERY = 1;
    private final int PHOTO_REQUEST_CUT = 2;
    private final long  HEAD_PHOTO_LIMIT = 4294967295L;

    // View
    private Button RegistButton;
    private TextView RegistUserId;
    private TextView RegistUserName;
    private TextView RegistUserPassword;
    private ImageView RegistHeadPhoto;
    private View ChatTextColorView;
    private View ChatBackgroundColorView;
    private View ChatBorderColorView;

    // spinner
    private Spinner ChatTextColorSpinner;
    private Spinner ChatBackgroundColorSpinner;
    private Spinner ChatBorderColorSpinner;
    private List<String> ColorList;
    private ArrayAdapter<String> ColorAdapter;

    // viemodel
    private RegistViewModel registViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initViewModel();
        initView();
        initSpinnerAndAdapter();
    }
    private void initViewModel(){
        registViewModel = new RegistViewModel();
        registViewModel.getSingal().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case RegistViewModel.ALREADY_EXIST:
                        Toast.makeText(getApplicationContext(),"用户名已经存在",Toast.LENGTH_SHORT).show();
                        break;
                    case RegistViewModel.ERROR:
                        Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
                        break;
                    case RegistViewModel.SUCCESSFUL:
                        Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
    private void initView(){
        RegistButton = findViewById(R.id.regist_send_button);
        RegistUserId = findViewById(R.id.regist_userid);
        RegistUserName = findViewById(R.id.regist_username);
        RegistHeadPhoto = findViewById(R.id.regist_chos_headphoto);
        RegistUserPassword = findViewById(R.id.regist_password);
        RegistHeadPhoto.setImageResource(R.drawable.frimage);
        ChatTextColorView = findViewById(R.id.chat_text_color_view);
        ChatBackgroundColorView = findViewById(R.id.chat_background_color_view);
        ChatBorderColorView = findViewById(R.id.chat_border_color_view);

        // 点击切换头像
        RegistHeadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory("android.intent.category.DEFAULT");
                startActivityForResult(intent,PHOTO_REQUEST_GALLERY);
            }
        });

        //点击注册
        RegistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regist_user_id = RegistUserId.getText().toString();
                String regist_user_password = RegistUserPassword.getText().toString();
                String regist_user_name = RegistUserName.getText().toString();
//                Bitmap headPhoto = BitmapFactory.decodeResource(getResources(),R.id.regist_chos_headphoto);
                Bitmap headPhoto = ((BitmapDrawable)RegistHeadPhoto.getDrawable()).getBitmap();
                if( headPhoto == null){

                    Log.d("RegistActivity","chos headPhoto is null");
                }
                String heaPhotoStr = BitmapUtil.getBitmapUtilInstance().BitmapToString(headPhoto);

                if( regist_user_id.isEmpty() || regist_user_password.isEmpty() || regist_user_name.isEmpty() ){
                    Toast.makeText(getApplicationContext(),"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    JSONObject styleJson = new JSONObject();
                    ColorDrawable colorDrawable;
                    colorDrawable = (ColorDrawable) ChatBackgroundColorView.getBackground();
                    int background_color = colorDrawable.getColor();
                    colorDrawable = (ColorDrawable) ChatTextColorView.getBackground();
                    int text_color = colorDrawable.getColor();
                    colorDrawable = (ColorDrawable) ChatBorderColorView.getBackground();
                    int border_color = colorDrawable.getColor();
                    try {
                        styleJson.put("ChatBackgroundColor",MyApplication.getMyApplicationInstance().colorToString(background_color));
                        styleJson.put("ChatTextColor",MyApplication.getMyApplicationInstance().colorToString(text_color));
                        styleJson.put("ChatBorderColor",MyApplication.getMyApplicationInstance().colorToString(border_color));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    registViewModel.addAccount(regist_user_id,regist_user_password,heaPhotoStr,regist_user_name,styleJson.toString());
                }
            }
        });
    }

    // 从文件获取到头像
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 从相册返回回来的图片
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        Bitmap chosBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        String chosBitmapStr = BitmapUtil.getBitmapUtilInstance().BitmapToString(chosBitmap);
                        if( chosBitmapStr.length() > HEAD_PHOTO_LIMIT ){
                            Toast.makeText(this,"选择头像过大",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            RegistHeadPhoto.setImageBitmap(chosBitmap);
                        }
                        if( chosBitmap == null ){
                            Log.d("RegistActivity"," return chos bitmap is null");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initSpinnerAndAdapter(){
        initList();
        ChatBackgroundColorSpinner = findViewById(R.id.chat_background_color_spinner);
        ChatTextColorSpinner = findViewById(R.id.chat_text_color_spinner);
        ChatBorderColorSpinner = findViewById(R.id.chat_border_color_spinner);

        ColorAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,ColorList);
        ChatBorderColorSpinner.setAdapter(ColorAdapter);
        ChatTextColorSpinner.setAdapter(ColorAdapter);
        ChatBackgroundColorSpinner.setAdapter(ColorAdapter);

        ChatBackgroundColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String content = parent.getItemAtPosition(position).toString();
                String ColorStr = getColor(content);
                int color = Color.parseColor(ColorStr);
                ChatBackgroundColorView.setBackgroundColor(color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ChatTextColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String content = parent.getItemAtPosition(position).toString();
                String ColorStr = getColor(content);
                int color = Color.parseColor(ColorStr);
                ChatTextColorView.setBackgroundColor(color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ChatBorderColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String content = parent.getItemAtPosition(position).toString();
                String ColorStr = getColor(content);
                int color = Color.parseColor(ColorStr);
                ChatBorderColorView.setBackgroundColor(color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initList(){
        ColorList = new ArrayList<>();
        ColorList.add("红色");
        ColorList.add("黄色");
        ColorList.add("紫色");
        ColorList.add("黑色");
        ColorList.add("橙色");
    }

    private String getColor(String name){
        switch (name){
            case "红色":
                return "#ff0000";
            case "黄色":
                return "#ffff00";
            case "紫色":
                return "#ee82ee";
            case "黑色":
                return "#000000";
            case "橙色":
                return "#ffa500";
            default:
                return "#f5f5f5";
        }
    }
}