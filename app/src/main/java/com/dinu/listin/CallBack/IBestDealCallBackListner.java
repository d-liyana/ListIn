package com.dinu.listin.CallBack;

import com.dinu.listin.Model.BestDeal;
import com.dinu.listin.Model.PopularCategory;

import java.util.List;

public interface IBestDealCallBackListner {

    void onBestDealLoadSuccess(List<BestDeal> bestDeals);
    void onBestDealLoadFailed(String message);


}
