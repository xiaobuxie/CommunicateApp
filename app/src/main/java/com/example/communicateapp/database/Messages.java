package com.example.communicateapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Messages {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private  int id;

    private String fromUserId;
    private String toUserId;
    private String sendTime;
    private String content;
    @Ignore
    public Messages(){

    }
    public Messages(String fromUserId , String toUserId, String sendTime , String content){
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.sendTime = sendTime;
        this.content = content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setId(int id) { this.id = id; }

    public void setSendTime(String sendTime) {this.sendTime = sendTime; }

    public int getId() { return id; }

    public String getContent() {return content; }

    public String getFromUserId() {return fromUserId; }

    public String getSendTime() {return sendTime; }

    public String getToUserId() {  return toUserId; }

    public void setToUserId(String toUserId) {this.toUserId = toUserId; }
}
