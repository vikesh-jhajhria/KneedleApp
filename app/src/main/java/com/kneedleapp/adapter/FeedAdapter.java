package com.kneedleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kneedleapp.AddCommentActivity;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.FeedDetailActivity;
import com.kneedleapp.KneedleApp;
import com.kneedleapp.ProfileActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    String viewType;
    private Context context;
    private ArrayList<FeedItemVo> mList;
    private ProfileItemListener mListener;
    private boolean isProfileFeed;

    public FeedAdapter(ArrayList<FeedItemVo> list, Context context, String viewType, ProfileItemListener mListener, boolean isProfileFeed) {
        this.context = context;
        this.mList = list;
        this.isProfileFeed = isProfileFeed;
        this.viewType = viewType;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (this.viewType.trim().equalsIgnoreCase("LIST")) {
            viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_feed_item, parent, false));
        } else {
            viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_stagger, parent, false));
        }
        return viewHolder;
    }


    public interface ProfileItemListener {
        public void getItem(int position, ViewHolder holder, boolean isLiked);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FeedItemVo feedItemVo = mList.get(position);
        holder.imgContent.setImageDrawable(null);
        holder.imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewType.equalsIgnoreCase("LIST")) {
                    mListener.getItem(position, holder, feedItemVo.getLiked());
                } else {
                    context.startActivity(new Intent(getApplicationContext(), FeedDetailActivity.class)
                            .putExtra("FEEDID",feedItemVo.getmId())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            }
        });
        Log.v("Image Loading", "URL: " + feedItemVo.getmContentImage());

        Glide.with(context).load(feedItemVo.getmContentImage()).centerCrop()
                .placeholder(R.drawable.default_feed).error(R.drawable.default_feed)
                .into(holder.imgContent);

        if (viewType.equalsIgnoreCase("GRID")) {
            ViewGroup.LayoutParams lp = holder.imgContent.getLayoutParams();
            lp.height = (int) Utils.getDeviceSize((BaseActivity) context).get("Width") / 3;
            holder.imgContent.setLayoutParams(lp);

        } else {
            holder.tvTitle.setText(feedItemVo.getmFullName());
            holder.tvSubTitle.setText(feedItemVo.getmUserName());
            holder.tvDescription.setText(Utils.makeSpannable(context, feedItemVo.getmDescription()));
            holder.tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            holder.tvDescription.setHighlightColor(Color.TRANSPARENT);

            holder.username1.setText(feedItemVo.getmUsername1());
            holder.username2.setText(feedItemVo.getmUsername2());

            holder.username1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                            .putExtra("USER_ID","")
                            .putExtra("USER_NAME",feedItemVo.getmUsername1())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            });
            holder.username2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                            .putExtra("USER_ID","")
                            .putExtra("USER_NAME",feedItemVo.getmUsername2())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            });
            if(feedItemVo.getmUsername1().isEmpty()){
                holder.username1.setVisibility(View.GONE);
            }else {
                holder.username1.setVisibility(View.VISIBLE);
            }
            if(feedItemVo.getmUsername2().isEmpty()){
                holder.username2.setVisibility(View.GONE);
            }else {
                holder.username2.setVisibility(View.VISIBLE);
            }
            holder.tvComment.setText(feedItemVo.getmCommentCount() + "");
            if (feedItemVo.getmCommentCount() > 0) {
                holder.itemView.findViewById(R.id.img_comment_down_arrow).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.img_comment_down_arrow).setVisibility(View.GONE);
            }
            holder.tvLikes.setText(feedItemVo.getmLikes() + "");
            //comments
            if (feedItemVo.getmComment_1().isEmpty()) {
                holder.card1.setVisibility(View.GONE);
            } else {
                holder.card1.setVisibility(View.VISIBLE);
                holder.comment1.setText(Utils.makeSpannable(context, feedItemVo.getmComment_1()));
                holder.comment1.setMovementMethod(LinkMovementMethod.getInstance());
                holder.comment1.setHighlightColor(Color.TRANSPARENT);
            }
            if (feedItemVo.getmComment_2().isEmpty()) {
                holder.card2.setVisibility(View.GONE);
            } else {
                holder.card2.setVisibility(View.VISIBLE);
                holder.comment2.setText(Utils.makeSpannable(context, feedItemVo.getmComment_2()));
                holder.comment2.setMovementMethod(LinkMovementMethod.getInstance());
                holder.comment2.setHighlightColor(Color.TRANSPARENT);
            }
            if (feedItemVo.getLiked()) {
                holder.imgHeart.setImageResource(R.drawable.heart);
            } else {
                holder.imgHeart.setImageResource(R.drawable.heart_unselected);
            }
            Log.e("imageurl", "" + feedItemVo.getmUserImage());
            holder.imgHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.setBounceEffect(holder.imgHeart);
                    if (Utils.isNetworkConnected(context, true)) {
                        if (!feedItemVo.getLiked()) {
                            holder.imgHeart.setImageResource(R.drawable.heart);
                            holder.tvLikes.setText((feedItemVo.getmLikes() + 1) + "");
                        } else {
                            holder.imgHeart.setImageResource(R.drawable.heart_unselected);
                            holder.tvLikes.setText((feedItemVo.getmLikes() - 1) + "");
                        }
                        if (Utils.isNetworkConnected(context, true)) {
                            likeFeed(AppPreferences.getAppPreferences(context).getUserId(),
                                    feedItemVo.getmId(), position);
                        }
                    }
                }
            });
            holder.imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int popupWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
                    int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                    View popupView = LayoutInflater.from(context).inflate(R.layout.menu_popup, null);

                    Utils.setTypeface(context, (TextView) popupView.findViewById(R.id.txt_report), Config.CENTURY_GOTHIC_REGULAR);
                    Utils.setTypeface(context, (TextView) popupView.findViewById(R.id.txt_delete), Config.CENTURY_GOTHIC_REGULAR);
                    Utils.setTypeface(context, (TextView) popupView.findViewById(R.id.txt_block), Config.CENTURY_GOTHIC_REGULAR);
                    Utils.setTypeface(context, (TextView) popupView.findViewById(R.id.txt_share_fb), Config.CENTURY_GOTHIC_REGULAR);
                    Utils.setTypeface(context, (TextView) popupView.findViewById(R.id.txt_tweet), Config.CENTURY_GOTHIC_REGULAR);


                    final PopupWindow attachmentPopup = new PopupWindow(context);
                    attachmentPopup.setFocusable(true);
                    attachmentPopup.setWidth(popupWidth);
                    attachmentPopup.setHeight(popupHeight);
                    attachmentPopup.setContentView(popupView);
                    attachmentPopup.setBackgroundDrawable(new BitmapDrawable());
                    attachmentPopup.showAsDropDown(view, -5, 0);
                    if (AppPreferences.getAppPreferences(context).getStringValue(AppPreferences.USER_ID).equalsIgnoreCase(feedItemVo.getmUserId())) {
                        popupView.findViewById(R.id.txt_delete).setVisibility(View.VISIBLE);
                        popupView.findViewById(R.id.txt_block).setVisibility(View.GONE);
                        popupView.findViewById(R.id.txt_report).setVisibility(View.GONE);
                    } else {
                        popupView.findViewById(R.id.txt_delete).setVisibility(View.GONE);
                        popupView.findViewById(R.id.txt_block).setVisibility(View.VISIBLE);
                        popupView.findViewById(R.id.txt_report).setVisibility(View.VISIBLE);
                    }


                    ((TextView) popupView.findViewById(R.id.txt_report)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reportProblem(feedItemVo.getmId());
                            attachmentPopup.dismiss();
                        }
                    });
                    ((TextView) popupView.findViewById(R.id.txt_block)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            block(feedItemVo.getmUserId());
                            attachmentPopup.dismiss();
                        }
                    });
                    ((TextView) popupView.findViewById(R.id.txt_delete)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            delete(feedItemVo.getmId(), position);
                            attachmentPopup.dismiss();
                        }
                    });
                    ((TextView) popupView.findViewById(R.id.txt_share_fb)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attachmentPopup.dismiss();
                        }
                    });
                    ((TextView) popupView.findViewById(R.id.txt_tweet)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attachmentPopup.dismiss();
                        }
                    });


                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(getApplicationContext(), AddCommentActivity.class)
                            .putExtra("FEEDID",feedItemVo.getmId())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            });
            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(getApplicationContext(), AddCommentActivity.class)
                            .putExtra("FEEDID",feedItemVo.getmId())
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            });
            Glide.with(context).load(feedItemVo.getmUserImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(holder.imgUser);
            holder.imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isProfileFeed) {
                        context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                                .putExtra("USER_ID",feedItemVo.getmUserId())
                                .putExtra("USER_NAME",feedItemVo.getmUserName())
                                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }

                }
            });


            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isProfileFeed) {
                        context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                                .putExtra("USER_ID",feedItemVo.getmUserId())
                                .putExtra("USER_NAME",feedItemVo.getmUserName())
                                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvSubTitle, tvDescription, tvLikes, tvComment, comment1, comment2, username1, username2;
        public ImageView imgUser, imgHeart, imgMenu, comment, share;
        public ViewGroup transitionsContainer;
        public ImageView imgContent;
        public CardView card1, card2;
        private LinearLayout ll_comment;

        public ViewHolder(View itemView) {
            super(itemView);
            imgContent = (ImageView) itemView.findViewById(R.id.imageview_content);
            if (viewType.equalsIgnoreCase("LIST")) {
                tvTitle = (TextView) itemView.findViewById(R.id.textview_title);
                tvSubTitle = (TextView) itemView.findViewById(R.id.textview_sub_title);
                tvDescription = (TextView) itemView.findViewById(R.id.textview_description);
                tvLikes = (TextView) itemView.findViewById(R.id.textview_likes);
                tvComment = (TextView) itemView.findViewById(R.id.tv_comments);
                comment1 = (TextView) itemView.findViewById(R.id.txt_comment_1);
                comment2 = (TextView) itemView.findViewById(R.id.txt_comment_2);
                username1 = (TextView) itemView.findViewById(R.id.txt_username_1);
                username2 = (TextView) itemView.findViewById(R.id.txt_username_2);
                imgUser = (ImageView) itemView.findViewById(R.id.imageview_user);

                imgHeart = (ImageView) itemView.findViewById(R.id.imageview_like);
                comment = (ImageView) itemView.findViewById(R.id.img_comment);
                share = (ImageView) itemView.findViewById(R.id.img_share);
                imgMenu = (ImageView) itemView.findViewById(R.id.imageview_menu);
                transitionsContainer = (ViewGroup) itemView.findViewById(R.id.ll_container);
                card1 = (CardView) itemView.findViewById(R.id.card_comment_1);
                card2 = (CardView) itemView.findViewById(R.id.card_comment_2);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_all_comment);

                Utils.setTypeface(context, tvTitle, Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(context, tvSubTitle, Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(context, tvDescription, Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(context, tvLikes, Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(context, tvComment, Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(context, comment1, Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(context, comment2, Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(context, username1, Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(context, username2, Config.CENTURY_GOTHIC_BOLD);
            }
        }
    }


    //Like
    public void likeFeed(final String userId, final String feedId, final int position) {

        //((MainActivity) context).showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.ADD_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Feed", "Response : " + response.toString());
                        //((MainActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                if (mList.get(position).getLiked()) {
                                    mList.get(position).setLiked(false);
                                    mList.get(position).setmLikes(mList.get(position).getmLikes() - 1);
                                } else {
                                    mList.get(position).setLiked(true);
                                    mList.get(position).setmLikes(mList.get(position).getmLikes() + 1);
                                }
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", userId);
                    params.put("feed_id", feedId);
                    params.put("like_date", Utils.getCurrentDate());
                    Log.v("Feed", "Params : " + params.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        requestLogin.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        KneedleApp.getInstance().addToRequestQueue(requestLogin);


    }


    public void reportProblem(final String feedId) {
        ((BaseActivity) context).showProgessDialog();
        StringRequest requestReportProblem = new StringRequest(Request.Method.POST, Config.REPORT_PROBLEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);

                            } else {
                                Toast.makeText(context, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("feed_id", feedId);
                params.put("user_id", AppPreferences.getAppPreferences(context).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        requestReportProblem.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(requestReportProblem);
    }

    public void block(final String userId) {
        ((BaseActivity) context).showProgessDialog();
        StringRequest block = new StringRequest(Request.Method.POST, Config.BLOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);

                            } else {
                                Toast.makeText(context, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("friend_user_id", userId);
                params.put("user_id", AppPreferences.getAppPreferences(context).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        block.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(block);
    }

    public void delete(final String feedId, final int position) {
        ((BaseActivity) context).showProgessDialog();
        StringRequest delete = new StringRequest(Request.Method.POST, Config.DELETE_FEED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                mList.remove(position);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("feed_id", feedId);
                params.put("user_id", AppPreferences.getAppPreferences(context).getStringValue(AppPreferences.USER_ID));


                return params;
            }
        };

        delete.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(delete);
    }
}

