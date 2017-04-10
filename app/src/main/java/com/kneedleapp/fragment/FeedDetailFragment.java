package com.kneedleapp.fragment;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.kneedleapp.BaseActivity;
import com.kneedleapp.FullImageViewActivity;
import com.kneedleapp.KneedleApp;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.R.id.img_comment_down_arrow;
import static com.kneedleapp.utils.Config.fragmentManager;


public class FeedDetailFragment extends BaseFragment {
    private TextView title;
    public TextView tvTitle, tvSubTitle, tvDescription, tvLikes, tvComment, comment1, comment2;
    public ImageView imgUser, imgHeart, imgMenu, comment, share, arrow;
    public ViewGroup transitionsContainer;
    public ImageView imgContent;
    public CardView card1, card2;
    private String feedId;
    private static final String FEEDID = "FEEDID";
    private FeedItemVo feedItemVo;

    public static FeedDetailFragment newInstance(String feedId) {
        FeedDetailFragment fragment = new FeedDetailFragment();
        Bundle args = new Bundle();
        args.putString(FEEDID, feedId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedId = getArguments().getString(FEEDID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_detail, container, false);
        title = (TextView) view.findViewById(R.id.txt_title);
        tvTitle = (TextView) view.findViewById(R.id.textview_title);
        tvSubTitle = (TextView) view.findViewById(R.id.textview_sub_title);
        tvDescription = (TextView) view.findViewById(R.id.textview_description);
        tvLikes = (TextView) view.findViewById(R.id.textview_likes);
        tvComment = (TextView) view.findViewById(R.id.tv_comments);
        comment1 = (TextView) view.findViewById(R.id.txt_comment_1);
        comment2 = (TextView) view.findViewById(R.id.txt_comment_2);
        imgUser = (ImageView) view.findViewById(R.id.imageview_user);
        imgContent = (ImageView) view.findViewById(R.id.imageview_content);
        imgHeart = (ImageView) view.findViewById(R.id.imageview_like);
        comment = (ImageView) view.findViewById(R.id.img_comment);
        share = (ImageView) view.findViewById(R.id.img_share);
        imgMenu = (ImageView) view.findViewById(R.id.imageview_menu);
        transitionsContainer = (ViewGroup) view.findViewById(R.id.ll_container);
        card1 = (CardView) view.findViewById(R.id.card_comment_1);
        card2 = (CardView) view.findViewById(R.id.card_comment_2);
        arrow = (ImageView) view.findViewById(img_comment_down_arrow);

        applyFonts();


        view.findViewById(R.id.img_back).setOnClickListener(this);

        if (Utils.isNetworkConnected(getActivity(), true)) {
            getFeedData(feedId);
        }
        return view;
    }

    @Override
    public void onClick(View mView) {
        super.onClick(mView);
        switch (mView.getId()) {

        }
    }

    private void applyFonts() {
        Utils.setTypeface(getActivity(), tvTitle, Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), tvSubTitle, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), tvDescription, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), tvLikes, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), tvComment, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), comment1, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), comment2, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), title, Config.CENTURY_GOTHIC_REGULAR);
    }


    @Override
    public void onResume() {
        super.onResume();

        MainActivity.isPost = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(FeedDetailFragment.this);
            }
        }, 500);

    }

    private void bindData() {

        tvTitle.setText(feedItemVo.getmFullName());
        tvSubTitle.setText(feedItemVo.getmUserName());
        tvDescription.setText(feedItemVo.getmDescription());
        tvComment.setText(feedItemVo.getmCommentCount() + "");
        if (feedItemVo.getmCommentCount() > 0) {
            arrow.setVisibility(View.VISIBLE);
        } else {
            arrow.setVisibility(View.GONE);
        }
        tvLikes.setText(feedItemVo.getmLikes() + "");
        //comments
        if (feedItemVo.getmComment_1().isEmpty()) {
            card1.setVisibility(View.GONE);
        } else {
            card1.setVisibility(View.VISIBLE);
            comment1.setText(feedItemVo.getmComment_1());
        }
        if (feedItemVo.getmComment_2().isEmpty()) {
            card2.setVisibility(View.GONE);
        } else {
            card2.setVisibility(View.VISIBLE);
            comment2.setText(feedItemVo.getmComment_2());
        }
        if (feedItemVo.getLiked()) {
            imgHeart.setImageResource(R.drawable.heart);
        } else {
            imgHeart.setImageResource(R.drawable.heart_unselected);
        }
        Log.e("imageurl", "" + feedItemVo.getmUserImage());
        imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.setBounceEffect(imgHeart);
                if (Utils.isNetworkConnected(getActivity(), true)) {
                    if (!feedItemVo.getLiked()) {
                        imgHeart.setImageResource(R.drawable.heart);
                        tvLikes.setText((feedItemVo.getmLikes() + 1) + "");
                    } else {
                        imgHeart.setImageResource(R.drawable.heart_unselected);
                        tvLikes.setText((feedItemVo.getmLikes() - 1) + "");
                    }
                    if (Utils.isNetworkConnected(getActivity(), true)) {
                        likeFeed(AppPreferences.getAppPreferences(getActivity()).getUserId(),
                                feedItemVo.getmId());
                    }
                }
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int popupWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
                int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_popup, null);

                Utils.setTypeface(getActivity(), (TextView) popupView.findViewById(R.id.txt_report), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(getActivity(), (TextView) popupView.findViewById(R.id.txt_delete), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(getActivity(), (TextView) popupView.findViewById(R.id.txt_block), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(getActivity(), (TextView) popupView.findViewById(R.id.txt_share_fb), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(getActivity(), (TextView) popupView.findViewById(R.id.txt_tweet), Config.CENTURY_GOTHIC_REGULAR);

                final PopupWindow attachmentPopup = new PopupWindow(getActivity());
                attachmentPopup.setFocusable(true);
                attachmentPopup.setWidth(popupWidth);
                attachmentPopup.setHeight(popupHeight);
                attachmentPopup.setContentView(popupView);
                attachmentPopup.setBackgroundDrawable(new BitmapDrawable());
                attachmentPopup.showAsDropDown(view, -5, 0);
                if (AppPreferences.getAppPreferences(getActivity()).getStringValue(AppPreferences.USER_ID).equalsIgnoreCase(feedItemVo.getmUserId())) {
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
                        delete(feedItemVo.getmId());
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
        imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FullImageViewActivity.class);
                intent.putExtra("USERNAME", feedItemVo.getmFullName());
                intent.putExtra("IMAGE", feedItemVo.getmContentImage());
                intent.putExtra("USERIMAGE", feedItemVo.getmUserImage());
                intent.putExtra("LIKES", feedItemVo.getmLikes());
                intent.putExtra("LIKEDORNOT", feedItemVo.getLiked());
                startActivity(intent);
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCommentFragment fragment = AddCommentFragment.newInstance(feedItemVo.getmId());
                fragmentManager.beginTransaction()
                        .add(R.id.main_frame, fragment, "ADDCOMMENT_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });

        Glide.with(getActivity()).load(feedItemVo.getmUserImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(imgUser);
        Glide.with(getActivity()).load(feedItemVo.getmContentImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(imgContent);


        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).addFragment(R.id.main_frame,
                        ProfileFragment.newInstance(feedItemVo.getmUserId(),
                                feedItemVo.getmUserName()), "PROFILE_FRAGMENT", true);

            }
        });


        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).addFragment(R.id.main_frame,
                        ProfileFragment.newInstance(feedItemVo.getmUserId(),
                                feedItemVo.getmUserName()), "PROFILE_FRAGMENT", true);
            }
        });

    }

    private void getFeedData(final String feedId) {
        ((BaseActivity) getActivity()).showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_FEED_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                JSONArray jsonArray = jObject.getJSONArray("data");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                feedItemVo = new FeedItemVo();
                                feedItemVo.setmFullName(jsonObject.getString("fullname"));
                                feedItemVo.setmId(jsonObject.getString("id"));
                                feedItemVo.setmUserId(jsonObject.getString("user_id"));
                                feedItemVo.setmDate(jsonObject.getString("date"));
                                feedItemVo.setmUserName(jsonObject.getString("username"));
                                feedItemVo.setmUserImage(Config.USER_IMAGE_URL + jsonObject.getString("mypic"));
                                feedItemVo.setmContentImage(Config.FEED_IMAGE_URL + jsonObject.getString("image"));
                                feedItemVo.setmDescription(jsonObject.getString("caption"));
                                feedItemVo.setmLikes(jsonObject.getInt("likes_count"));
                                feedItemVo.setmCommentCount(jsonObject.getInt("comment_count"));
                                feedItemVo.setmComment_1(jsonObject.getString("comment_1"));
                                feedItemVo.setmComment_2(jsonObject.getString("comment_2"));
                                feedItemVo.setLiked(jsonObject.getString("likes_status").equals("1"));
                                bindData();

                            } else {
                                Toast.makeText(getActivity(), jObject.getString("status_msg"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("feed_id", feedId);
                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(getActivity());
        feedqueue.add(requestFeed);

    }

    //Like
    public void likeFeed(final String userId, final String feedId) {

        //((MainActivity) context).showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.ADD_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("KNEEDLE", "Response : " + response.toString());
                        //((MainActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {

                                if (feedItemVo.getLiked()) {
                                    feedItemVo.setLiked(false);
                                    feedItemVo.setmLikes(feedItemVo.getmLikes() - 1);
                                    imgHeart.setImageResource(R.drawable.heart_unselected);
                                    tvLikes.setText(feedItemVo.getmLikes() + "");
                                } else {
                                    feedItemVo.setLiked(true);
                                    feedItemVo.setmLikes(feedItemVo.getmLikes() + 1);
                                    imgHeart.setImageResource(R.drawable.heart);
                                    tvLikes.setText(feedItemVo.getmLikes() + "");
                                }
                            } else {
                                Toast.makeText(getActivity(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
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
                    Log.v("KNEEDLE", "Params : " + params.toString());
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
        ((BaseActivity) getActivity()).showProgessDialog();
        StringRequest requestReportProblem = new StringRequest(Request.Method.POST, Config.REPORT_PROBLEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);

                            } else {
                                Toast.makeText(getActivity(), "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("feed_id", feedId);
                params.put("user_id", AppPreferences.getAppPreferences(getActivity()).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        requestReportProblem.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(requestReportProblem);
    }

    public void block(final String userId) {
        ((BaseActivity) getActivity()).showProgessDialog();
        StringRequest block = new StringRequest(Request.Method.POST, Config.BLOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);

                            }
                            Toast.makeText(getActivity(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("friend_user_id", userId);
                params.put("user_id", AppPreferences.getAppPreferences(getActivity()).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        block.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(block);
    }

    public void delete(final String feedId) {
        ((BaseActivity) getActivity()).showProgessDialog();
        StringRequest delete = new StringRequest(Request.Method.POST, Config.DELETE_FEED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                fragmentManager.popBackStack();
                            }
                            Toast.makeText(getActivity(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("feed_id", feedId);
                params.put("user_id", AppPreferences.getAppPreferences(getActivity()).getStringValue(AppPreferences.USER_ID));


                return params;
            }
        };

        delete.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(delete);
    }
}

