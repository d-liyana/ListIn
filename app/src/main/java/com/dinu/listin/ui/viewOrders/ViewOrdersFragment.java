package com.dinu.listin.ui.viewOrders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dinu.listin.Adapter.MyOrdersAdapter;
import com.dinu.listin.CallBack.ILoadOrderCallbackListner;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.Order;
import com.dinu.listin.R;
import com.dinu.listin.ui.menu.MenuViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListner {

    private ViewOrdersModel viewOrdersModel;
    private ILoadOrderCallbackListner listner;

    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    AlertDialog alertDialog;

    Unbinder unbinder;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersModel =
                ViewModelProviders.of(this).get(ViewOrdersModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);
        unbinder= ButterKnife.bind(this,root);

        loadOrdersFromFirebase();
        initViews (root);

        viewOrdersModel.getMutableLiveDataOrders().observe(getViewLifecycleOwner(),orders -> {
            MyOrdersAdapter adapter=new MyOrdersAdapter(getContext(),orders);
            recycler_orders.setAdapter(adapter);
        });

        return root;
    }

    private void loadOrdersFromFirebase() {
        List<Order> orderList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF).orderByChild("userId")
        .equalTo(Common.currentUser.getUid()).limitToLast(100)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot orderDataSnapshot : dataSnapshot.getChildren()){
                    Order order=orderDataSnapshot.getValue(Order.class);
                    order.setOrderNumber(orderDataSnapshot.getKey());
                    orderList.add(order);
                }
                listner.onLoadOrderSuccess(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listner.onLoadOrderFail(databaseError.getMessage());

            }
        });

    }

    private void initViews(View root) {
        listner=this;
        alertDialog=new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager linearLayout= new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(linearLayout);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(),linearLayout.getOrientation()));
    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        alertDialog.dismiss();
        viewOrdersModel.setMutableLiveDataOrders(orderList);

    }

    @Override
    public void onLoadOrderFail(String message) {
        alertDialog.dismiss();
        Toast.makeText(getContext(), "Fail "+message, Toast.LENGTH_SHORT).show();

    }
}
