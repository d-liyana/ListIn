package com.dinu.listin.ui.itemlist;

import android.view.View;

import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

public class ItemListViewModel extends ViewModel {

    private MutableLiveData<List<Item>> mutableLiveDataItemList;


    public ItemListViewModel() {

    }

    public MutableLiveData<List<Item>> getMutableLiveDataItemList() {
        if (mutableLiveDataItemList == null){
            mutableLiveDataItemList = new MutableLiveData<>();
            mutableLiveDataItemList.setValue(Common.categorySelected.getItems());
        }
        return mutableLiveDataItemList;
    }
}
