package com.example.communicateapp.UserMainPage.DrawerPage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChoseColorDialog extends Dialog {

    private Button confirmButton;
    private String dialogName;
    private TextView dialogNameTextView;
    // spinner
    private Spinner ChatTextColorSpinner;
    private Spinner ChatBackgroundColorSpinner;
    private Spinner ChatBorderColorSpinner;
    private List<String> ColorList;
    private ArrayAdapter<String> ColorAdapter;

    private View ChatTextColorView;
    private View ChatBackgroundColorView;
    private View ChatBorderColorView;

    // view model
    UpdateInfoViewModel updateInfoViewModel;
    // context
    Context context;
    LifecycleOwner mLifeCycleOwner;

    public ChoseColorDialog(@NonNull Context context,String dialogName,LifecycleOwner lifecycleOwner) {
        super(context);
        this.dialogName = dialogName;
        this.context = context;
        this.mLifeCycleOwner = lifecycleOwner;
    }

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chose_color_dialog);
        dialogNameTextView = findViewById(R.id.dialog_name);
        dialogNameTextView.setText(dialogName);

        confirmButton = findViewById(R.id.confirm_color_chos_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject styleJson = new JSONObject();
                ColorDrawable colorDrawable;
                colorDrawable = (ColorDrawable) ChatBackgroundColorView.getBackground();
                int background_color = colorDrawable.getColor();
                colorDrawable = (ColorDrawable) ChatTextColorView.getBackground();
                int text_color = colorDrawable.getColor();
                colorDrawable = (ColorDrawable) ChatBorderColorView.getBackground();
                int border_color = colorDrawable.getColor();
                try {
                    styleJson.put("ChatBackgroundColor", MyApplication.getMyApplicationInstance().colorToString(background_color));
                    styleJson.put("ChatTextColor",MyApplication.getMyApplicationInstance().colorToString(text_color));
                    styleJson.put("ChatBorderColor",MyApplication.getMyApplicationInstance().colorToString(border_color));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateInfoViewModel.changeTextStyle(styleJson.toString());
            }
        });

        // spinner
        ChatTextColorView = findViewById(R.id.chat_text_color_view);
        ChatBackgroundColorView = findViewById(R.id.chat_background_color_view);
        ChatBorderColorView = findViewById(R.id.chat_border_color_view);

        initViewModel();
        initSpinnerAndAdapter();
    }

    // init view model
    private void initViewModel(){
        updateInfoViewModel = new UpdateInfoViewModel();
        updateInfoViewModel.setInfo(context,MyApplication.getMyApplicationInstance().UserId);
        updateInfoViewModel.getChangeTextStyleSingal().observe(mLifeCycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if( integer == UpdateInfoViewModel.CHANGE_NOT_OK){
                    Toast.makeText(context,"修改不成功",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }
    private void initSpinnerAndAdapter(){
        initList();
        ChatBackgroundColorSpinner = findViewById(R.id.chat_background_color_spinner);
        ChatTextColorSpinner = findViewById(R.id.chat_text_color_spinner);
        ChatBorderColorSpinner = findViewById(R.id.chat_border_color_spinner);

        ColorAdapter = new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,ColorList);
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
