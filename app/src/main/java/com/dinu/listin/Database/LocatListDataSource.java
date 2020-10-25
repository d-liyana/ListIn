package com.dinu.listin.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocatListDataSource implements ListDataSource {

    private ItemDAO itemDAO;

    public LocatListDataSource(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public Flowable<List<ListItem>> getAllItem(String uid) {
        return itemDAO.getAllItem(uid);
    }

    @Override
    public Single<Integer> countItemList(String uid) {
        return itemDAO.countItemList(uid);
    }

    @Override
    public Single<Double> sumPriceInList(String uid) {
        return itemDAO.sumPriceInList(uid);
    }

    @Override
    public Single<ListItem> getItemInList(String itemid, String uid) {
        return itemDAO.getItemInList(itemid,uid);
    }

    @Override
    public Completable insertOrReplaceAll(ListItem... listItems) {
        return itemDAO.insertOrReplaceAll(listItems);
    }

    @Override
    public Single<Integer> updatItemList(ListItem listItems) {
        return itemDAO.updatItemList(listItems);
    }

    @Override
    public Single<Integer> deleteItemList(ListItem listItem) {
        return itemDAO.deleteItemList(listItem);
    }

    @Override
    public Single<Integer> cleanList(String uid) {
        return itemDAO.cleanList(uid);
    }

    @Override
    public Single<ListItem> getItemListWithSize(String itemid, String uid, String itemSize) {
        return itemDAO.getItemListWithSize(itemid, uid, itemSize);
    }
}
