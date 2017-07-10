package com.kneedleapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Pair;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.R.id.img_comment_down_arrow;

public class FeedDetailActivity extends BaseActivity {


    public TextView tvTitle, tvSubTitle, tvDescription, tvLikes, tvComment,
            comment1, comment2, username1, username2, time;
    public ImageView imgUser, imgHeart, imgMenu, comment, share, arrow;
    public ViewGroup transitionsContainer;
    public ImageView imgContent;
    public CardView card1, card2;
    private String feedId;
    private static final String FEEDID = "FEEDID";
    private FeedItemVo feedItemVo;
    private LinearLayout ll_comment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        CURRENT_PAGE = "FEED_DETAIL";
        feedId = getIntent().getExtras().getString(FEEDID);
        tvTitle = (TextView) findViewById(R.id.textview_title);
        tvSubTitle = (TextView) findViewById(R.id.textview_sub_title);
        tvDescription = (TextView) findViewById(R.id.textview_description);
        tvLikes = (TextView) findViewById(R.id.textview_likes);
        tvComment = (TextView) findViewById(R.id.tv_comments);
        comment1 = (TextView) findViewById(R.id.txt_comment_1);
        comment2 = (TextView) findViewById(R.id.txt_comment_2);
        time = (TextView) findViewById(R.id.txt_time);
        username1 = (TextView) findViewById(R.id.txt_username_1);
        username2 = (TextView) findViewById(R.id.txt_username_2);
        imgUser = (ImageView) findViewById(R.id.imageview_user);
        imgContent = (ImageView) findViewById(R.id.imageview_content);
        imgHeart = (ImageView) findViewById(R.id.imageview_like);
        comment = (ImageView) findViewById(R.id.img_comment);
        ll_comment = (LinearLayout) findViewById(R.id.ll_all_comment);
        imgMenu = (ImageView) findViewById(R.id.imageview_menu);
        transitionsContainer = (ViewGroup) findViewById(R.id.ll_container);
        card1 = (CardView) findViewById(R.id.card_comment_1);
        card2 = (CardView) findViewById(R.id.card_comment_2);
        arrow = (ImageView) findViewById(img_comment_down_arrow);

        applyFonts();


        findViewById(R.id.img_back).setOnClickListener(this);

