package com.example.communicateapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Messages.class  , FriendShip.class, FriendRequest.class}, version = 9,exportSchema = false)
public abstract  class UserDatabase extends RoomDatabase{
    public abstract UserDatabaseDAO getDao();
}
