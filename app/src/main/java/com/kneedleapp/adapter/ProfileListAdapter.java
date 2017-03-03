package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kneedleapp.BaseActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.ListVo;

import java.util.ArrayList;


public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.CheckViewHolder> {

    Context context;
    ArrayList<ListVo> list;
    String viewType;


    public ProfileListAdapter(ArrayList<ListVo> list, Context context, String viewType) {
        this.context = context;
        this.list = list;
        this.viewType = viewType;
    }

    @Override
    public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckViewHolder viewHolder;
        if(this.viewType.trim().equalsIgnoreCase("LIST")){
            viewHolder=new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.list_feed_item, parent, false));
        }
        else{
            viewHolder=new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.item_stagger, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckViewHolder holder, final int position) {
       final ListVo checkVo = list.get(position);
        if(viewType.equalsIgnoreCase("GRID")) {
            holder.img.setImageResource(R.drawable.image);
            ViewGroup.LayoutParams lp = holder.img.getLayoutParams();
            lp.height = (int) Utils.getDeviceSize((BaseActivity) context).get("Width") / 3;
            holder.img.setLayoutParams(lp);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        ImageView img;



        public CheckViewHolder(final View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_view);




        }

    }

}
