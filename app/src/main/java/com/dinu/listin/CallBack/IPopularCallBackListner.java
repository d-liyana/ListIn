package com.dinu.listin.CallBack;

import com.dinu.listin.Model.PopularCategory;

import java.util.List;

public interface IPopularCallBackListner {

    void onPopularLoadSuccess(List<PopularCategory> popularCategories);
    void onPopularLoadFailed(String message);
}
