package com.kneedleapp.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.FullImageViewActivity;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.adapter.FeedItemAdapter;
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


public class HomeFragment extends BaseFragment implements FeedItemAdapter.FeedItemListener {

    private RecyclerView mRecyclerView;
    private FeedItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<FeedItemVo> mList;
    private String names[] = {"aman", "ravi", "manoj", "krishan"};
    private BaseActivity context;
    private View mView;
    private FeedItemAdapter.ViewHolder viewHolder;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_home, container, false);

        context = (BaseActivity) getActivity();


        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FeedItemAdapter(getActivity(), mList, this);
        mRecyclerView.setAdapter(mAdapter);
        if (Utils.isNetworkConnected(getContext(), true)) {
            FeedData();
        }

        ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedData();
                ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });



        return mView;
    }

    public void FeedData() {

        context.showProgessDialog("Please wait...");
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FEED_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        context.dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                JSONArray jsonArray = jObject.getJSONArray("feed_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    FeedItemVo feedItemVo = new FeedItemVo();
                                    feedItemVo.setmUserTitle(jsonObject.getString("fullname"));
                                    feedItemVo.setmId(jsonObject.getString("id"));
                                    feedItemVo.setmDate(jsonObject.getString("date"));
                                    feedItemVo.setmUserSubTitle(jsonObject.getString("username"));
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
                                mAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getContext(), "no data available", Toast.LENGTH_SHORT).show();
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
                params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
                params.put("lmt", "10");
                params.put("offset", "1");
                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(getContext());
        feedqueue.add(requestFeed);
    }


    @Override
    public void getItem(int position, FeedItemAdapter.ViewHolder holder, boolean isLiked) {
        Intent intent = new Intent(getActivity(), FullImageViewActivity.class);
        intent.putExtra("USERNAME", mList.get(position).getmUserTitle());
        intent.putExtra("IMAGE", mList.get(position).getmContentImage());
        intent.putExtra("USERIMAGE", mList.get(position).getmUserImage());
        intent.putExtra("LIKES", mList.get(position).getmLikes());
        intent.putExtra("LIKEDORNOT", isLiked);
        startActivity(intent);
    }


    @SuppressLint("NewApi")
    public class DetailsTransition extends android.transition.TransitionSet {
        public DetailsTransition() {
            init();
        }

        /**
         * This constructor allows us to use this transition in XML
         */
        public DetailsTransition(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new android.transition.ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform());
        }
    }


}

