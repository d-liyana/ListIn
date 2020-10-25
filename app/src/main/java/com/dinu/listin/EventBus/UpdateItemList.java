package com.dinu.listin.EventBus;

import com.dinu.listin.Database.ListItem;

public class UpdateItemList {

    private ListItem listItem;

    public UpdateItemList(ListItem listItem) {
        this.listItem = listItem;
    }

    public ListItem getListItem() {
        return listItem;
    }

    public void setListItem(ListItem listItem) {
        this.listItem = listItem;
    }
}
