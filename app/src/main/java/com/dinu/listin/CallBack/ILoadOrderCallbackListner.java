package com.dinu.listin.CallBack;

import com.dinu.listin.Model.Order;

import java.util.List;

public interface ILoadOrderCallbackListner  {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFail(String message);

}
