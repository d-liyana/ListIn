package com.dinu.listin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dinu.listin.CallBack.IRecyclerClickListner;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Database.ListDataSource;
import com.dinu.listin.Database.ListDatabase;
import com.dinu.listin.Database.ListItem;
import com.dinu.listin.Database.LocatListDataSource;
import com.dinu.listin.EventBus.CounterItemEvent;
import com.dinu.listin.EventBus.ItemClick;
import com.dinu.listin.Model.Item;
import com.dinu.listin.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyItemListAdapter extends RecyclerView.Adapter<MyItemListAdapter.MyViewHolder> {

    private Context context;
    private List<Item> itemList;
    private CompositeDisposable compositeDisposable;
    private ListDataSource listDataSource;

    public MyItemListAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.compositeDisposable = new CompositeDisposable();
        this.listDataSource = new LocatListDataSource(ListDatabase.getInstance(context).itemDAO());

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("OnCreate","ViewHolder");
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.i("Position"," "+position);
        Glide.with(context).load(itemList.get(position).getImage()).into(holder.img_item_image);
        holder.txt_item_price.setText(new StringBuilder("$").append(itemList.get(position).getPrice()));
        holder.txt_item_name.setText(new StringBuilder(" ").append(itemList.get(position).getName()));

        //Event
        holder.setiRecyclerClickListner((view, pos) -> {
            Common.selectedItem=  itemList .get(pos);
            EventBus.getDefault().postSticky(new ItemClick(true,itemList.get(pos)));

        });

        holder.img_quick_cart.setOnClickListener(v -> {
            ListItem listItem=new ListItem();
            listItem.setUid(Common.currentUser.getUid());
            listItem.setUserPhone(Common.currentUser.getPhone());

            listItem.setItemId(itemList.get(position).getId());
            listItem.setItemName(itemList.get(position).getName());
            listItem.setItemImage(itemList.get(position).getImage());
            listItem.setItemPrice(Double.valueOf(String.valueOf(itemList.get(position).getPrice())));
            listItem.setItemQuantity(1);
            listItem.setItemExtraPrice(0.0);
            listItem.setItemSize("Default ");

            Log.i("currentUser.getUid()"+Common.currentUser.getUid(),"listItem.getItemId()"+listItem.getItemId());
            Log.i("listItem.getItemSize()"+listItem.getItemSize(),"listItem.getItemId()"+listItem.getItemId());
            Log.i("Query"+listDataSource.getItemListWithSize(Common.currentUser.getUid(),listItem.getItemId(),listItem.getItemSize())
                    ,"listItem.getItemId()"+listItem.getItemId());

            listDataSource.getItemListWithSize(Common.currentUser.getUid(),listItem.getItemId(),listItem.getItemSize())
                    .subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new SingleObserver<ListItem>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(ListItem listItemFromDB) {
                            Log.i("onSuccess","listItemFromDB"+listItemFromDB.getItemSize());
                            Log.i("onSuccess","listItemFromDB"+listItemFromDB.getItemExtraPrice());
                            Log.i("onSuccess","listItemFromDB"+listItemFromDB.getItemQuantity());

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
                                                Toast.makeText(context, "LIST UPATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(context, "[UPDATE LIST ERROR]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else{
                                compositeDisposable.add(listDataSource.insertOrReplaceAll(listItem).subscribeOn(Schedulers.io()).
                                        observeOn(AndroidSchedulers.mainThread()).
                                        subscribe(()->{
                                            Toast.makeText(context, "Add to list success", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                },throwable -> {
                                            Toast.makeText(context, "[LIST ERROR]", Toast.LENGTH_SHORT).show();
                                        }));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("OnError","e.getMessage().contains"+e.getMessage());
                            if (e.getMessage().contains("empty")){
                                Log.i("OnError","e.getMessage() "+e.getMessage());

                                compositeDisposable.add(listDataSource.insertOrReplaceAll(listItem).subscribeOn(Schedulers.io()).
                                        observeOn(AndroidSchedulers.mainThread()).
                                        subscribe(()->{
                                            Toast.makeText(context, "Add to list success", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                        },throwable -> {
                                            Toast.makeText(context, "[LIST ERROR]", Toast.LENGTH_SHORT).show();
                                        }));
                            }
                            //Toast.makeText(context, "[LIST ERROR]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.text_item_name)
        TextView txt_item_name;
        @BindView(R.id.text_item_price)
        TextView txt_item_price;
        @BindView(R.id.img_item_image)
        ImageView img_item_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_quick_cart;

        IRecyclerClickListner iRecyclerClickListner;

        public void setiRecyclerClickListner(IRecyclerClickListner iRecyclerClickListner) {
            this.iRecyclerClickListner = iRecyclerClickListner;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            iRecyclerClickListner.onItemClickListner(v,getAdapterPosition());
        }
    }
}
