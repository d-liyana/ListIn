package com.dinu.listin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.dinu.listin.Common.Common;
import com.dinu.listin.Database.ListDataSource;
import com.dinu.listin.Database.ListDatabase;
import com.dinu.listin.Database.LocatListDataSource;
import com.dinu.listin.EventBus.BestDealItemClick;
import com.dinu.listin.EventBus.CategoryClick;
import com.dinu.listin.EventBus.CounterItemEvent;
import com.dinu.listin.EventBus.ItemClick;
import com.dinu.listin.EventBus.PoplarCategoryClick;
import com.dinu.listin.Model.Category;
import com.dinu.listin.Model.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


import io.reactivex.schedulers.Schedulers;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private  DrawerLayout drawer;
    private NavController navController;

    private ListDataSource listDataSource;

    android.app.AlertDialog dialog;

    @BindView(R.id.fab)
    CounterFab fab;

    @Override
    protected void onResume() {
        super.onResume();
        counterListItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        ButterKnife.bind(this);
        listDataSource =new LocatListDataSource(ListDatabase.getInstance(this).itemDAO());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_list);

            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_menu,R.id.nav_item_details,R.id.nav_item_list,R.id.nav_list)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();


        View headerView=navigationView.getHeaderView(0);
        TextView txt_user=(TextView) headerView.findViewById(R.id.txt_user);
        Common.setSpanString("Hello ",Common.currentUser.getName(),txt_user);

        counterListItem();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        switch (item.getItemId()){
            case  R.id.nav_home:
                navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_menu:
                navController.navigate(R.id.nav_menu);
                break;
            case R.id.nav_list:
                navController.navigate(R.id.nav_list);
                break;
            case R.id.nav_view_orders:
                navController.navigate(R.id.nav_view_orders);
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
        }
        return true;
    }

    private void signOut() {
        Log.i("inSigOut","LogOut");
        Toast.makeText(this, "SignOut", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        Log.i("AlertBuilder","LogOut");

        builder.setTitle("Logout").setMessage("Do you really want to logout?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Log.i("inSigOut","LogOut");

                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Log.i("OK","LogOut");

                Common.selectedItem=null;
                Common.categorySelected=null;
                Common.currentUser=null;
                FirebaseAuth.getInstance().signOut();

                Intent intent=new Intent(Home.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //EventBus


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)

    public void onCategorySelected(CategoryClick event){
        if (event.isSuccess()){
            navController.navigate(R.id.nav_item_list);
           // Toast.makeText(this, "Click to "+event, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onItemClick(ItemClick event){
        if (event.isSuccess()){

            navController.navigate(R.id.nav_item_details);
            // Toast.makeText(this, "Click to "+event, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onListCounter(CounterItemEvent event){
        if (event.isSuccess()){
            counterListItem();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onBestDealItemClick(BestDealItemClick event){
        if (event.getBestDeal() != null ){
            dialog.show();
            Log.i("onBestDealItemClick","");
            FirebaseDatabase.getInstance().getReference("Category")
                    .child(event.getBestDeal().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("onDataChange","event.getBestDeal().getItem_id()"+event.getBestDeal().getItem_id());
                            if (dataSnapshot.exists()){
                                Common.categorySelected=dataSnapshot.getValue(Category.class);
                                FirebaseDatabase.getInstance().getReference("Category")
                                        .child(event.getBestDeal().getMenu_id())
                                        .child("items")
                                        .orderByChild("id")
                                        .equalTo(event.getBestDeal().getItem_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    for (DataSnapshot itemSnapShot:dataSnapshot.getChildren()){
                                                        Common.selectedItem = itemSnapShot.getValue(Item.class);
                                                    }
                                                    dialog.dismiss();
                                                    navController.navigate(R.id.nav_item_list);

                                                }else {
                                                    dialog.dismiss();
                                                    Toast.makeText(Home.this, "Item does not exit", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialog.dismiss();
                                                Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }else {
                                dialog.dismiss();
                                Toast.makeText(Home.this, "Item does not exit", Toast.LENGTH_SHORT).show();
                            }
                            
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPopularCategoryItemClick(PoplarCategoryClick event){
        if (event.getPopularCategory() != null ){
            dialog.show();
            FirebaseDatabase.getInstance().getReference("Category")
                    .child(event.getPopularCategory().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Common.categorySelected=dataSnapshot.getValue(Category.class);
                                FirebaseDatabase.getInstance().getReference("Category")
                                        .child(event.getPopularCategory().getMenu_id())
                                        .child("items")
                                        .orderByChild("id")
                                        .equalTo(event.getPopularCategory().getItem_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    for (DataSnapshot itemSnapShot:dataSnapshot.getChildren()){
                                                        Common.selectedItem = itemSnapShot.getValue(Item.class);
                                                    }
                                                    dialog.dismiss();
                                                    navController.navigate(R.id.nav_item_list);

                                                }else {
                                                    dialog.dismiss();
                                                    Toast.makeText(Home.this, "Item does not exit", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialog.dismiss();
                                                Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }else {
                                dialog.dismiss();
                                Toast.makeText(Home.this, "Item does not exit", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }


    private void counterListItem() {
        Log.i("Common.currentUser","currentUser.getUid()"+Common.currentUser.getUid());
        listDataSource.countItemList(Common.currentUser.getUid()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                
            }

            @Override
            public void onSuccess(Integer integer) {
                fab.setCount(integer);
            }

            @Override
            public void onError(Throwable e) {
                if (!e.getMessage().contains("query returned empty")){
                    Toast.makeText(Home.this, "[COUNT LIST]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    fab.setCount(0 );
                }

            }
        });
    }
}
