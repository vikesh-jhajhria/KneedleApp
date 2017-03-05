package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.SearchResultVO;

import java.util.ArrayList;


public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.CheckViewHolder> {

    Context context;
    ArrayList<SearchResultVO> list;


    public FollowerAdapter(ArrayList<SearchResultVO> list, Context context) {
        this.context = context;
        this.list = list;

    }

    @Override
    public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckViewHolder viewHolder;

        viewHolder = new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_follow_item, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckViewHolder holder, final int position) {
        final SearchResultVO checkVo = list.get(position);
        holder.txt_name.setText(checkVo.Name);
        holder.fullname.setText(checkVo.fname);
        holder.job.setText(checkVo.job);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name, fullname, job;
        TextView follow, hide;
        ImageView img;


        public CheckViewHolder(final View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txt_username);
            fullname = (TextView) itemView.findViewById(R.id.txt_fullname);
            job = (TextView) itemView.findViewById(R.id.txt_designation);
            follow = (TextView) itemView.findViewById(R.id.btn_follow);
            hide = (TextView) itemView.findViewById(R.id.btn_hide);
            img = (ImageView) itemView.findViewById(R.id.img_profile);


            Utils.setTypeface(context, txt_name, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, fullname, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, job, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, follow, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, hide, Config.CENTURY_GOTHIC_BOLD);

        }

    }

}
