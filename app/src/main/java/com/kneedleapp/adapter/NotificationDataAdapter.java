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

import java.util.ArrayList;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

/**
 * Created by aman.sharma on 2/22/2017.
 */

public class NotificationDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderAdapter<NotificationDataAdapter.HeaderHolder>{

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
        Log.v("DAY",position+" : "+obj.getType());
        if(obj.getType() == BaseActivity.NotificationType.HEADER){
            return 1;
        }
        return 0;
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.notificationheader, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int position) {
        NotificationItemVo obj = mList.get(position);
        ((HeaderHolder) holder).mTvHeader.setText(obj.getTime());
        /*if(obj.getmTvHeader() != null && obj.getmTvHeader().equalsIgnoreCase("today")){
            ((HeaderHolder) holder).bell.setVisibility(View.VISIBLE);
        } else {
            ((HeaderHolder) holder).bell.setVisibility(View.GONE);
        }*/
        Utils.setTypeface(context, ((HeaderHolder) holder).mTvHeader , Config.CENTURY_GOTHIC_BOLD);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        /*switch (viewType) {
            case NotificationItemVo.DAY:

                view = mInflater.inflate(R.layout.notificationheader, parent, false);
                return new HeaderHolder(view);

            case NotificationItemVo.NOTIFICATION:*/

                view = mInflater.inflate(R.layout.notification_item, parent, false);
                return new NotificationDataViewHolder(view);


        //}


        //return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NotificationItemVo obj = mList.get(position);
        if (obj != null) {

            /*switch (obj.getmType()) {
                case NotificationItemVo.DAY:
                    ((HeaderHolder) holder).mTvHeader.setText(obj.getmTvHeader());
                    if(obj.getmTvHeader().equalsIgnoreCase("today")){
                        ((HeaderHolder) holder).bell.setVisibility(View.VISIBLE);
                    } else {
                        ((HeaderHolder) holder).bell.setVisibility(View.GONE);
                    }
                    Utils.setTypeface(context, ((HeaderHolder) holder).mTvHeader , Config.CENTURY_GOTHIC_BOLD);
                    break;

                case NotificationItemVo.NOTIFICATION:*/
                    ((NotificationDataViewHolder) holder).mTvNoti.setText(obj.getFullName());
                    Utils.setTypeface(context, ((NotificationDataViewHolder) holder).mTvNoti , Config.CENTURY_GOTHIC_REGULAR);

                   /* break;

            }*/


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
        /*if (mList != null) {

            NotificationItemVo notificationItemVo = mList.get(position);
            if (notificationItemVo != null) {

                return notificationItemVo.getmType();
            }
        }*/
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

        public NotificationDataViewHolder(View itemView) {
            super(itemView);

            mTvNoti = (TextView) itemView.findViewById(R.id.txt_notification);

        }
    }


}
