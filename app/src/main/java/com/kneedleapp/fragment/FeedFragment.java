package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kneedleapp.R;
import com.kneedleapp.adapter.FeedItemAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.vo.FeedItemVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FeedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FeedItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<FeedItemVo> mList;
    private String names[] = {"aman", "ravi", "manoj", "krishan"};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BaseActivity context;


    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list_item, container, false);

        context = (BaseActivity) getActivity();


        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FeedItemAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        FeedData();


        return view;


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
                                    feedItemVo.setmUserSubTitle(jsonObject.getString("username"));
                                    feedItemVo.setmUserImage("http://kneedleapp.com/restAPIs/uploads/user_images/" + jsonObject.getString("mypic"));
                                    feedItemVo.setmContentImage("http://kneedleapp.com/restAPIs/uploads/post_images/" + jsonObject.getString("image"));
                                    feedItemVo.setmDesciption(jsonObject.getString("caption"));
                                    feedItemVo.setmLikes(jsonObject.getString("likes_count"));

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
                params.put("user_id", "4");
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
}