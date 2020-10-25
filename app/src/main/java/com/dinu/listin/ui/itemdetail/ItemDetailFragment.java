package com.dinu.listin.ui.itemdetail;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Database.ListDataSource;
import com.dinu.listin.Database.ListDatabase;
import com.dinu.listin.Database.ListItem;
import com.dinu.listin.Database.LocatListDataSource;
import com.dinu.listin.EventBus.CounterItemEvent;
import com.dinu.listin.Model.Item;
import com.dinu.listin.Model.Size;
import com.dinu.listin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ItemDetailFragment extends Fragment {

    private ItemDetailViewModel itemDetailViewModel;
    private Unbinder unbinder;

    private ListDataSource listDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // private Context context;


    @BindView(R.id.img_item)
    ImageView img_item;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.item_description)
    TextView item_description;
    @BindView(R.id.item_price)
    TextView item_price;
    @BindView(R.id.number_btn)
    ElegantNumberButton numberButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowComment;
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;



    @OnClick(R.id.btnCart)
    void onItemListAdd(){

        Log.i("currentUser.getUid() "+Common.currentUser.getUid(),"listItem.getItemId() "+Common.currentUser.getUid());
        Log.i("listItem.getItemSize() "+Common.selectedItem.getName(),"listItem.getItemId() "+Double.valueOf(String.valueOf(Common.selectedItem.getPrice())));


        ListItem listItem=new ListItem();
        listItem.setUid(Common.currentUser.getUid());
        listItem.setUserPhone(Common.currentUser.getPhone());

        listItem.setItemId(Common.selectedItem.getId());
        listItem.setItemName(Common.selectedItem.getName());
        listItem.setItemImage(Common.selectedItem.getImage());
        listItem.setItemPrice(Double.valueOf(String.valueOf(Common.selectedItem.getPrice())));
        listItem.setItemQuantity(Integer.valueOf(numberButton.getNumber()));
        listItem.setItemExtraPrice(Common.calculateExtraPrice(Common.selectedItem.getUserSelectedSize()));

        if (Common.selectedItem.getUserSelectedSize() != null)
            listItem.setItemSize(new Gson().toJson(Common.selectedItem.getUserSelectedSize()));
        else{
            listItem.setItemSize("Default ");

        }

        Log.i("currentUser.getUid()"+Common.currentUser.getUid(),"listItem.getItemId()"+listItem.getItemId());
        Log.i("listItem.getItemSize()"+listItem.getItemSize(),"listItem.getItemId()"+listItem.getItemId());

        listDataSource.getItemListWithSize(Common.currentUser.getUid(),listItem.getItemId(),listItem.getItemSize())
                .subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new SingleObserver<ListItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ListItem listItemFromDB) {

                        Log.i("withinOnsuccess","b4If");
                        if (listItemFromDB.equals(listItem)){
                            listItemFromDB.setItemExtraPrice(listItem.getItemExtraPrice());
                            listItemFromDB.setItemSize(listItem.getItemSize());
                            listItemFromDB.setItemQuantity(listItem.getItemQuantity() + listItem.getItemQuantity());
                            listDataSource.updatItemList(listItemFromDB).subscribeOn(Schedulers.io()).
                                    observeOn(AndroidSchedulers.mainThread()).
                                    subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            Toast.makeText(getContext(), "LIST UPATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "[UPDATE LIST ERROR]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            compositeDisposable.add(listDataSource.insertOrReplaceAll(listItem).subscribeOn(Schedulers.io()).
                                    observeOn(AndroidSchedulers.mainThread()).
                                    subscribe(()->{
                                        Toast.makeText(getContext(), "Add to list success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                    },throwable -> {
                                        Toast.makeText(getContext(), "[LIST ERROR]", Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("inFail","kkhkh");
                        if (e.getMessage().contains("empty")){
                            compositeDisposable.add(listDataSource.insertOrReplaceAll(listItem).subscribeOn(Schedulers.io()).
                                    observeOn(AndroidSchedulers.mainThread()).
                                    subscribe(()->{
                                        Toast.makeText(getContext(), "Add to list success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                    },throwable -> {
                                        Toast.makeText(getContext(), "[LIST ERROR]", Toast.LENGTH_SHORT).show();
                                    }));
                        }
                        Toast.makeText(getContext(), "[CART ERROR]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        {
            itemDetailViewModel =
                    ViewModelProviders.of(this).get(ItemDetailViewModel.class);
            View root = inflater.inflate(R.layout.fragment_item_detail, container, false);
            unbinder = ButterKnife.bind(this, root);
            initViews();
            itemDetailViewModel.getMutableLiveDataItem().observe(getViewLifecycleOwner(), item -> {
                    displayItem(item);
            });

            return root;
        }
    }

    private void initViews() {
        listDataSource = new LocatListDataSource(ListDatabase.getInstance(getContext()).itemDAO());

    }

    private void displayItem(Item item) {

        Glide.with(getContext()).load(item.getImage()).into(img_item);
        item_name.setText(new StringBuilder(item.getName()));
        item_description.setText(new StringBuilder(item.getDescription()));
        item_price.setText(new StringBuilder(item.getPrice().toString()));

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.selectedItem.getName());

        //Size
        for (Size size:Common.selectedItem.getSize()){
            RadioButton radioButton=new RadioButton(getContext());
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if(isChecked){
                    Common.selectedItem.setUserSelectedSize(size);
                    calculateTotalPrice();
                }
            });

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1.0f);
            radioButton.setLayoutParams(params);
            radioButton.setText(size.getName());
            radioButton.setTag(size.getPrice());

            rdi_group_size.addView(radioButton);
        }

        if(rdi_group_size.getChildCount() > 0){
            RadioButton radioButton=(RadioButton)rdi_group_size.getChildAt(0);
            radioButton.setChecked(true);
        }

        calculateTotalPrice();

    }

    private  void calculateTotalPrice() {
        double totalPrice=Double.parseDouble(Common.selectedItem.getPrice().toString()),displayPrice=0.0;
        //Size
        totalPrice+=Double.parseDouble(Common.selectedItem.getUserSelectedSize().getPrice().toString());
        displayPrice=totalPrice * (Integer.parseInt(numberButton.getNumber()));
        displayPrice=Math.round(displayPrice * 100.00/100.00);
        item_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString() );
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}