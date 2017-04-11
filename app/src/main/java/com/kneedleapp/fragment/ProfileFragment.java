package com.kneedleapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kneedleapp.BaseActivity;
import com.kneedleapp.FullImageViewActivity;
import com.kneedleapp.R;
import com.kneedleapp.adapter.ProfileListAdapter;
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

import static com.kneedleapp.utils.Config.fragmentManager;


public class ProfileFragment extends BaseFragment
        implements ProfileListAdapter.ProfileItemListener {

    private ProfileListAdapter profileListAdapter;
    private ArrayList<FeedItemVo> mList = new ArrayList<>();
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private ImageView listBtn, gridBtn;
    private static BaseActivity context;
    private View view;
    private String mUserId = "", mUserName = "";

    private TextView num_of_posts, num_of_followers, num_of_following, profile_type, emptyView, companyName, bio, website, location;
    private CircleImageView userImgView;
    private AppPreferences mPrefernce;

    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";

    public static ProfileFragment newInstance(String userId, String userName) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    public String getUserId() {
        return mUserId;
    }

    public void loadMyProfile() {
        mUserId = mPrefernce.getUserId();
        mUserName = mPrefernce.getUserName();
        view.findViewById(R.id.txt_btn_edit).setVisibility(View.VISIBLE);
        if (Utils.isNetworkConnected(getActivity(), true)) {
            getUserDetails();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserName = bundle.getString(USER_NAME);
            mUserId = bundle.getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        applyFonts(view);

        view.findViewById(R.id.ll_followers).setOnClickListener(this);
        view.findViewById(R.id.ll_following).setOnClickListener(this);

        context = (BaseActivity) getActivity();
        mPrefernce = AppPreferences.getAppPreferences(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mList = new ArrayList<>();

        if (mPrefernce.getUserId().equalsIgnoreCase(mUserId)) {
            view.findViewById(R.id.txt_btn_edit).setVisibility(View.VISIBLE);
        }

        listBtn = (ImageView) view.findViewById(R.id.img_list);
        gridBtn = (ImageView) view.findViewById(R.id.img_grid);
        profileListAdapter = new ProfileListAdapter(mList, getActivity(), "Grid", this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(profileListAdapter);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listBtn.setVisibility(View.GONE);
                gridBtn.setVisibility(View.VISIBLE);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                profileListAdapter = new ProfileListAdapter(mList, getContext(), "LIST", ProfileFragment.this);
                recyclerView.setAdapter(profileListAdapter);
            }
        });
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridBtn.setVisibility(View.GONE);
                listBtn.setVisibility(View.VISIBLE);
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
                profileListAdapter = new ProfileListAdapter(mList, getContext(), "GRID", ProfileFragment.this);
                recyclerView.setAdapter(profileListAdapter);
            }
        });


        view.findViewById(R.id.txt_btn_edit).setOnClickListener(this);
        view.findViewById(R.id.txt_btn_follow).setOnClickListener(this);
        view.findViewById(R.id.txt_btn_following).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_chat).setOnClickListener(this);

        num_of_posts = (TextView) view.findViewById(R.id.txt_post_count);
        num_of_followers = (TextView) view.findViewById(R.id.txt_follower_count);
        num_of_following = (TextView) view.findViewById(R.id.txt_following_count);
        profile_type = (TextView) view.findViewById(R.id.txt_profile_type);
        companyName = (TextView) view.findViewById(R.id.txt_company);
        bio = (TextView) view.findViewById(R.id.txt_bio);
        website = (TextView) view.findViewById(R.id.txt_website);
        location= (TextView) view.findViewById(R.id.txt_location);
        userImgView = (CircleImageView) view.findViewById(R.id.user_img);


        if (Utils.isNetworkConnected(getActivity(), true)) {
            getUserDetails();
        }
        ((SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserDetails();
                ((SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });


        return view;
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_post_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_follower), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_follower_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_following), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_following_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_btn_edit), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_btn_follow), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_btn_following), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_profile_type), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_company), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_bio), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_website), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_location), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), emptyView, Config.CENTURY_GOTHIC_REGULAR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Kneedle", "Profile");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(ProfileFragment.this);
            }
        }, 500);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.txt_btn_edit:
                Bundle bundle = new Bundle();
                Fragment fragment = new EditProfileFragment();
                fragmentManager.beginTransaction().add(R.id.main_frame, fragment, "EDITPROFILE").addToBackStack(null).commit();
                break;
            case R.id.txt_btn_follow:
                followUnfollowUser(mUserId, Utils.getCurrentDate());
                break;
            case R.id.txt_btn_following:

                break;
            case R.id.img_back:
                break;
            case R.id.img_chat:
               /* final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.unfollow_popup, null);
                builder.setCancelable(false);
                builder.setView(view1);

                String text = "UNFOLLOW  ABIELKUNST?";
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colourRed)), 0, 8, 0);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorPrimary)), 9, text.length(), 0);
                ((TextView) view1.findViewById(R.id.txt_unfollow)).setText(spannableString);
                Utils.setTypeface(getActivity(), ((TextView) view1.findViewById(R.id.txt_unfollow)), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(getActivity(), ((TextView) view1.findViewById(R.id.txt_cancel)), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(getActivity(), ((TextView) view1.findViewById(R.id.txt_confirm)), Config.CENTURY_GOTHIC_BOLD);
                final AlertDialog alertDialog = builder.create();

                ((TextView) view1.findViewById(R.id.txt_cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                ((TextView) view1.findViewById(R.id.txt_confirm)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();*/

                Fragment fragment1 = SettingFragment.newInstance(getUserId());
                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment1).addToBackStack(null).commit();


                break;
            case R.id.ll_followers:
                FollowerFragment followerFragment = FollowerFragment.newInstance(getUserId());
                fragmentManager.beginTransaction().add(R.id.main_frame, followerFragment)
                        .addToBackStack(null).commit();

                break;
            case R.id.ll_following:
                FollowingFragment followingFragment = FollowingFragment.newInstance(getUserId());
                fragmentManager.beginTransaction().add(R.id.main_frame, followingFragment)
                        .addToBackStack(null).commit();
                break;


        }
    }

    public void getUserDetails() {
        context.showProgessDialog();
        StringRequest requestUser = new StringRequest(Request.Method.POST, Config.USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        context.dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                FeedData(mUserId);
                                Log.e("responce....::>>>", response);

                                JSONObject userDataJsonObject = jObject.getJSONObject("user_data");
                                num_of_posts.setText(userDataJsonObject.getString("posts"));
                                num_of_following.setText(userDataJsonObject.getString("following"));
                                num_of_followers.setText(userDataJsonObject.getString("followers"));
                                if (!userDataJsonObject.getString("image").isEmpty()) {
                                    Glide.with(context).load(Config.USER_IMAGE_URL + userDataJsonObject.getString("image")).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(userImgView);
                                }
                                if (!mPrefernce.getUserId().equalsIgnoreCase(mUserId)) {
                                    if (userDataJsonObject.getString("follow_status").equalsIgnoreCase("0")) {
                                        view.findViewById(R.id.txt_btn_follow).setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.txt_btn_following).setVisibility(View.GONE);
                                    } else {
                                        view.findViewById(R.id.txt_btn_following).setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.txt_btn_follow).setVisibility(View.GONE);
                                    }
                                }

                                profile_type.setText(userDataJsonObject.getString("profiletype"));
                                if(!userDataJsonObject.getString("company_info").isEmpty()) {
                                    companyName.setText(userDataJsonObject.getString("company_info"));
                                    companyName.setVisibility(View.VISIBLE);
                                }
                                if(!userDataJsonObject.getString("bio").isEmpty()) {
                                    bio.setText(userDataJsonObject.getString("bio"));
                                    bio.setVisibility(View.VISIBLE);
                                }
                                if(!userDataJsonObject.getString("website").isEmpty()) {
                                    website.setText(userDataJsonObject.getString("website"));
                                    website.setVisibility(View.VISIBLE);
                                }
                                if(!userDataJsonObject.getString("state").isEmpty()) {
                                    location.setText(userDataJsonObject.getString("country")+", "+userDataJsonObject.getString("state"));
                                    location.setVisibility(View.VISIBLE);
                                }

                                ((TextView) view.findViewById(R.id.txt_username))
                                        .setText(userDataJsonObject.getString("fullname"));
                            } else {
                                Toast.makeText(getContext(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mUserId);
                params.put("username", mUserName);

                return params;
            }
        };

        requestUser.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue userqueue = Volley.newRequestQueue(getContext());
        userqueue.add(requestUser);
    }

    public void FeedData(final String user_id) {
        emptyView.setVisibility(View.GONE);
        context.showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_USER_FEEDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        context.dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                mList.clear();
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
                                profileListAdapter.notifyDataSetChanged();

                            } else {
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
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        context.dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("lmt", "30");
                params.put("offset", "0");
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

    public void followUnfollowUser(final String friendId, final String date) {
        context.showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FOLLOW_UNFOLLOW_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) context).showProgessDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                String num = num_of_followers.getText().toString().trim();
                                if (!num.isEmpty())
                                    num_of_followers.setText(Integer.parseInt(num) + 1);
                            }
                            Toast.makeText(context, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

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
                        ((BaseActivity) context).dismissProgressDialog();
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

        RequestQueue feedqueue = Volley.newRequestQueue(context);
        feedqueue.add(requestFeed);
    }

    @Override
    public void getItem(int position, ProfileListAdapter.ViewHolder holder, boolean isLiked) {
        Intent intent = new Intent(getActivity(), FullImageViewActivity.class);
        intent.putExtra("USERNAME", mList.get(position).getmFullName());
        intent.putExtra("IMAGE", mList.get(position).getmContentImage());
        intent.putExtra("USERIMAGE", mList.get(position).getmUserImage());
        intent.putExtra("LIKES", mList.get(position).getmLikes());
        intent.putExtra("LIKEDORNOT", isLiked);
        startActivity(intent);
    }
}







