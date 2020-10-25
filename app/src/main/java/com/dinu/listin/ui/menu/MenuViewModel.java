package com.dinu.listin.ui.menu;

import android.util.Log;

import com.dinu.listin.CallBack.ICategoryCallBackListner;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.Category;
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

public class MenuViewModel extends ViewModel implements ICategoryCallBackListner {

    private MutableLiveData<List<Category>> categoryListMutable;
    private  MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private ICategoryCallBackListner iCategoryCallBackListner;

    public MenuViewModel() {
        iCategoryCallBackListner=this;
    }

    public MutableLiveData<List<Category>> getCategoryListMutable() {
       if (categoryListMutable == null){
           categoryListMutable = new MutableLiveData<>();
           errorMsg = new MutableLiveData<>();
           loadCategories();
       }
       return categoryListMutable;
    }

    private void loadCategories() {
        Log.i("AmHere","MenuViewHolder");
        List<Category> tempList= new ArrayList<>();
        DatabaseReference catRef = FirebaseDatabase.getInstance().getReference(Common.CAT_REF);
        catRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("WithinThe","onDataChange");
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    Log.i("TempList","temp"+tempList);
                    Log.i("TempList","temp"+tempList);

                    Category category=itemSnapshot.getValue(Category.class);
                    category.setMenuid(itemSnapshot.getKey());
                    tempList.add(category);
                }
                    iCategoryCallBackListner.onCategoryLoadSuccess(tempList );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iCategoryCallBackListner.onCategoryLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    @Override
    public void onCategoryLoadSuccess(List<Category> categoryList) {
        Log.i("LoadedSuccess","onCategoryLoadSuccess"+categoryList);
        categoryListMutable.setValue(categoryList);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
            errorMsg.setValue(message);
    }
}