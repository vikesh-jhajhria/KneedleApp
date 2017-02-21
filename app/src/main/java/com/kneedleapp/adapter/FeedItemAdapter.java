package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.vo.FeedItemVo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aman.sharma on 2/21/2017.
 */

public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FeedItemVo> mList;

    public FeedItemAdapter(Context context, ArrayList<FeedItemVo> mList) {
        this.context = context;
        this.mList = mList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_feed_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FeedItemVo feedItemVo = mList.get(position);
        holder.tvTitle.setText(feedItemVo.getmUserTitle());
        holder.tvSubTitle.setText(feedItemVo.getmUserSubTitle());
        holder.tvDescription.setText(feedItemVo.getmDesciption());
        holder.tvLikes.setText(feedItemVo.getmLikes());
        Log.e("imageurl", "" + feedItemVo.getmUserImage());

        Picasso.with(context).load(feedItemVo.getmUserImage()).placeholder(R.drawable.shahrukhkhan).error(R.drawable.shahrukhkhan).into(holder.imgUser);
        Picasso.with(context).load(feedItemVo.getmContentImage()).placeholder(R.drawable.shahrukhkhan).error(R.drawable.shahrukhkhan).into(holder.imgContent);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvSubTitle, tvDescription, tvLikes;
        private ImageView imgUser, imgContent;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.textview_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.textview_sub_title);
            tvDescription = (TextView) itemView.findViewById(R.id.textview_description);
            tvLikes = (TextView) itemView.findViewById(R.id.textview_likes);
            imgUser = (ImageView) itemView.findViewById(R.id.imageview_user);
            imgContent = (ImageView) itemView.findViewById(R.id.imageview_content);

        }
    }


}
