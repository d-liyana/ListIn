package com.dinu.listin.Database;

import java.util.List;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface ListDataSource {

    Flowable<List<ListItem>> getAllItem(String uid);
    Single<Integer> countItemList(String uid);
    Single<Double> sumPriceInList(String uid);
    Single<ListItem> getItemInList(String itemid,String uid);
    Completable insertOrReplaceAll(ListItem... listItems);
    Single<Integer> updatItemList(ListItem listItems);
    Single<Integer> deleteItemList(ListItem listItem);
    Single<Integer> cleanList(String uid);
    Single<ListItem> getItemListWithSize(String uid,String itemid,String itemSize);

}
