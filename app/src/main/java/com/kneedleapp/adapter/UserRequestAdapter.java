package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.vo.UserRequestVo;

import java.util.ArrayList;

/**
 * Created by aman.sharma on 3/1/2017.
 */

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserRequestVo> mList;


    public UserRequestAdapter(Context context, ArrayList<UserRequestVo> mList) {

        this.context = context;
        this.mList = mList;

    }


    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.follow_request_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgUserImage;
        private TextView mTvUserId, mUserName, mUserWork;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgUserImage = (ImageView) itemView.findViewById(R.id.img_request_user);
            mTvUserId = (TextView) itemView.findViewById(R.id.txt_request_userid);
            mUserName = (TextView) itemView.findViewById(R.id.txt_user_requestname);
            mUserWork = (TextView) itemView.findViewById(R.id.txt_user_requestwork);
        }
    }
}
