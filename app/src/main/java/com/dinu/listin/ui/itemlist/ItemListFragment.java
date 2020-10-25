package com.dinu.listin.ui.itemlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.dinu.listin.Adapter.MyItemListAdapter;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Model.Item;
import com.dinu.listin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ItemListFragment extends Fragment {

    private ItemListViewModel itemListViewModel;

    Unbinder unbinder;
    @BindView(R.id.reclycler_item_list)
    RecyclerView reclycler_item_list;
    LayoutAnimationController layoutAnimationController;
    MyItemListAdapter itemListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemListViewModel= ViewModelProviders.of(this).get(ItemListViewModel.class);
        View root=inflater.inflate(R.layout.fragment_item_list,container,false);
        unbinder= ButterKnife.bind(this,root);
        initViews();
        itemListViewModel.getMutableLiveDataItemList().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                itemListAdapter = new MyItemListAdapter(getContext(),items);
                reclycler_item_list.setAdapter(itemListAdapter);
                reclycler_item_list.setLayoutAnimation(layoutAnimationController);
            }
        });
        return root;
    }

    private void initViews() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.categorySelected.getName());
        reclycler_item_list.setHasFixedSize(true);
        reclycler_item_list.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
    }
}
