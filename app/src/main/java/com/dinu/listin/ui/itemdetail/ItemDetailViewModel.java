package com.dinu.listin.ui.itemdetail;

import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.Item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemDetailViewModel extends ViewModel {
    private MutableLiveData<Item> mutableLiveDataItem;


    public MutableLiveData<Item> getMutableLiveDataItem() {
        if (mutableLiveDataItem == null) {
            mutableLiveDataItem = new MutableLiveData<>();
            mutableLiveDataItem.setValue(Common.selectedItem);
        }
        return mutableLiveDataItem;
    }
}
