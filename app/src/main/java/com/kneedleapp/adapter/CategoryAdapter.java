package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kneedleapp.R;
import com.kneedleapp.RegistrationActivity;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CategoryVo;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<CategoryVo> mList;
    private String data;
    private ArrayList<String> mListSelected;


    public interface Sender {
        public void sendData(ArrayList<String> data);

    }

    public CategoryAdapter(Context mContext, ArrayList<CategoryVo> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mListSelected = new ArrayList<>();


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.items_categories, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CategoryVo categoryVo = mList.get(position);
        holder.checkBox.setText(categoryVo.getmCategoryName());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(categoryVo.isChecked());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                categoryVo.setChecked(b);
                data = categoryVo.getmCategoryName().toString().trim();
                Log.e("Data", data);
                mListSelected.add(data);


                for (int i = 0; i < RegistrationActivity.mStoreList.size(); i++) {
                    CategoryVo obj = RegistrationActivity.mStoreList.get(i);


                    if (obj.getmCategoryName().trim().equalsIgnoreCase(categoryVo.getmCategoryName())) {
                        RegistrationActivity.mStoreList.remove(i);

                    }
                }
                if (b)
                    RegistrationActivity.mStoreList.add(categoryVo);

                Log.e("TAG", "" + mListSelected.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_category);
            Utils.setTypeface(mContext, checkBox, Config.CENTURY_GOTHIC_REGULAR);
        }
    }
}
