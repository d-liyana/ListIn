package com.dinu.listin.ui.menu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

import com.dinu.listin.Adapter.MyCategoryAdapter;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Common.SpacesItemDecoration;
import com.dinu.listin.R;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;


    Unbinder unbinder;
    @BindView(R.id.reclycler_menu)
    RecyclerView recyclerMenu;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyCategoryAdapter myCategoryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        unbinder= ButterKnife.bind(this,root);
        initViews();


        menuViewModel.getErrorMsg().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        menuViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(),categoryList -> {
            dialog.dismiss();
            Log.i("CatList","CL "+categoryList);
            myCategoryAdapter = new MyCategoryAdapter(getContext(),categoryList);
            recyclerMenu.setAdapter(myCategoryAdapter);
            recyclerMenu.setLayoutAnimation(layoutAnimationController);
        });

        return root;
    }

    private void initViews() {
        Log.i("CatList","initViews");

        // dialog = new SpotsDialog().Builder().setContext(getContext()).setCanceble(false).build();
        dialog= new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (myCategoryAdapter != null){
                    switch (myCategoryAdapter.getItemViewType(position)){
                        case Common.DEFAUL_COLUMN_COUNT : return 1;
                        case Common.FULL_WIDTH_COLUMN: return 2;
                        default:return -1;
                    }
                }
                return -1;
            }
        });

        recyclerMenu.setLayoutManager(layoutManager);
        recyclerMenu.addItemDecoration(new SpacesItemDecoration(8));
    }
}
