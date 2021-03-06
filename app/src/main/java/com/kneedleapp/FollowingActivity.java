package com.kneedleapp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.adapter.UserListAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.UserDetailsVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowingActivity extends BaseActivity {

    private UserListAdapter userListAdapter;
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private String mUserId;
    private TextView emptyView;
    private boolean isLoading;
    private java.util.List<UserDetailsVo> followingsList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_list);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUserId = bundle.getString("USER_ID");
        }

        emptyView = (TextView) findViewById(R.id.empty_view);
        applyFonts();
        CURRENT_PAGE = "FOLLOWING";
        findViewById(R.id.img_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.txt_title)).setText("Followings");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(FollowingActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        userListAdapter = new UserListAdapter(FollowingActivity.this, followingsList, "FOLLOWING");
        recyclerView.setAdapter(userListAdapter);
        if (Utils.isNetworkConnected(FollowingActivity.this, true)) {
            isLoading = true;
            getFollowing();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Config.updateFollowing && !isLoading) {
            if (Utils.isNetworkConnected(FollowingActivity.this, true)) {
                getFollowing();
            }
        }
        isLoading = false;
    }

    private void applyFonts() {
        Utils.setTypeface(FollowingActivity.this, (TextView) findViewById(R.id.txt_title), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(FollowingActivity.this, emptyView, Config.CENTURY_GOTHIC_REGULAR);
    }


    private void getFollowing() {
        followingsList.clear();
        emptyView.setVisibility(View.GONE);
        showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FOLLOWING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                Config.updateFollowing = false;
                                JSONArray jsonArray = jObject.getJSONArray("following_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    UserDetailsVo userObject = new UserDetailsVo();
                                    userObject.setUserId(jsonObject.getString("user_id"));
                                    userObject.setFullname(jsonObject.getString("fullname"));
                                    userObject.setImage(jsonObject.getString("image").isEmpty() ? ""
                                            : Config.USER_IMAGE_URL + jsonObject.getString("image"));
                                    userObject.setProfiletype(jsonObject.getString("profiletype"));
                                    userObject.setUsername(jsonObject.getString("username"));
                                    userObject.setGender(jsonObject.getString("gender"));
                                    userObject.setCity(jsonObject.getString("city"));
                                    userObject.setCountry(jsonObject.getString("state"));
                                    userObject.setCountry(jsonObject.getString("country"));
                                    userObject.setBio(jsonObject.getString("bio"));
                                    userObject.setDob(jsonObject.getString("dob"));
                                    userObject.setEmail(jsonObject.getString("email"));
                                    userObject.setWebsite(jsonObject.getString("website"));
                                    userObject.setPrivacy(jsonObject.getString("privacy"));
                                    userObject.setZipcode(jsonObject.getString("zipcode"));
                                    userObject.setStatus(jsonObject.getString("status"));

                                    followingsList.add(userObject);
                                }
                                userListAdapter.notifyDataSetChanged();

                            }
                            if (followingsList.size() == 0) {
                                emptyView.setText(jObject.getString("status_msg"));
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FollowingActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mUserId);

                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(FollowingActivity.this);
        feedqueue.add(requestFeed);
    }

}
