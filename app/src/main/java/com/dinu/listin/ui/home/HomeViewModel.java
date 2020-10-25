package com.dinu.listin.ui.home;

import android.util.Log;

import com.dinu.listin.CallBack.IBestDealCallBackListner;
import com.dinu.listin.CallBack.IPopularCallBackListner;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.BestDeal;
import com.dinu.listin.Model.PopularCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel implements IPopularCallBackListner, IBestDealCallBackListner {

private MutableLiveData<List<PopularCategory>> popularCatList;
private MutableLiveData<List<BestDeal>> bestDealList;

private MutableLiveData<String> msgError;

private IPopularCallBackListner popularCallBackListner;
private IBestDealCallBackListner bestDealCallBackListner;

    public HomeViewModel() {
        popularCallBackListner = this;
        bestDealCallBackListner = this;

    }

    public MutableLiveData<List<BestDeal>> getBestDealList() {

        if (bestDealList==null){
            bestDealList= new MutableLiveData<>();
            msgError=new MutableLiveData<>();
            loadBestDealList();
        }

        Log.i("Return","bestDealList");
        return bestDealList;
    }

    private void loadBestDealList() {

        List<BestDeal> tempDealList=new ArrayList<>();
        DatabaseReference bestRef=FirebaseDatabase.getInstance().getReference(Common.BEST_DEAL_REF);
        bestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("WithinonDataChange",""+tempDealList);
                for (DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    BestDeal bestDeal=itemSnapshot.getValue(BestDeal.class);
                    Log.i("WithinonDatabestDeal",""+bestDeal);

                    tempDealList.add(bestDeal);
                }
                bestDealCallBackListner.onBestDealLoadSuccess(tempDealList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                bestDealCallBackListner.onBestDealLoadFailed(databaseError.getMessage());

            }
        });
    }

    public MutableLiveData<List<PopularCategory>> getPopularCatList() {
        if (popularCatList==null){
            popularCatList= new MutableLiveData<>();
            msgError=new MutableLiveData<>();
            loadPopularList();

        }
        return popularCatList;
    }

    private void loadPopularList() {
        final List<PopularCategory> tempList= new ArrayList<>();
        final DatabaseReference popularRef= FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORY_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    PopularCategory popularCategory=itemSnapshot.getValue(PopularCategory.class);
                    tempList.add(popularCategory);
                }
                popularCallBackListner.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallBackListner.onPopularLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMsgError() {
        return msgError;
    }

    @Override
    public void onPopularLoadSuccess(List<PopularCategory> popularCategories) {
            popularCatList.setValue(popularCategories);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        msgError.setValue(message);
    }

    @Override
    public void onBestDealLoadSuccess(List<BestDeal> bestDeals) {
        Log.i("BestDeal","LoadSuccess");
            bestDealList.setValue(bestDeals);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
            msgError.setValue(message);
    }
}