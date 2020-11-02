package com.example.communicateapp.UserMainPage.NowFriendPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communicateapp.BitmapUtil;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;
import com.example.communicateapp.TalkPage.TalkActivity;
import com.example.communicateapp.database.FriendShip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    // data
    List<FriendShip> friendList;

    private Context context;

    public FriendAdapter(List<FriendShip> new_friendList, Context context){
        this.friendList = new_friendList;
        this.context = context;
    }

    // viewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView friendUserName;
        ImageView friendImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            friendUserName = (TextView)itemView.findViewById(R.id.friendUerName);
            friendImage = (ImageView)itemView.findViewById(R.id.friend_image);
        }
    }

    @Override
    public FriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
        MyViewHolder friendViewHolder = new MyViewHolder(view);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FriendShip friend = friendList.get(position);
        String bitStr = friend.getHeadPhoto();
        Bitmap bitmap = BitmapUtil.getBitmapUtilInstance().StringToBitmap(bitStr);

        holder.friendUserName.setText(friend.getUsername());
        holder.friendImage.setImageBitmap(bitmap);
        // onclick listneer

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,""+friend.getUsername(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context.getApplicationContext(), TalkActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("talkerUserId",friend.getUserId());
                bundle.putString("talkerUserName",friend.getUsername());
//                bundle.putString("talkerHeadPhoto",friend.getHeadPhoto());
                bundle.putString("talkerTextStyle",friend.getTextStyle());
                String bitmapStr = friend.getHeadPhoto();
                MyApplication.getMyApplicationInstance().TalkerHeadPhoto = BitmapUtil.getBitmapUtilInstance().StringToBitmap(bitmapStr);

                Log.d("FriendAdapter","textStyle: " + friend.getTextStyle());
                intent.putExtras(bundle);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    // add friend
    public void addFriendItem(int pos, FriendShip friendShip){
        friendList.add(pos, friendShip);
        notifyItemChanged(pos);
    }
}
