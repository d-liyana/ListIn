package com.dinu.listin.CallBack;

import com.dinu.listin.Model.BestDeal;
import com.dinu.listin.Model.Category;

import java.util.List;

public interface ICategoryCallBackListner {
    void onCategoryLoadSuccess(List<Category> categoryList);
    void onCategoryLoadFailed(String message);

}
