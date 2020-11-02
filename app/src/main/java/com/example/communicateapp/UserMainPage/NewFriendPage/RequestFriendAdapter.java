package com.example.communicateapp.UserMainPage.NewFriendPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communicateapp.BitmapUtil;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;
import com.example.communicateapp.database.FriendRequest;

import java.util.List;

public class RequestFriendAdapter extends RecyclerView.Adapter<RequestFriendAdapter.MyViewHolder> {
    //data
    List<FriendRequest> requestList;
    RequestFriendViewModel requestFriendViewModel;
    private Context context;

    public RequestFriendAdapter(List< FriendRequest > requestList, Context context){
        this.requestList = requestList;
        this.context = context;
        requestFriendViewModel = new RequestFriendViewModel();
        requestFriendViewModel.setInfo(context,MyApplication.getMyApplicationInstance().UserId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView request_image;
        TextView request_username;
        Button OkButton;
        Button NoButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            request_image = itemView.findViewById(R.id.friend_request_image);
            request_username = itemView.findViewById(R.id.friend_request_name);
            OkButton = itemView.findViewById(R.id.yes_request_button);
            NoButton = itemView.findViewById(R.id.no_request_button);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_friend_request_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final FriendRequest friendRequest = requestList.get(position);
        // 名字
        String requestUserName = friendRequest.getUsername();
        holder.request_username.setText(requestUserName);

        //头像
        String bitStr = friendRequest.getHeadPhoto();
        Bitmap bitmap = BitmapUtil.getBitmapUtilInstance().StringToBitmap(bitStr);
        holder.request_image.setImageBitmap(bitmap);

        // 设置点击ok 和 no  的 处理事件
        holder.OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"yes",Toast.LENGTH_SHORT).show();
                requestFriendViewModel.handleRequestFriend(friendRequest,true);
                // 删除该请求
                deleteRequestFriend(position);
            }
        });

        holder.NoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"no",Toast.LENGTH_SHORT).show();
                requestFriendViewModel.handleRequestFriend(friendRequest,false);
                //删除该请求
                deleteRequestFriend(position);
            }
        });
    }

    public void addRequestFriend(int position,FriendRequest friendRequest){
        Log.d("RequestFriendAdapter","add:" + friendRequest.getUserId());
        requestList.add(friendRequest);
        notifyItemChanged(position);
    }

    public void deleteRequestFriend(int position){
        requestList.remove(position);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}
