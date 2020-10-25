package com.dinu.listin.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.dinu.listin.Adapter.MyBestDealsAdapter;
import com.dinu.listin.Adapter.MyPopularCategoriesAdapter;
import com.dinu.listin.CallBack.IPopularCallBackListner;
import com.dinu.listin.Model.PopularCategory;
import com.dinu.listin.R;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Unbinder unbinder;

    @BindView(R.id.recycler_ppular)
    RecyclerView recycler_ppular;

    @BindView(R.id.viewpager)
    LoopingViewPager viewPager;

    LayoutAnimationController layoutAnimationController;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        init();

        homeViewModel.getPopularCatList().observe(getViewLifecycleOwner(),popularCategories ->{
            MyPopularCategoriesAdapter adapter=new MyPopularCategoriesAdapter(getContext(),popularCategories);
            recycler_ppular.setAdapter(adapter);
            recycler_ppular.setLayoutAnimation(layoutAnimationController);

                }
        );

        homeViewModel.getBestDealList().observe(getViewLifecycleOwner(),bestDeals -> {
            Log.i("here","home");
            MyBestDealsAdapter bestDealsAdapter=new MyBestDealsAdapter(getContext(),bestDeals,true);
            viewPager.setAdapter(bestDealsAdapter);

        });
        return root;

    }

    private void init() {
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        recycler_ppular.setHasFixedSize(true);
        recycler_ppular.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        viewPager.pauseAutoScroll();
        super.onPause();
    }
}
