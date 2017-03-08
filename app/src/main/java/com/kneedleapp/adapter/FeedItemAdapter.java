package com.kneedleapp.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kneedleapp.KneedleApp;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.fragment.AddCommentFragment;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;

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

    public interface FeedItemListener {
        public void getItem(int position, ViewHolder holder);
    }

    public FeedItemAdapter(Context context, ArrayList<FeedItemVo> mList, MainActivity mActivityContext, FeedItemListener mListener) {
        this.context = context;
        this.mList = mList;
        this.mActivityContext = mActivityContext;
        this.mListener = mListener;
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
        final FeedItemVo feedItemVo = mList.get(position);
        holder.tvTitle.setText(feedItemVo.getmUserTitle());
        holder.tvSubTitle.setText(feedItemVo.getmUserSubTitle());
        holder.tvDescription.setText(feedItemVo.getmDescription());
        holder.tvLikes.setText(feedItemVo.getmLikes());
        if(feedItemVo.getLiked()){
            holder.imgHeart.setImageResource(R.drawable.heart);
        } else {
            holder.imgHeart.setImageResource(R.drawable.heart_unselected);
        }
        Log.e("imageurl", "" + feedItemVo.getmUserImage());
        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.setBounceEffect(holder.imgHeart);
                if(Utils.isNetworkConnected(context,true)) {
                    likeFeed( "4",feedItemVo.getmFeedId(),Utils.getCurrentDate());
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
                mListener.getItem(position, holder);
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCommentFragment fragment = new AddCommentFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.main_frame, fragment, "ADDCOMMENT_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });

        Picasso.with(context).load(feedItemVo.getmUserImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(holder.imgUser);
        Picasso.with(context).load(feedItemVo.getmContentImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(holder.imgContent);


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvSubTitle, tvDescription, tvLikes, tvComment;
        public ImageView imgUser, imgHeart, imgMenu, comment, share;
        public ViewGroup transitionsContainer;
        public ImageView imgContent;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.textview_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.textview_sub_title);
            tvDescription = (TextView) itemView.findViewById(R.id.textview_description);
            tvLikes = (TextView) itemView.findViewById(R.id.textview_likes);
            tvComment = (TextView) itemView.findViewById(R.id.tv_comments);
            imgUser = (ImageView) itemView.findViewById(R.id.imageview_user);
            imgContent = (ImageView) itemView.findViewById(R.id.imageview_content);
            imgHeart = (ImageView) itemView.findViewById(R.id.imageview_like);
            comment = (ImageView) itemView.findViewById(R.id.img_comment);
            share = (ImageView) itemView.findViewById(R.id.img_share);
            imgMenu = (ImageView) itemView.findViewById(R.id.imageview_menu);
            transitionsContainer = (ViewGroup) itemView.findViewById(R.id.ll_container);

            Utils.setTypeface(context, tvTitle, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, tvSubTitle, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, tvDescription, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, tvLikes, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, tvComment, Config.CENTURY_GOTHIC_REGULAR);
        }
    }



    private void likeFeed(String userId, String feedId, String date) {
        ((MainActivity)context).showProgessDialog();

        try {
            final JSONObject jsonobj = new JSONObject();
            jsonobj.put("user_id", userId);
            jsonobj.put("feed_id", feedId);
            jsonobj.put("like_date", date);
            Log.e("params : ", "" + jsonobj);
            final JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    Config.ADD_LIKE, jsonobj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject json) {
                    ((MainActivity)context).dismissProgressDialog();
                    Log.e("response : ", "" + json);

                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                            ((MainActivity)context).dismissProgressDialog();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            KneedleApp.getInstance().addToRequestQueue(postRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
