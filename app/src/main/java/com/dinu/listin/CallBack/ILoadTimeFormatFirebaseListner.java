package com.dinu.listin.CallBack;

import com.dinu.listin.Model.Order;

public interface ILoadTimeFormatFirebaseListner {
    void onLoadTimeSuccess(Order order,long estimateTimeMills);
    void onLoadTimeFailed(String message);
}
