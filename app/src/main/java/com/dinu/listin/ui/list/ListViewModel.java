package com.dinu.listin.ui.list;

import android.content.Context;

import com.dinu.listin.Common.Common;
import com.dinu.listin.Database.ListDataSource;
import com.dinu.listin.Database.ListDatabase;
import com.dinu.listin.Database.ListItem;
import com.dinu.listin.Database.LocatListDataSource;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends ViewModel {

    private MutableLiveData<List<ListItem>> mutableLiveDataListItems;
    private  CompositeDisposable compositeDisposable;
    private ListDataSource listDataSource;

    public ListViewModel() {
        compositeDisposable=new CompositeDisposable();
    }

    public void initListDataSource(Context context){
        listDataSource=new LocatListDataSource(ListDatabase.getInstance(context).itemDAO());
    }

    public void onStop(){
        compositeDisposable.clear();
    }

    public MutableLiveData<List<ListItem>> getMutableLiveDataListItems() {
        if (mutableLiveDataListItems == null){
            mutableLiveDataListItems=new MutableLiveData<>();
            getAllListItems();
        }
        return mutableLiveDataListItems;
    }

    private void getAllListItems() {
        compositeDisposable.add(listDataSource.getAllItem(Common.currentUser.getUid()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ListItem>>() {
                               @Override
                               public void accept(List<ListItem> listItems) throws Exception {
                                   mutableLiveDataListItems.setValue(listItems);
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   mutableLiveDataListItems.setValue(null);
                               }
                           }

                ));
    }
}