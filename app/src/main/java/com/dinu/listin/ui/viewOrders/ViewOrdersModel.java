package com.dinu.listin.ui.viewOrders;

import com.dinu.listin.Model.Order;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewOrdersModel extends ViewModel {
    private MutableLiveData<List<Order>> mutableLiveDataOrders;

    public ViewOrdersModel(){
        mutableLiveDataOrders=new MutableLiveData<>();
    }

    public MutableLiveData<List<Order>> getMutableLiveDataOrders() {
        return mutableLiveDataOrders;
    }

    public void setMutableLiveDataOrders(List<Order> listOrders) {
        mutableLiveDataOrders.setValue(listOrders);
    }
}
