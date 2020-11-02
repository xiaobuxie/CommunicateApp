package com.example.communicateapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class FriendRequest {
    @NonNull
    @PrimaryKey
    private String userId;
    private String username;
    private String headPhoto;
    private String time;
    private String textStyle;
    @Ignore
    public FriendRequest(){}
    public FriendRequest(String userId,String username, String headPhoto , String time,String textStyle){
        this.userId = userId;
        this.username = username;
        this.headPhoto = headPhoto;
        this.time = time;
        this.textStyle = textStyle;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(String textStyle) {
        this.textStyle = textStyle;
    }
}
