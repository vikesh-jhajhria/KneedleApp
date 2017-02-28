package com.kneedleapp.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aman.sharma on 2/21/2017.
 */

public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FeedItemVo> mList;
    private int count = 1;
    private ViewHolder mHolder;
    private MainActivity mActivityContext;
    private FeedItemListener mListener;

    public interface FeedItemListener{
        public void getItem(int position, ViewHolder holder);
    }

    public FeedItemAdapter(Context context, ArrayList<FeedItemVo> mList, MainActivity mActivityContext,FeedItemListener mListener) {
        this.context = context;
        this.mList = mList;
        this.mActivityContext = mActivityContext;
        this.mListener  = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_feed_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mHolder = holder;
        FeedItemVo feedItemVo = mList.get(position);
        holder.tvTitle.setText(feedItemVo.getmUserTitle());
        holder.tvSubTitle.setText(feedItemVo.getmUserSubTitle());
        holder.tvDescription.setText(feedItemVo.getmDesciption());
        holder.tvLikes.setText(feedItemVo.getmLikes());
        Log.e("imageurl", "" + feedItemVo.getmUserImage());
        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if (count % 2 == 0) {
                    holder.imgHeart.setImageResource(R.drawable.heart);
                } else {
                    holder.imgHeart.setImageResource(R.drawable.heart_unselected);
                }
            }
        });
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int popupWidth = 300;//ViewGroup.LayoutParams.WRAP_CONTENT;
                int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                View popupView = LayoutInflater.from(context).inflate(R.layout.menu_popup, null);
                PopupWindow attachmentPopup = new PopupWindow(context);
                attachmentPopup.setFocusable(true);
                attachmentPopup.setWidth(popupWidth);
                attachmentPopup.setHeight(popupHeight);
                attachmentPopup.setContentView(popupView);
                attachmentPopup.setBackgroundDrawable(new BitmapDrawable());
                attachmentPopup.showAsDropDown(view, -5, 0);

            }
        });
        holder.imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.getItem(position,holder);

            }
        });

        Picasso.with(context).load(feedItemVo.getmUserImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(holder.imgUser);
        Picasso.with(context).load(feedItemVo.getmContentImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(holder.imgContent);

        Utils.setTypeface(context, holder.tvTitle, Config.CENTURY_GOTHIC);
        Utils.setTypeface(context, holder.tvSubTitle, Config.CENTURY_GOTHIC);
        Utils.setTypeface(context, holder.tvDescription, Config.CENTURY_GOTHIC);
    }



    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvSubTitle, tvDescription, tvLikes;
        public ImageView imgUser, imgHeart, imgMenu;
        public ViewGroup transitionsContainer;
        public ImageView imgContent;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.textview_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.textview_sub_title);
            tvDescription = (TextView) itemView.findViewById(R.id.textview_description);
            tvLikes = (TextView) itemView.findViewById(R.id.textview_likes);
            imgUser = (ImageView) itemView.findViewById(R.id.imageview_user);
            imgContent = (ImageView) itemView.findViewById(R.id.imageview_content);
            imgHeart = (ImageView) itemView.findViewById(R.id.imageview_like);
            imgMenu = (ImageView) itemView.findViewById(R.id.imageview_menu);
            transitionsContainer = (ViewGroup) itemView.findViewById(R.id.ll_container);
        }
    }




}
