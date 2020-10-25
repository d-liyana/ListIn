package com.dinu.listin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dinu.listin.Database.ListItem;
import com.dinu.listin.EventBus.UpdateItemList;
import com.dinu.listin.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {

    Context context;
    List<ListItem> listItemList;

    public MyListAdapter(Context context, List<ListItem> listItemList) {
        this.context = context;
        this.listItemList = listItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_display_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(listItemList.get(position).getItemImage()).into(holder.img_list);
        holder.txt_item_name.setText(new StringBuilder(listItemList.get(position).getItemName()));
        holder.txt_item_price.setText(new StringBuilder("").append(listItemList.get(position).getItemPrice()+
                listItemList.get(position).getItemExtraPrice()));
        holder.number_button.setNumber(String.valueOf(listItemList.get(position).getItemQuantity()));
        holder.number_button.setOnValueChangeListener((view, oldValue, newValue) -> {
            listItemList.get(position).setItemQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemList(listItemList.get(position)));
        });


    }

    @Override
    public int getItemCount() {
        return listItemList.size();
    }

    public ListItem getItemAtPosition(int pos) {
        return listItemList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;

        @BindView(R.id.img_list)
        ImageView img_list;
        @BindView(R.id.txt_item_name)
        TextView txt_item_name;
        @BindView(R.id.txt_item_price)
        TextView txt_item_price;
        @BindView(R.id.number_button)
        ElegantNumberButton number_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);

        }
    }
}