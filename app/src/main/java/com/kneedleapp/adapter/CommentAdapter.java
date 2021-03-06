package com.kneedleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kneedleapp.ProfileActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CommentVo;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final CommentVo obj = mList.get(position);
        holder.userName.setText(obj.getUserName());
        holder.userComment.setText(Utils.makeSpannable(mContext, obj.getmComment()));
        holder.time.setText(Utils.getTimeDifference(obj.getmDate()));
        holder.userComment.setMovementMethod(LinkMovementMethod.getInstance());
        holder.userComment.setHighlightColor(Color.TRANSPARENT);
        holder.imgUser.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_feed));
        if(!obj.getmUserImageUrl().isEmpty()) {
            Glide.with(mContext).load(Config.USER_IMAGE_URL + obj.getmUserImageUrl()).asBitmap().placeholder(R.drawable.default_feed)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            holder.imgUser.setImageBitmap(resource);
                        }
                    });
        }
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mContext.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",obj.getUserId())
                        .putExtra("USER_NAME", obj.getUserName())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",obj.getUserId())
                        .putExtra("USER_NAME", obj.getUserName())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, userComment, time;
        private ImageView imgUser;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.txt_user_name);
            userComment = (TextView) itemView.findViewById(R.id.txt_comment);
            time = (TextView) itemView.findViewById(R.id.txt_time);
            imgUser = (ImageView) itemView.findViewById(R.id.img_circle_comment);

            Utils.setTypeface(mContext, userName, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(mContext, userComment, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(mContext, time, Config.CENTURY_GOTHIC_REGULAR);
        }
    }
}