        if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
            isLoading = true;
            getFeedData(feedId);
        }
    }

    private boolean isLoading;

    @Override
    public void onClick(View mView) {
        super.onClick(mView);
        switch (mView.getId()) {

        }
    }

    private void applyFonts() {
        Utils.setTypeface(FeedDetailActivity.this, tvTitle, Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(FeedDetailActivity.this, tvSubTitle, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, tvDescription, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, tvLikes, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, tvComment, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, comment1, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, comment2, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, time, Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(FeedDetailActivity.this, username1, Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(FeedDetailActivity.this, username2, Config.CENTURY_GOTHIC_BOLD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AddCommentActivity.feedItemVo != null && !isLoading) {
            feedItemVo = AddCommentActivity.feedItemVo;
            bindData();
        }

    }

    private void bindData() {
        AddCommentActivity.feedItemVo = feedItemVo;
        tvTitle.setText("@" + feedItemVo.getmUserName());
        String location = "";
        if (!feedItemVo.getCity().isEmpty()) {
            location = feedItemVo.getCity();
            if (!feedItemVo.getState().isEmpty()) {
                location = location + ", " + feedItemVo.getState();
            }
        } else if (!feedItemVo.getState().isEmpty()) {
            location = feedItemVo.getState();
        }
        tvSubTitle.setText(location);
        tvDescription.setText(Utils.makeSpannable(this, feedItemVo.getmDescription()));
        tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
        tvDescription.setHighlightColor(Color.TRANSPARENT);
        tvComment.setText(feedItemVo.getmCommentCount() + "");
        time.setText(Utils.getTimeDifference(feedItemVo.getmDate()));
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
            comment1.setText(Utils.makeSpannable(this, feedItemVo.getmComment_1()));
            comment1.setMovementMethod(LinkMovementMethod.getInstance());
            comment1.setHighlightColor(Color.TRANSPARENT);
        }
        if (feedItemVo.getmComment_2().isEmpty()) {
            card2.setVisibility(View.GONE);
        } else {
            card2.setVisibility(View.VISIBLE);
            comment2.setText(Utils.makeSpannable(this, feedItemVo.getmComment_2()));
            comment2.setMovementMethod(LinkMovementMethod.getInstance());
            comment2.setHighlightColor(Color.TRANSPARENT);
        }

        username1.setText(feedItemVo.getmUsername1());
        username2.setText(feedItemVo.getmUsername2());


        username1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID", "")
                        .putExtra("USER_NAME", feedItemVo.getmUsername1())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        username2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID", "")
                        .putExtra("USER_NAME", feedItemVo.getmUsername2())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        if (feedItemVo.getmUsername1().isEmpty()) {
            username1.setVisibility(View.GONE);
        } else {
            username1.setVisibility(View.VISIBLE);
        }
        if (feedItemVo.getmUsername2().isEmpty()) {
            username2.setVisibility(View.GONE);
        } else {
            username2.setVisibility(View.VISIBLE);
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
                if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                    if (!feedItemVo.getLiked()) {
                        imgHeart.setImageResource(R.drawable.heart);
                        tvLikes.setText((feedItemVo.getmLikes() + 1) + "");
                    } else {
                        imgHeart.setImageResource(R.drawable.heart_unselected);
                        tvLikes.setText((feedItemVo.getmLikes() - 1) + "");
                    }
                    if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                        likeFeed(AppPreferences.getAppPreferences(FeedDetailActivity.this).getUserId(),
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
                View popupView = LayoutInflater.from(FeedDetailActivity.this).inflate(R.layout.menu_popup, null);

                Utils.setTypeface(FeedDetailActivity.this, (TextView) popupView.findViewById(R.id.txt_report), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(FeedDetailActivity.this, (TextView) popupView.findViewById(R.id.txt_delete), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(FeedDetailActivity.this, (TextView) popupView.findViewById(R.id.txt_block), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(FeedDetailActivity.this, (TextView) popupView.findViewById(R.id.txt_share_fb), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(FeedDetailActivity.this, (TextView) popupView.findViewById(R.id.txt_tweet), Config.CENTURY_GOTHIC_REGULAR);

                final PopupWindow attachmentPopup = new PopupWindow(FeedDetailActivity.this);
                attachmentPopup.setFocusable(true);
                attachmentPopup.setWidth(popupWidth);
                attachmentPopup.setHeight(popupHeight);
                attachmentPopup.setContentView(popupView);
                attachmentPopup.setBackgroundDrawable(new BitmapDrawable());
                attachmentPopup.showAsDropDown(view, -5, 0);
                if (AppPreferences.getAppPreferences(FeedDetailActivity.this).getStringValue(AppPreferences.USER_ID).equalsIgnoreCase(feedItemVo.getmUserId())) {
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
                        if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                            reportProblem(feedItemVo.getmId());
                        }
                        attachmentPopup.dismiss();
                    }
                });
                ((TextView) popupView.findViewById(R.id.txt_block)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                            block(feedItemVo.getmUserId());
                        }
                        attachmentPopup.dismiss();
                    }
                });
                ((TextView) popupView.findViewById(R.id.txt_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                            delete(feedItemVo.getmId());
                        }
                        attachmentPopup.dismiss();
                    }
                });
                ((TextView) popupView.findViewById(R.id.txt_share_fb)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                            showProgessDialog();
                            ShareDialog shareDialog = new ShareDialog(FeedDetailActivity.this);
                            if (ShareDialog.canShow(ShareLinkContent.class)) {

                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentDescription(feedItemVo.getmDescription())
                                        .setContentUrl(Uri.parse(feedItemVo.getmContentImage()))
                                        .build();
                                shareDialog.show(linkContent);
                                dismissProgressDialog();
                            }
                        }
                        attachmentPopup.dismiss();
                    }
                });
                ((TextView) popupView.findViewById(R.id.txt_tweet)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utils.isNetworkConnected(FeedDetailActivity.this, true)) {
                            showProgessDialog();
                            Bitmap bitmap = ((BitmapDrawable) imgContent.getDrawable()).getBitmap();
                            String url = "";

                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    Config.IMAGE_DIRECTORY_NAME);

                            if (!mediaStorageDir.exists()) {
                                if (!mediaStorageDir.mkdirs()) {
                                    url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, feedItemVo.getmUserName(), "");
                                }
                            }

                            File pictureFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + ".jpg");
                            url = pictureFile.getAbsolutePath();
                            try {
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                                fos.close();
                            } catch (FileNotFoundException e) {
                                Log.d("KneedleApp", "File not found: " + e.getMessage());
                            } catch (IOException e) {
                                Log.d("KneedleApp", "Error accessing file: " + e.getMessage());
                            }

                            TweetComposer.Builder builder = null;

                            try {
                                builder = new TweetComposer.Builder(FeedDetailActivity.this)
                                        .image(Uri.parse(url))
                                        .text(feedItemVo.getmDescription());
                                builder.show();
                                dismissProgressDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        attachmentPopup.dismiss();
                    }
                });

            }
        });
        imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.fullScreenFeedBitmap = null;
                Config.fullScreenUserBitmap = null;
                BitmapDrawable feedDrawable = ((BitmapDrawable) imgContent.getDrawable());
                if (feedDrawable != null) {
                    Config.fullScreenFeedBitmap = feedDrawable.getBitmap();
                    BitmapDrawable userDrawable = ((BitmapDrawable) imgUser.getDrawable());
                    if (userDrawable != null) {
                        Config.fullScreenUserBitmap = userDrawable.getBitmap();
                    }
                    Intent intent = new Intent(FeedDetailActivity.this, FullImageViewActivity.class);
                    intent.putExtra("USERNAME", "@" + feedItemVo.getmUserName());
                    intent.putExtra("IMAGE", feedItemVo.getmContentImage());
                    intent.putExtra("USERIMAGE", feedItemVo.getmUserImage());
                    intent.putExtra("LIKES", feedItemVo.getmLikes());
                    intent.putExtra("LIKEDORNOT", feedItemVo.getLiked());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(FeedDetailActivity.this,
                                new Pair<View, String>(imgUser, "userimage"),
                                new Pair<View, String>(imgContent, "image"),
                                new Pair<View, String>(imgHeart, "heart"),
                                new Pair<View, String>(tvTitle, "title"),
                                new Pair<View, String>(tvLikes, "likes"));
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(FeedDetailActivity.this, "Image Loading", Toast.LENGTH_SHORT).show();
                }

            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCommentActivity.class)
                        .putExtra("FEEDID", feedItemVo.getmId())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            }
        });
        ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCommentActivity.class)
                        .putExtra("FEEDID", feedItemVo.getmId())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        //Glide.with(FeedDetailActivity.this).load(feedItemVo.getmUserImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(imgUser);
        //Glide.with(FeedDetailActivity.this).load(feedItemVo.getmContentImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(imgContent);
        imgContent.setImageResource(R.drawable.default_feed);
        if(!feedItemVo.getmContentImage().isEmpty()) {
            Glide.with(this).load(feedItemVo.getmContentImage()).asBitmap().placeholder(R.drawable.default_feed)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            imgContent.setImageBitmap(resource);
                        }
                    });
        }
        imgUser.setImageResource(R.drawable.default_feed);
        if(!feedItemVo.getmUserImage().isEmpty()) {
            Glide.with(this).load(feedItemVo.getmUserImage()).asBitmap().placeholder(R.drawable.default_feed)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            imgUser.setImageBitmap(resource);
                        }
                    });
        }
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID", feedItemVo.getmUserId())
                        .putExtra("USER_NAME", feedItemVo.getmUserName())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            }
        });


        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID", feedItemVo.getmUserId())
                        .putExtra("USER_NAME", feedItemVo.getmUserName())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

    }

    private void getFeedData(final String feedId) {
        ((BaseActivity) FeedDetailActivity.this).showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_FEED_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isLoading = false;
                        ((BaseActivity) FeedDetailActivity.this).dismissProgressDialog();
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
                                feedItemVo.setmUserImage(jsonObject.getString("mypic").isEmpty() ? ""
                                        : Config.USER_IMAGE_URL + jsonObject.getString("mypic"));
                                feedItemVo.setmContentImage(jsonObject.getString("image").isEmpty() ? ""
                                        : Config.FEED_IMAGE_URL + jsonObject.getString("image"));
                                feedItemVo.setmDescription(jsonObject.getString("caption"));
                                feedItemVo.setmLikes(jsonObject.getInt("likes_count"));
                                feedItemVo.setmCommentCount(jsonObject.getInt("comment_count"));
                                feedItemVo.setmComment_1(jsonObject.getString("comment_1"));
                                feedItemVo.setCity(jsonObject.getString("city"));
                                feedItemVo.setState(jsonObject.getString("state"));
                                feedItemVo.setmComment_2(jsonObject.getString("comment_2"));
                                feedItemVo.setmUsername1(jsonObject.getString("user_name_1"));
                                feedItemVo.setmUsername2(jsonObject.getString("user_name_2"));
                                feedItemVo.setLiked(jsonObject.getString("likes_status").equals("1"));
                                bindData();

                            } else {
                                Toast.makeText(FeedDetailActivity.this, jObject.getString("status_msg"), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FeedDetailActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity) FeedDetailActivity.this).dismissProgressDialog();
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

        RequestQueue feedqueue = Volley.newRequestQueue(FeedDetailActivity.this);
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
                                Toast.makeText(FeedDetailActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FeedDetailActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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
        ((BaseActivity) FeedDetailActivity.this).showProgessDialog();
        StringRequest requestReportProblem = new StringRequest(Request.Method.POST, Config.REPORT_PROBLEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) FeedDetailActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);

                            } else {
                                Toast.makeText(FeedDetailActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(FeedDetailActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("feed_id", feedId);
                params.put("user_id", AppPreferences.getAppPreferences(FeedDetailActivity.this).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        requestReportProblem.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(FeedDetailActivity.this);
        queue.add(requestReportProblem);
    }

    public void block(final String userId) {
        ((BaseActivity) FeedDetailActivity.this).showProgessDialog();
        StringRequest block = new StringRequest(Request.Method.POST, Config.BLOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) FeedDetailActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);

                            }
                            Toast.makeText(FeedDetailActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(FeedDetailActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("friend_user_id", userId);
                params.put("user_id", AppPreferences.getAppPreferences(FeedDetailActivity.this).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        block.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(FeedDetailActivity.this);
        queue.add(block);
    }

    public void delete(final String feedId) {
        ((BaseActivity) FeedDetailActivity.this).showProgessDialog();
        StringRequest delete = new StringRequest(Request.Method.POST, Config.DELETE_FEED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) FeedDetailActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                onBackPressed();
                            }
                            Toast.makeText(FeedDetailActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(FeedDetailActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("feed_id", feedId);
                params.put("user_id", AppPreferences.getAppPreferences(FeedDetailActivity.this).getStringValue(AppPreferences.USER_ID));


                return params;
            }
        };

        delete.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(FeedDetailActivity.this);
        queue.add(delete);
    }
}
