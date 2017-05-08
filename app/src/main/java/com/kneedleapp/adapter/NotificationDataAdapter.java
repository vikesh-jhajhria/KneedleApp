package com.kneedleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.FeedDetailActivity;
import com.kneedleapp.ProfileActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.NotificationItemVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by aman.sharma on 2/22/2017.
 */

public class NotificationDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyHeaderAdapter<NotificationDataAdapter.HeaderHolder> {

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
        Date date = null;
        long headerId = 0;
        try {
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            date = curFormater.parse(mList.get(position).getTime());
            headerId = Long.parseLong(date.getDate()+""+date.getMonth()+date.getYear());
        } catch (Exception e) {}

        //Log.e("HEADER = ",headerId+"");
        return headerId;
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
        Log.e("KNEEDLE", "position=" + position + " date=" + obj.getTime());


        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = curFormater.parse(mList.get(position).getTime());
            holder.mTvHeader.setText(simpleDate.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notification_item, parent, false);
        NotificationDataViewHolder holder = new NotificationDataViewHolder(view);
        Utils.setTypeface(context, holder.mTvNoti, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(context, holder.username, Config.CENTURY_GOTHIC_BOLD);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final NotificationItemVo obj = mList.get(position);
        if (obj != null) {
            if (obj.getType() == BaseActivity.NotificationType.LIKE) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(" liked your post.");
            } else if (obj.getType() == BaseActivity.NotificationType.COMMENT) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(" commented : " + obj.getComment());
            } else if (obj.getType() == BaseActivity.NotificationType.FOLLOW) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(" started following you");
            } else if (obj.getType() == BaseActivity.NotificationType.TAGGED) {
                ((NotificationDataViewHolder) holder).mTvNoti.setText(" tagged you in a post.");
            }
            ((NotificationDataViewHolder) holder).username.setText(obj.getUsername());

            Log.v("Img url", "position:" + position+" url="+obj.getImgUser());
            if (!obj.getImgUser().isEmpty()) {
                Glide.with(context).load(obj.getImgUser()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(((NotificationDataViewHolder) holder).imgUser);
            } else Log.v("Img url", "position:" + position);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (obj.getType() == BaseActivity.NotificationType.LIKE  || obj.getType() == BaseActivity.NotificationType.COMMENT
                        || obj.getType() == BaseActivity.NotificationType.TAGGED) {
                    context.startActivity(new Intent(getApplicationContext(), FeedDetailActivity.class)
                            .putExtra("FEEDID",obj.getFeedId())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (obj.getType() == BaseActivity.NotificationType.FOLLOW) {
                    context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                            .putExtra("USER_ID",obj.getUserId())
                            .putExtra("USER_NAME", obj.getUsername())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            }
        });
        ((NotificationDataViewHolder) holder).imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",obj.getUserId())
                        .putExtra("USER_NAME", obj.getUsername())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        ((NotificationDataViewHolder) holder).username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",obj.getUserId())
                        .putExtra("USER_NAME", obj.getUsername())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

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
        private TextView mTvNoti, username;
        public ImageView imgUser;

        public NotificationDataViewHolder(View itemView) {
            super(itemView);

            mTvNoti = (TextView) itemView.findViewById(R.id.txt_notification);
            username = (TextView) itemView.findViewById(R.id.txt_username);
            imgUser = (ImageView) itemView.findViewById(R.id.imgcircle_user);

        }
    }


}
