package com.kneedleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
import com.kneedleapp.vo.SearchResultVO;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.CheckViewHolder> {

    private Context context;
    private ArrayList<SearchResultVO> list;


    public SearchResultAdapter(ArrayList<SearchResultVO> list, Context context) {
        this.context = context;
        this.list = list;

    }

    @Override
    public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckViewHolder viewHolder;

        viewHolder = new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_search_result_item, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckViewHolder holder, final int position) {
        final SearchResultVO searchResultVo = list.get(position);
        holder.txt_name.setText(searchResultVo.getmUserName());
        holder.fullname.setText(searchResultVo.getmFullName());
        holder.job.setText(searchResultVo.getmProfileType());
        holder.place.setText(searchResultVo.getmCityName());
        holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.default_feed));

        if(!searchResultVo.getmImgUrl().isEmpty()) {
            Glide.with(context).load(Config.USER_IMAGE_URL + searchResultVo.getmImgUrl()).asBitmap().placeholder(R.drawable.default_feed)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            holder.img.setImageBitmap(resource);
                        }
                    });
        }
        holder.fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",searchResultVo.getmUserId())
                        .putExtra("USER_NAME", searchResultVo.getmUserName())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",searchResultVo.getmUserId())
                        .putExtra("USER_NAME", searchResultVo.getmUserName())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_name, fullname, job, place;
        ImageView img;


        public CheckViewHolder(final View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txt_username);
            fullname = (TextView) itemView.findViewById(R.id.txt_fullname);
            job = (TextView) itemView.findViewById(R.id.txt_profile_type);
            place = (TextView) itemView.findViewById(R.id.txt_address);
            img = (ImageView) itemView.findViewById(R.id.img_profile);


            Utils.setTypeface(context, txt_name, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, fullname, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, job, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, place, Config.CENTURY_GOTHIC_REGULAR);

        }

    }

}
