package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.vo.NotificationItemVo;

import java.util.ArrayList;

/**
 * Created by aman.sharma on 2/22/2017.
 */

public class NotificationDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<NotificationItemVo> mList;

    public NotificationDataAdapter(Context context, ArrayList<NotificationItemVo> mList) {
        this.context = context;
        this.mList = mList;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case NotificationItemVo.DAY:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationheader, parent, false);
                return new DayViewHolder(view);

            case NotificationItemVo.NOTIFICATION:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
                return new NotificationDataViewHolder(view);


        }


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NotificationItemVo obj = mList.get(position);
        if (obj != null) {

            switch (obj.getmType()) {
                case NotificationItemVo.DAY:

                    ((DayViewHolder) holder).mTvHeader.setText(obj.getmTvHeader());

                    break;

                case NotificationItemVo.NOTIFICATION:
                    ((NotificationDataViewHolder) holder).mTvNoti.setText(obj.getmTvNotiText());
                    break;

            }


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
        if (mList != null) {

            NotificationItemVo notificationItemVo = mList.get(position);
            if (notificationItemVo != null) {

                return notificationItemVo.getmType();
            }
        }
        return 0;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvHeader;

        public DayViewHolder(View itemView) {
            super(itemView);
            mTvHeader = (TextView) itemView.findViewById(R.id.txt_header);

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
