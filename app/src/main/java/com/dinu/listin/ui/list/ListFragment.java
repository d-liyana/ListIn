package com.dinu.listin.ui.list;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.dinu.listin.Adapter.MyListAdapter;
import com.dinu.listin.CallBack.ILoadTimeFormatFirebaseListner;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Common.MySwipeHelper;
import com.dinu.listin.Database.ListDataSource;
import com.dinu.listin.Database.ListDatabase;
import com.dinu.listin.Database.ListItem;
import com.dinu.listin.Database.LocatListDataSource;
import com.dinu.listin.EventBus.CounterItemEvent;
import com.dinu.listin.EventBus.UpdateItemList;
import com.dinu.listin.Model.Order;
import com.dinu.listin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ListFragment extends Fragment implements ILoadTimeFormatFirebaseListner {

    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    private ListViewModel listViewModel;
    private ListDataSource listDataSource;

    private ILoadTimeFormatFirebaseListner iLoadTimeFormatFirebaseListner;

    public Parcelable recyclerViewState;


    @BindView(R.id.reclycler_list)
    RecyclerView reclycler_list;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;
    @BindView(R.id.txt_empty_list)
    TextView txt_empty_list ;


     @OnClick(R.id.btn_send_list)
     void onSendListClick() {

         compositeDisposable.add(listDataSource.getAllItem(Common.currentUser.getUid())
         .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
         .subscribe(listItems -> {
             listDataSource.sumPriceInList(Common.currentUser.getUid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new SingleObserver<Double>() {
                         @Override
                         public void onSubscribe(Disposable d) {

                         }

                         @Override
                         public void onSuccess(Double totalPrice) {
                             double finalPrice=totalPrice;
                             Order order=new Order();

                             order.setUserId(Common.currentUser.getUid());
                             order.setUserName(Common.currentUser.getName());
                             order.setUserPhone(Common.currentUser.getPhone());
                             order.setListItems(listItems);
                             order.setTotalpayment(totalPrice);

                             syncLoadTimeWithGlobal(order);

                            // writeOrderToFirebase(order);
                         }

                         @Override
                         public void onError(Throwable e) {
                             Toast.makeText(getContext(), " "+e.getMessage(), Toast.LENGTH_SHORT).show();

                         }
                     });


         }, throwable -> Toast.makeText(getContext(), " "+throwable.getMessage(), Toast.LENGTH_SHORT).show()
         ));
    }

    private void syncLoadTimeWithGlobal(Order order) {
         final DatabaseReference offSetRef=FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
         offSetRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 long offSet=dataSnapshot.getValue(Long.class);
                 long estimatedServerTimeMs=System.currentTimeMillis()+offSet;
                 SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                 Date resultdate= new Date(estimatedServerTimeMs);
                 Log.d("",""+sdf.format(resultdate));
                 iLoadTimeFormatFirebaseListner.onLoadTimeSuccess(order,estimatedServerTimeMs);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void writeOrderToFirebase(Order order) {
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF).child(Common.createOrderNumber())
                .setValue(order)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), " "+e.getMessage(), Toast.LENGTH_SHORT).show();

                }).addOnCompleteListener(task -> {
                    listDataSource.cleanList(Common.currentUser.getUid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    //Clean Success
                                    sumAllItemInList();
                                    EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                    Toast.makeText(getContext(), "Order sent successfull", Toast.LENGTH_SHORT).show();



                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getContext(), " "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }

    private Unbinder unbinder;
    private MyListAdapter adapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                ViewModelProviders.of(this).get(ListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        iLoadTimeFormatFirebaseListner=this;

        listViewModel.initListDataSource(getContext());
        listViewModel.getMutableLiveDataListItems().observe(getViewLifecycleOwner(), new Observer<List<ListItem>>() {
            @Override
            public void onChanged(List<ListItem> listItems) {
                if (listItems == null || listItems.isEmpty()){
                    reclycler_list.setVisibility(View.GONE);
                    group_place_holder.setVisibility(View.GONE);
                    txt_empty_list.setVisibility(View.VISIBLE);

                }else {
                    reclycler_list.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    txt_empty_list.setVisibility(View.GONE);

                    adapter=new MyListAdapter(getContext(),listItems);
                    reclycler_list.setAdapter(adapter);

                }
            }
        });
        unbinder= ButterKnife.bind(this,root);
        initVews();
        return root;
    }

    private void initVews() {

        setHasOptionsMenu(true );

        listDataSource=new LocatListDataSource(ListDatabase.getInstance(getContext()).itemDAO());
        reclycler_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        reclycler_list.setLayoutManager(layoutManager);
        reclycler_list.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipeHelper swipeHelper=new MySwipeHelper(getContext(),reclycler_list,200) {
            @Override
            public void initiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"DELETE",30,0, Color.parseColor("#FF3C30"),
                        pos -> {
                            ListItem listItem=adapter.getItemAtPosition(pos);
                            listDataSource.deleteItemList(listItem).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(new SingleObserver<Integer>() {
                                         @Override
                                         public void onSubscribe(Disposable d) {

                                         }

                                         @Override
                                         public void onSuccess(Integer integer) {
                                             adapter.notifyItemRemoved(pos);
                                             sumAllItemInList();
                                             EventBus.getDefault().postSticky(new CounterItemEvent(true));
                                             Toast.makeText(getContext(), "Successfully Delete the item", Toast.LENGTH_SHORT).show();

                                         }

                                         @Override
                                         public void onError(Throwable e) {
                                             Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                         }
                                     });


                        }));

            }
        };
        
        sumAllItemInList();
   }

    private void sumAllItemInList() {
        listDataSource.sumPriceInList(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        txt_total_price.setText(new StringBuilder() .append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_list){
            listDataSource.cleanList(Common.currentUser.getUid()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), "Successfully clear the cart" ,Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterItemEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {

        listViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this );
        compositeDisposable.clear();
        super.onStop();

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onUpdateItemInListEvent (UpdateItemList event){
        if (event.getListItem()!= null){

            recyclerViewState=reclycler_list.getLayoutManager().onSaveInstanceState();
           // listDataSource=new LocatListDataSource(ListDatabase.getInstance(getContext()).itemDAO());
            listDataSource.updatItemList(event.getListItem()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            reclycler_list.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "[UPDATE LIST]"+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        }
    }

    private void calculateTotalPrice() {
        listDataSource.sumPriceInList(Common.currentUser.getUid()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        txt_total_price.setText(new StringBuilder("Total: ").append(Common.formatPrice(price)));

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getContext(), "[SUM]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }


    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeMills) {
        order.setCreateDate(estimateTimeMills);
        writeOrderToFirebase(order);


    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), "Error "+message, Toast.LENGTH_SHORT).show();
    }
}
