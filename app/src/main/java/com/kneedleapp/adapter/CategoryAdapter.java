package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.fragment.CategoriesFragment;
import com.kneedleapp.utils.Config;
import com.kneedleapp.vo.CategoryVo;

import java.util.ArrayList;

/**
 * Created by aman.sharma on 3/20/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<CategoryVo> mList;
    private String data;
    private Sender sender;


    public interface Sender {
        public void sendData(String data);

    }


    public CategoryAdapter(Context mContext, ArrayList<CategoryVo> mList, CategoriesFragment fragment) {
        this.mContext = mContext;
        this.mList = mList;
        sender = fragment;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.items_categories, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        CategoryVo categoryVo = mList.get(position);
        holder.tvCategoryName.setText(categoryVo.getmCategoryName());

        if (holder.checkBox.isChecked()) {
            data = mList.get(position).getmCategoryName();
            Log.e("TAG", data);

        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = (TextView) itemView.findViewById(R.id.txt_category_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_category);


        }
    }


}
