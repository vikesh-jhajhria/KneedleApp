package com.kneedleapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kneedleapp.adapter.FeedAdapter;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements FeedAdapter.ProfileItemListener {


    private FeedAdapter feedAdapter;
    private ArrayList<FeedItemVo> mList = new ArrayList<>();
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    StaggeredGridLayoutManager gridLayoutManager;
    private ImageView listBtn, gridBtn;
    private String mUserId = "", mUserName = "";
    private int offset = 0, limit = 10;
    private boolean loading, isLastPage;

    private TextView num_of_posts, num_of_followers, num_of_following, profile_type, emptyView, companyName, bio, website, location;
    private CircleImageView userImgView;
    private AppPreferences mPrefernce;

    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";

    public String getUserId() {
        return mUserId;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        CURRENT_PAGE = "PROFILE";
        findViewById(R.id.rl_profile_selected).setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUserName = bundle.getString(USER_NAME);
            mUserId = bundle.getString(USER_ID);
        }

        emptyView = (TextView) findViewById(R.id.empty_view);
        applyFonts();

        findViewById(R.id.ll_followers).setOnClickListener(this);
        findViewById(R.id.ll_following).setOnClickListener(this);

        mPrefernce = AppPreferences.getAppPreferences(ProfileActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mList = new ArrayList<>();

        if (mPrefernce.getUserId().equalsIgnoreCase(mUserId)) {
            findViewById(R.id.txt_btn_edit).setVisibility(View.VISIBLE);
        }

        listBtn = (ImageView) findViewById(R.id.img_list);
        gridBtn = (ImageView) findViewById(R.id.img_grid);
        feedAdapter = new FeedAdapter(mList, this, "GRID", this, true);
        gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(feedAdapter);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listBtn.setVisibility(View.GONE);
                gridBtn.setVisibility(View.VISIBLE);
                layoutManager = new LinearLayoutManager(ProfileActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                feedAdapter = new FeedAdapter(mList, ProfileActivity.this, "LIST", ProfileActivity.this, true);
                recyclerView.setAdapter(feedAdapter);
            }
        });
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridBtn.setVisibility(View.GONE);
                listBtn.setVisibility(View.VISIBLE);
                gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
                feedAdapter = new FeedAdapter(mList, ProfileActivity.this, "GRID", ProfileActivity.this, true);
                recyclerView.setAdapter(feedAdapter);
            }
        });


        findViewById(R.id.txt_btn_edit).setOnClickListener(this);
        findViewById(R.id.txt_btn_follow).setOnClickListener(this);
        findViewById(R.id.txt_btn_following).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_setting).setOnClickListener(this);

        num_of_posts = (TextView) findViewById(R.id.txt_post_count);
        num_of_followers = (TextView) findViewById(R.id.txt_follower_count);
        num_of_following = (TextView) findViewById(R.id.txt_following_count);
        profile_type = (TextView) findViewById(R.id.txt_profile_type);
        companyName = (TextView) findViewById(R.id.txt_company);
        bio = (TextView) findViewById(R.id.txt_bio);
        website = (TextView) findViewById(R.id.txt_website);
        location = (TextView) findViewById(R.id.txt_location);
        userImgView = (CircleImageView) findViewById(R.id.user_img);


        if (Utils.isNetworkConnected(ProfileActivity.this, true)) {
            getUserDetails();
        }
        ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserDetails();
                ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastvisibleitemposition;
                if (gridBtn.getVisibility() != View.VISIBLE) {
                    int[] arr = gridLayoutManager.findLastVisibleItemPositions(null);
                    lastvisibleitemposition = arr[0];
                    if (lastvisibleitemposition >= feedAdapter.getItemCount() - 3) {

                        if (!loading && !isLastPage) {
                            offset = limit + offset;
                            loading = true;
                            FeedData(mUserId);
                        }
                    }
                } else {
                    lastvisibleitemposition = layoutManager.findLastVisibleItemPosition();
                    if (lastvisibleitemposition == feedAdapter.getItemCount() - 1) {

                        if (!loading && !isLastPage) {
                            offset = offset + limit;
                            loading = true;
                            FeedData(mUserId);
                        }
                    }
                }
            }
        });
    }


    private void applyFonts() {
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_post_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_follower), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_follower_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_following), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_following_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_btn_edit), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_btn_follow), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_btn_following), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_profile_type), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_company), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_bio), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_website), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, (TextView) findViewById(R.id.txt_location), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(ProfileActivity.this, emptyView, Config.CENTURY_GOTHIC_REGULAR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Kneedle", "Profile");

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {

            case R.id.txt_btn_edit:
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.txt_btn_follow:
                followUnfollowUser(mUserId, Utils.getCurrentDate());
                break;
            case R.id.txt_btn_following:
                followUnfollowUser(mUserId, Utils.getCurrentDate());
                break;
            case R.id.img_back:
                break;
            case R.id.img_setting:

                startActivity(new Intent(getApplicationContext(), SettingActivity.class)
                        .putExtra("USER_ID", mUserId)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

                break;
            case R.id.ll_followers:
                startActivity(new Intent(getApplicationContext(), FollowerActivity.class)
                        .putExtra("USER_ID", getUserId())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.ll_following:
                startActivity(new Intent(getApplicationContext(), FollowingActivity.class)
                        .putExtra("USER_ID", getUserId())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;


        }
    }

    public void getUserDetails() {
        showProgessDialog();
        StringRequest requestUser = new StringRequest(Request.Method.POST, Config.USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                mList.clear();
                                offset = 0;
                                isLastPage = false;

                                Log.e("responce....::>>>", response);

                                JSONObject userDataJsonObject = jObject.getJSONObject("user_data");
                                mUserId = userDataJsonObject.getString("user_id");
                                FeedData(mUserId);
                                num_of_posts.setText(userDataJsonObject.getString("posts"));
                                num_of_following.setText(userDataJsonObject.getString("following"));
                                num_of_followers.setText(userDataJsonObject.getString("followers"));
                                if (!userDataJsonObject.getString("image").isEmpty()) {
                                    Glide.with(ProfileActivity.this).load(Config.USER_IMAGE_URL + userDataJsonObject.getString("image")).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(userImgView);
                                }
                                if (!mPrefernce.getUserId().equalsIgnoreCase(mUserId)) {
                                    findViewById(R.id.txt_btn_edit).setVisibility(View.GONE);
                                    if (userDataJsonObject.getString("follow_status").equalsIgnoreCase("0")) {
                                        mFollowStatus = 0;
                                        findViewById(R.id.txt_btn_follow).setVisibility(View.VISIBLE);
                                        findViewById(R.id.txt_btn_following).setVisibility(View.GONE);
                                    } else {
                                        mFollowStatus = 1;
                                        findViewById(R.id.txt_btn_following).setVisibility(View.VISIBLE);
                                        findViewById(R.id.txt_btn_follow).setVisibility(View.GONE);
                                    }
                                } else {
                                    findViewById(R.id.txt_btn_follow).setVisibility(View.GONE);
                                    findViewById(R.id.txt_btn_following).setVisibility(View.GONE);
                                    findViewById(R.id.txt_btn_edit).setVisibility(View.VISIBLE);
                                }
                                String str = userDataJsonObject.getString("profiletype");
                                if (!userDataJsonObject.getString("company_info").isEmpty()) {
                                    str = str + " @" + userDataJsonObject.getString("company_info");
                                }
                                profile_type.setText(Utils.makeSpannable(ProfileActivity.this, str));
                                profile_type.setMovementMethod(LinkMovementMethod.getInstance());
                                profile_type.setHighlightColor(Color.TRANSPARENT);
                                /*if(!userDataJsonObject.getString("company_info").isEmpty()) {
                                    companyName.setText(userDataJsonObject.getString("company_info"));
                                    companyName.setVisibility(View.VISIBLE);
                                }*/
                                if (!userDataJsonObject.getString("bio").isEmpty()) {
                                    bio.setText(userDataJsonObject.getString("bio"));
                                    bio.setVisibility(View.VISIBLE);
                                }
                                if (!userDataJsonObject.getString("website").isEmpty()) {
                                    website.setText(userDataJsonObject.getString("website"));
                                    website.setVisibility(View.VISIBLE);
                                }
                                if (!userDataJsonObject.getString("state").isEmpty()) {
                                    location.setText(userDataJsonObject.getString("country") + ", " + userDataJsonObject.getString("state"));
                                    location.setVisibility(View.VISIBLE);
                                }

                                ((TextView) findViewById(R.id.txt_username))
                                        .setText(userDataJsonObject.getString("fullname"));
                            } else {
                                Toast.makeText(ProfileActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mPrefernce.getUserId());
                params.put("username", mUserName.isEmpty() ? mPrefernce.getUserName() : mUserName);

                return params;
            }
        };

        requestUser.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue userqueue = Volley.newRequestQueue(ProfileActivity.this);
        userqueue.add(requestUser);
    }

    public void FeedData(final String user_id) {
        emptyView.setVisibility(View.GONE);
        showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_USER_FEEDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                JSONArray jsonArray = jObject.getJSONArray("feed_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    FeedItemVo feedItemVo = new FeedItemVo();
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

                                    mList.add(feedItemVo);
                                }
                                loading = false;
                                feedAdapter.notifyDataSetChanged();

                            } else {
                                loading = false;
                                isLastPage = true;
                                if (mList.size() == 0) {
                                    emptyView.setText(jObject.getString("status_msg"));
                                    emptyView.setVisibility(View.VISIBLE);
                                } else {
                                    emptyView.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("lmt", "" + limit);
                params.put("offset", "" + offset);
                Log.v("KNEEDLE", "params: " + params);
                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(ProfileActivity.this);
        feedqueue.add(requestFeed);
    }

    private int mFollowStatus;

    public void followUnfollowUser(final String friendId, final String date) {
        showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FOLLOW_UNFOLLOW_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                String num = num_of_following.getText().toString().trim();
                                if (mFollowStatus == 0) {
                                    if (!num.isEmpty())
                                        num_of_following.setText("" + (Integer.parseInt(num) + 1));

                                    findViewById(R.id.txt_btn_follow).setVisibility(View.GONE);
                                    findViewById(R.id.txt_btn_following).setVisibility(View.VISIBLE);
                                    mFollowStatus = 1;
                                } else {
                                    if (!num.isEmpty())
                                        num_of_following.setText("" + (Integer.parseInt(num) - 1));

                                    findViewById(R.id.txt_btn_follow).setVisibility(View.VISIBLE);
                                    findViewById(R.id.txt_btn_following).setVisibility(View.GONE);
                                    mFollowStatus = 0;
                                }
                            }
                            Toast.makeText(ProfileActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mPrefernce.getUserId());
                params.put("friend_user_id", friendId);
                params.put("follow_date", date);

                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(ProfileActivity.this);
        feedqueue.add(requestFeed);
    }

    @Override
    public void getItem(int position, FeedAdapter.ViewHolder holder, boolean isLiked) {
        Intent intent = new Intent(ProfileActivity.this, FullImageViewActivity.class);
        intent.putExtra("USERNAME", mList.get(position).getmFullName());
        intent.putExtra("IMAGE", mList.get(position).getmContentImage());
        intent.putExtra("USERIMAGE", mList.get(position).getmUserImage());
        intent.putExtra("LIKES", mList.get(position).getmLikes());
        intent.putExtra("LIKEDORNOT", isLiked);
        startActivity(intent);
    }
}
