package com.dinu.listin.Database;

import android.util.Log;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "List",primaryKeys = {"uid","itemId","itemSize"})
public class ListItem {


    @NonNull
    @ColumnInfo(name = "itemId")
    private String itemId;

    @ColumnInfo(name = "itemName")
    private String itemName;

    @ColumnInfo(name = "itemImage")
    private String itemImage;

    @ColumnInfo(name = "itemPrice")
    private Double itemPrice;

    @ColumnInfo(name = "itemExtraPrice")
    private Double itemExtraPrice;

    @ColumnInfo(name = "itemQuantity")
    private int itemQuantity;

    @ColumnInfo(name = "userPhone")
    private String userPhone;

    @NonNull
    @ColumnInfo(name = "itemSize")
    private String itemSize;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Double getItemExtraPrice() {
        return itemExtraPrice;
    }

    public void setItemExtraPrice(Double itemExtraPrice) {
        this.itemExtraPrice = itemExtraPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof ListItem))
            return false;
        ListItem listItem=(ListItem)obj;
            return listItem.getItemId().equals(this.itemId) && listItem.getItemSize().equals(this.itemSize);
        //return super.equals(obj);
    }
}

