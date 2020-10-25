package com.dinu.listin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dinu.listin.CallBack.IRecyclerClickListner;
import com.dinu.listin.Common.Common;
import com.dinu.listin.EventBus.CategoryClick;
import com.dinu.listin.Model.Category;
import com.dinu.listin.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> {

    Context context;
    List<Category> categoryList;

    public MyCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.i("","categoryList.get(position).getImage()).into(holder.cat_img"+categoryList.get(position).getImage());
        Glide.with(context).load(categoryList.get(position).getImage()).into(holder.cat_img);
        holder.cat_txt.setText(new StringBuilder(categoryList.get(position).getName()));
        //Event
        holder.setListner((view, pos) -> {
            Common.categorySelected=categoryList.get(pos);
            EventBus.getDefault().postSticky(new CategoryClick(true,categoryList.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Unbinder unbinder;
        @BindView(R.id.img_category)
        ImageView cat_img;
        @BindView(R.id.txt_category)
        TextView cat_txt;

        IRecyclerClickListner listner;

        public void setListner(IRecyclerClickListner listner) {
            this.listner = listner;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            listner.onItemClickListner(view,getAdapterPosition());
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (categoryList.size()==1){

            return Common.DEFAUL_COLUMN_COUNT;
        }else{
            if (categoryList.size() %2 ==0) {

                return Common.DEFAUL_COLUMN_COUNT;
            }else{
                return (position > 1 && position == categoryList.size()-1)? Common.FULL_WIDTH_COLUMN :Common.DEFAUL_COLUMN_COUNT;
            }

        }
    }
}
