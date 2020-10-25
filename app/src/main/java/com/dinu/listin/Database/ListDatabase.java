package com.dinu.listin.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1,entities = ListItem.class,exportSchema = false)
public abstract class ListDatabase extends RoomDatabase {

    public abstract ItemDAO itemDAO();
    private static ListDatabase instance;

    public static ListDatabase getInstance(Context context){
        if (instance == null)
            instance= Room.databaseBuilder(context,ListDatabase.class,"ListIn1").build();

        return instance;
    }


}
