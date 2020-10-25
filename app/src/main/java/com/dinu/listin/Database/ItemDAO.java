package com.dinu.listin.Database;

import android.util.Log;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ItemDAO {

    @Query("SELECT * FROM List WHERE uid=:uid")
    Flowable<List<ListItem>> getAllItem(String uid);

    @Query("SELECT COUNT(*) FROM List WHERE uid=:uid")
    Single<Integer> countItemList(String uid);

    @Query("SELECT SUM((itemPrice + itemExtraPrice)*itemQuantity )FROM List WHERE uid=:uid")
    Single<Double> sumPriceInList(String uid);

    @Query("SELECT * FROM List WHERE itemId=:itemid AND uid=:uid")
    Single<ListItem> getItemInList(String itemid,String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(ListItem... listItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updatItemList(ListItem listItems);

    @Delete
    Single<Integer> deleteItemList(ListItem listItem);

    @Query("DELETE FROM List WHERE uid=:uid")
    Single<Integer> cleanList(String uid);



    @Query("SELECT * FROM List WHERE itemId=:itemid AND uid=:uid AND itemSize=:itemSize")
    Single<ListItem> getItemListWithSize(String itemid,String uid,String itemSize);

}
