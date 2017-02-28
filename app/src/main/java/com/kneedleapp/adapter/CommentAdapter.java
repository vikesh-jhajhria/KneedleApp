package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.vo.CommentVo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aman.sharma on 2/23/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CommentVo> mList;


    public CommentAdapter(Context mContext, ArrayList<CommentVo> mList) {
        this.mContext = mContext;
        this.mList = mList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_recyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CommentVo obj = mList.get(position);
        holder.userName.setText(obj.getmUserName());
        holder.userComment.setText(obj.getmDescription());
        //Picasso.with(mContext).load(obj.getmImageUrl()).placeholder(R.drawable.shahrukhkhan).error(R.drawable.shahrukhkhan).into(holder.imgUser);


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, userComment;
        private ImageView imgUser;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.txt_user_name);
            userComment = (TextView) itemView.findViewById(R.id.txt_comment);
            imgUser = (ImageView) itemView.findViewById(R.id.img_circle_comment);


        }
    }


}
