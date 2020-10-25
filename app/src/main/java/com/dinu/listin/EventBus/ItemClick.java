package com.dinu.listin.EventBus;

import com.dinu.listin.Model.Item;

public class ItemClick {
    private boolean success;
    private Item item;

    public ItemClick(boolean success, Item item) {
        this.success = success;
        this.item = item;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
