package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.BaseActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.NotificationItemVo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

/**
 * Created by aman.sharma on 2/22/2017.
 */

public class NotificationDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderAdapter<NotificationDataAdapter.HeaderHolder> {

    private Context context;
    private ArrayList<NotificationItemVo> mList;
    private LayoutInflater mInflater;

    public NotificationDataAdapter(Context context, ArrayList<NotificationItemVo> mList) {
        this.context = context;
        this.mList = mList;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public long getHeaderId(int position) {
        NotificationItemVo obj = mList.get(position);
        Log.v("DAY", position + " : " + obj.getType());
        if (obj.getType() == BaseActivity.NotificationType.HEADER) {
            return 1;
        }
        return 0;
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.notificationheader, parent, false);
        HeaderHolder holder = new HeaderHolder(view);
        Utils.setTypeface(context, holder.mTvHeader, Config.CENTURY_GOTHIC_BOLD);
        return holder;
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int position) {
        NotificationItemVo obj = mList.get(position);
        holder.mTvHeader.setText(obj.getTime());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notification_item, parent, false);
        NotificationDataViewHolder holder = new NotificationDataViewHolder(view);
        Utils.setTypeface(context, holder.mTvNoti, Config.CENTURY_GOTHIC_REGULAR);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NotificationItemVo obj = mList.get(position);
        if (obj != null) {
            if (obj.getType() == BaseActivity.NotificationType.LIKE) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(obj.getUsername() + " liked your post.");
            } else if (obj.getType() == BaseActivity.NotificationType.COMMENT) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(obj.getUsername() + " commented : " + obj.getComment());
            } else if (obj.getType() == BaseActivity.NotificationType.FOLLOW) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(obj.getUsername() + " started following you");
            } else if (obj.getType() == BaseActivity.NotificationType.TAGGED) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(obj.getUsername() + " tagged you in a post.");
            }
            if(!obj.getImgUser().isEmpty()) {
                Picasso.with(context).load(obj.getImgUser()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(((NotificationDataViewHolder) holder).imgUser);
            }else Log.v("Img url","position:"+position);
        }

    }

    @Override
    public int getItemCount() {

        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView mTvHeader;
        private ImageView bell;

        public HeaderHolder(View itemView) {
            super(itemView);
            mTvHeader = (TextView) itemView.findViewById(R.id.txt_header);
            bell = (ImageView) itemView.findViewById(R.id.img_bell);

        }
    }

    public static class NotificationDataViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvNoti;
        private ImageView imgUser;

        public NotificationDataViewHolder(View itemView) {
            super(itemView);

            mTvNoti = (TextView) itemView.findViewById(R.id.txt_notification);
            imgUser = (ImageView) itemView.findViewById(R.id.img_notification);

        }
    }


}
