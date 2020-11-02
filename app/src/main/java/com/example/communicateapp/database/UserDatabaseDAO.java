package com.example.communicateapp.database;
import androidx.room.*;

import java.util.List;

@Dao
public interface UserDatabaseDAO{
    @Insert
    void InsertMessage(Messages...messages);
    @Query("SELECT * FROM Messages WHERE fromUserId = :talkUser or toUserId = :talkUser")
    List<Messages> queryAllMessage(String talkUser);

    @Query("SELECT * FROM Messages WHERE fromUserId = :from and toUserId = :to and sendTime = :time and content = :content")
    Messages searchMessage(String from , String to , String time , String content);

    /*friend*/
    @Insert
    void InsertFriend(FriendShip friend);

    @Query("SELECT * FROM FriendShip WHERE userId = :user_id")
    FriendShip searchFriendShip(String user_id);

    @Query("SELECT * FROM FriendShip")
    List<FriendShip> queryAllFriends();

    @Query("SELECT * FROM FriendShip WHERE userId = :user_id")
    FriendShip searchFriend(String user_id);


    /*friendRequest*/
    @Insert
    void InsertFriendRequest(FriendRequest friendRequest);

    @Query("SELECT * FROM FriendRequest")
    List<FriendRequest> queryAllRequest();

    // 查询某一个userid 的好友请求
    @Query("SELECT * FROM FriendRequest WHERE userId = :user_id")
    FriendRequest searchFriendRequest(String user_id);

    @Query("DELETE FROM FriendRequest WHERE userId = :user_id")
    void deleteFriendRequest(String user_id);
}
