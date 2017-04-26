package com.kneedleapp.fragment;


import android.content.Intent;
import android.os.Bundle;
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
import com.kneedleapp.BaseActivity;
import com.kneedleapp.FullImageViewActivity;
import com.kneedleapp.R;
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


public class HomeFragment extends BaseFragment implements FeedAdapter.ProfileItemListener {

    private RecyclerView mRecyclerView;
    private FeedAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private StaggeredGridLayoutManager gridLayoutManager;
    private ArrayList<FeedItemVo> mList;
    private String names[] = {"aman", "ravi", "manoj", "krishan"};
    private BaseActivity context;
    private View mView;
    private TextView emptyView;
    private int page = 0;
    private boolean loading, isLastPage;
    private ImageView listBtn, gridBtn;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_home, container, false);

        context = (BaseActivity) getActivity();
        listBtn = (ImageView) mView.findViewById(R.id.img_list);
        gridBtn = (ImageView) mView.findViewById(R.id.img_grid);
        emptyView = (TextView) mView.findViewById(R.id.empty_view);
        Utils.setTypeface(getActivity(), emptyView, Config.CENTURY_GOTHIC_REGULAR);
        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FeedAdapter( mList,getActivity(), "LIST", HomeFragment.this,false);
        mRecyclerView.setAdapter(mAdapter);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listBtn.setVisibility(View.GONE);
                gridBtn.setVisibility(View.VISIBLE);
                mLayoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new FeedAdapter( mList,getActivity(), "LIST", HomeFragment.this,false);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridBtn.setVisibility(View.GONE);
                listBtn.setVisibility(View.VISIBLE);
                gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(gridLayoutManager);
                mAdapter = new FeedAdapter( mList,getActivity(), "GRID", HomeFragment.this,false);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        if (Utils.isNetworkConnected(getContext(), true)) {
            getFeedData();
        }

        ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                page = 0;
                isLastPage = false;
                getFeedData();
                ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastvisibleitemposition;
                if(gridBtn.getVisibility() != View.VISIBLE){
                    int[] arr = gridLayoutManager.findLastVisibleItemPositions(null);
                    lastvisibleitemposition = arr[0];
                    if (lastvisibleitemposition >= mAdapter.getItemCount() - 3) {

                        if (!loading && !isLastPage) {
                            page = page + 1;
                            loading = true;
                            getFeedData();
                        }
                    }
                }
                else {
                    lastvisibleitemposition = mLayoutManager.findLastVisibleItemPosition();
                    if (lastvisibleitemposition == mAdapter.getItemCount() - 1) {

                        if (!loading && !isLastPage) {
                            page = page + 1;
                            loading = true;
                            getFeedData();
                        }
                    }
                }
            }
        });



        return mView;
    }
    @Override
    public void getItem(int position, FeedAdapter.ViewHolder holder, boolean isLiked) {
        Intent intent = new Intent(getActivity(), FullImageViewActivity.class);
        intent.putExtra("USERNAME", mList.get(position).getmFullName());
        intent.putExtra("IMAGE", mList.get(position).getmContentImage());
        intent.putExtra("USERIMAGE", mList.get(position).getmUserImage());
        intent.putExtra("LIKES", mList.get(position).getmLikes());
        intent.putExtra("LIKEDORNOT", isLiked);
        startActivity(intent);
    }
    public void getFeedData() {
        emptyView.setVisibility(View.GONE);
        context.showProgessDialog();
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
                                isLastPage = !(jsonArray.length() > 0);
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
                                mAdapter.notifyDataSetChanged();

                            } else {
                                loading = false;
                                isLastPage = true;
                            }
                            if (mList.size() == 0) {
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
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        context.dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
                params.put("login_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
                params.put("lmt", "10");
                params.put("offset", ""+page);
                Log.v("KNEEDLE","params: "+params);
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


}

