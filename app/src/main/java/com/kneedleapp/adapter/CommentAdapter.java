package com.kneedleapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.R;
import com.kneedleapp.fragment.ProfileFragment;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CommentVo;

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

        final CommentVo obj = mList.get(position);
        holder.userName.setText(obj.getUserName());
        holder.userComment.setText(Utils.makeSpannable(mContext, obj.getmComment()));
        holder.userComment.setMovementMethod(LinkMovementMethod.getInstance());
        holder.userComment.setHighlightColor(Color.TRANSPARENT);
        Glide.with(mContext).load(Config.USER_IMAGE_URL + obj.getmUserImageUrl()).placeholder(R.drawable.profile_img).error(R.drawable.profile_img).into(holder.imgUser);

        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) mContext).addFragment(R.id.main_frame,
                        ProfileFragment.newInstance(obj.getUserId(),
                                obj.getUserName()), "PROFILE_FRAGMENT", true);
            }
        });
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) mContext).addFragment(R.id.main_frame,
                        ProfileFragment.newInstance(obj.getUserId(),
                                obj.getUserName()), "PROFILE_FRAGMENT", true);
            }
        });
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

            Utils.setTypeface(mContext, userName, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(mContext, userComment, Config.CENTURY_GOTHIC_REGULAR);
        }
    }
}
