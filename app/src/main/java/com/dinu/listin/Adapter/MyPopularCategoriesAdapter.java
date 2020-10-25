package com.dinu.listin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dinu.listin.CallBack.IRecyclerClickListner;
import com.dinu.listin.EventBus.PoplarCategoryClick;
import com.dinu.listin.Model.PopularCategory;
import com.dinu.listin.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPopularCategoriesAdapter extends RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder> {

    Context context;
    List<PopularCategory> popularCategoriesList;

    public MyPopularCategoriesAdapter(Context context, List<PopularCategory> popularCategories) {
        this.context = context;
        this.popularCategoriesList = popularCategories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_categories,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(popularCategoriesList.get(position).getImage()).into(holder.category_image);
        holder.txt_ctegory_name.setText(popularCategoriesList.get(position).getName());
        holder.setListner(new IRecyclerClickListner() {
            @Override
            public void onItemClickListner(View view, int pos) {
               // Toast.makeText(context, ""+popularCategoriesList.get(pos).getName(), Toast.LENGTH_SHORT).show();
                EventBus.getDefault().postSticky(new PoplarCategoryClick(popularCategoriesList.get(pos)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularCategoriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Unbinder unbinder;

        @BindView(R.id.txt_ctegory_name)
        TextView txt_ctegory_name;
        @BindView(R.id.category_image)
        CircleImageView category_image;

        IRecyclerClickListner listner;

        public void setListner(IRecyclerClickListner listner) {
            this.listner = listner;
        }

        public MyViewHolder (View itemView){
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            listner.onItemClickListner(view,getAdapterPosition());
        }

    }
}
