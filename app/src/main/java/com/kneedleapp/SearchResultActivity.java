package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.kneedleapp.adapter.FeedAdapter;
import com.kneedleapp.adapter.SearchResultAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;
import com.kneedleapp.vo.SearchResultVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchResultActivity extends BaseActivity implements FeedAdapter.ProfileItemListener {

    private SearchResultAdapter searchResultAdapter;
    private FeedAdapter mFeedAdapter;
    private ArrayList<SearchResultVO> mList;
    private ArrayList<FeedItemVo> mFeedList;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String mSearchText, mCategory, mZip, hashString = "", mRange;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        findViewById(R.id.rl_search_selected).setVisibility(View.VISIBLE);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mSearchText = bundle.getString("SEARCHTEXT", "");
            mCategory = bundle.getString("CATEGORY", "");
            mZip = bundle.getString("ZIP", "");
            mRange = bundle.getString("RANGE", "");

        }

        applyFonts();
        CURRENT_PAGE = "SEARCH";

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mList = new ArrayList<>();
        mFeedList = new ArrayList<>();
        if (Utils.isNetworkConnected(SearchResultActivity.this, true)) {
            if (!mZip.isEmpty() && !mRange.isEmpty()) {
                getCriteria();
            } else {
                getSearchItem();
            }
        }
        layoutManager = new LinearLayoutManager(SearchResultActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        searchResultAdapter = new SearchResultAdapter(mList, SearchResultActivity.this);
        mFeedAdapter = new FeedAdapter(mFeedList, SearchResultActivity.this, "LIST", SearchResultActivity.this, false);
        recyclerView.setAdapter(searchResultAdapter);


        ((EditText) findViewById(R.id.txt_title)).setText(mSearchText);


        /*((EditText) findViewById(R.id.txt_title)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchText = ((EditText) findViewById(R.id.txt_title)).getText().toString().trim();
                    mFeedList.clear();
                    mList.clear();
                    getSearchItem();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });*/

        ((ImageView) findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) findViewById(R.id.txt_title)).setText("");
                mFeedList.clear();
                mList.clear();
            }
        });


        ((EditText) findViewById(R.id.txt_title)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((ImageView) findViewById(R.id.img_close)).setVisibility(View.VISIBLE);
                if (((EditText) findViewById(R.id.txt_title)).getText().toString().trim().equals("")) {

                    ((ImageView) findViewById(R.id.img_close)).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkConnected(SearchResultActivity.this, true)) {
                    mFeedList.clear();
                    mList.clear();
                    if (!mZip.isEmpty() && !mRange.isEmpty()) {
                        getCriteria();
                    } else {
                        getSearchItem();
                    }
                }
                ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFeedAdapter != null){
            mFeedAdapter.notifyDataSetChanged();
        }
    }
    private void applyFonts() {
        Utils.setTypeface(SearchResultActivity.this, (TextView) findViewById(R.id.txt_title), Config.CENTURY_GOTHIC_REGULAR);
    }

    public void getCriteria() {
        showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_USERS_CRITERIA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            searchResultAdapter.notifyDataSetChanged();

                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                boolean isFeedData = false;
                                JSONArray jsonArray = jObject.getJSONArray("search_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (!isFeedData && jsonObject.isNull("id")) {
                                        SearchResultVO searchResultVO = new SearchResultVO();
                                        searchResultVO.setmUserId(jsonObject.getString("user_id"));
                                        searchResultVO.setmUserName(jsonObject.getString("username"));
                                        searchResultVO.setmFullName(jsonObject.getString("fullname"));
                                        searchResultVO.setmProfileType(jsonObject.getString("profiletype"));
                                        searchResultVO.setmCityName(jsonObject.getString("city"));
                                        searchResultVO.setmImgUrl(jsonObject.getString("profile_pic"));
                                        mList.add(searchResultVO);
                                    } else {
                                        isFeedData = true;
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
                                        feedItemVo.setmUsername1(jsonObject.getString("user_name_1"));
                                        feedItemVo.setCity(jsonObject.getString("city"));
                                        feedItemVo.setState(jsonObject.getString("state"));
                                        feedItemVo.setmUsername2(jsonObject.getString("user_name_2"));
                                        feedItemVo.setmComment_2(jsonObject.getString("comment_2"));
                                        feedItemVo.setLiked(jsonObject.getString("likes_status").equals("1"));

                                        mFeedList.add(feedItemVo);
                                    }
                                }

                                if (isFeedData) {
                                    recyclerView.setAdapter(mFeedAdapter);
                                    mFeedAdapter.notifyDataSetChanged();
                                } else {
                                    recyclerView.setAdapter(searchResultAdapter);
                                    searchResultAdapter.notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(SearchResultActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SearchResultActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zipcode", mZip);
                params.put("radius", mRange);
                params.put("profiletype", mCategory);
                Log.v("Kneedle", "Params: " + params);
                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(SearchResultActivity.this);
        feedqueue.add(requestFeed);
    }

    public void getSearchItem() {
        showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_SEARCH_ITEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            searchResultAdapter.notifyDataSetChanged();

                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                boolean isFeedData = false;
                                JSONArray jsonArray = jObject.getJSONArray("search_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (!isFeedData && jsonObject.isNull("id")) {
                                        SearchResultVO searchResultVO = new SearchResultVO();
                                        searchResultVO.setmUserId(jsonObject.getString("user_id"));
                                        searchResultVO.setmUserName(jsonObject.getString("username"));
                                        searchResultVO.setmFullName(jsonObject.getString("fullname"));
                                        searchResultVO.setmProfileType(jsonObject.getString("profiletype"));
                                        searchResultVO.setmCityName(jsonObject.getString("city"));
                                        searchResultVO.setmImgUrl(jsonObject.getString("profile_pic"));
                                        mList.add(searchResultVO);
                                    } else {
                                        isFeedData = true;
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
                                        feedItemVo.setmUsername1(jsonObject.getString("user_name_1"));
                                        feedItemVo.setCity(jsonObject.getString("city"));
                                        feedItemVo.setState(jsonObject.getString("state"));
                                        feedItemVo.setmUsername2(jsonObject.getString("user_name_2"));
                                        feedItemVo.setmComment_2(jsonObject.getString("comment_2"));
                                        feedItemVo.setLiked(jsonObject.getString("likes_status").equals("1"));

                                        mFeedList.add(feedItemVo);
                                    }
                                }

                                if (isFeedData) {
                                    recyclerView.setAdapter(mFeedAdapter);
                                    mFeedAdapter.notifyDataSetChanged();
                                } else {
                                    recyclerView.setAdapter(searchResultAdapter);
                                    searchResultAdapter.notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(SearchResultActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SearchResultActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (mSearchText != null && !mSearchText.isEmpty()) {
                    params.put("category", mCategory);
                    params.put("zipcode", mZip);
                } else {
                    mSearchText = ((EditText) findViewById(R.id.txt_title)).getText().toString().trim();
                    Log.e("aman", "serach text");
                }
                hashString = "";
                char[] charArray = mSearchText.toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    if (charArray[j] == '#') {
                        hashString = hashString.length() > 0 ? hashString + "," : hashString;
                        int endIndex = mSearchText.indexOf(" ", j);
                        if (endIndex == -1) {
                            endIndex = mSearchText.indexOf("#", j + 1);
                        }
                        if (endIndex == -1) {
                            endIndex = mSearchText.length();
                        }
                        hashString = hashString + mSearchText.substring(j + 1, endIndex);
                    }
                }
                params.put("hashkeyword", hashString);
                params.put("searchtext", "");
                if (hashString.isEmpty()) {
                    params.put("searchtext", mSearchText);
                }
                Log.v("Kneedle", "Params: " + params);
                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(SearchResultActivity.this);
        feedqueue.add(requestFeed);
    }

    @Override
    public void getItem(int position, FeedAdapter.ViewHolder holder, boolean isLiked) {
        Intent intent = new Intent(this, FullImageViewActivity.class);
        intent.putExtra("USERNAME", mFeedList.get(position).getmFullName());
        intent.putExtra("IMAGE", mFeedList.get(position).getmContentImage());
        intent.putExtra("USERIMAGE", mFeedList.get(position).getmUserImage());
        intent.putExtra("LIKES", mFeedList.get(position).getmLikes());
        intent.putExtra("LIKEDORNOT", isLiked);
        startActivity(intent);
    }
}
