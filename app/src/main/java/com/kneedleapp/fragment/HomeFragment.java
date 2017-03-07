package com.kneedleapp.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FeedItemVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements FeedItemAdapter.FeedItemListener {

    private RecyclerView mRecyclerView;
    private FeedItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<FeedItemVo> mList;
    private String names[] = {"aman", "ravi", "manoj", "krishan"};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BaseActivity context;


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = (BaseActivity) getActivity();

        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FeedItemAdapter(getContext(), mList, ((MainActivity) getActivity()), this);
        mRecyclerView.setAdapter(mAdapter);
        if (Utils.isNetworkConnected(getContext(), true)) {
            FeedData();
        }

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

    @Override
    public void getItem(int position, FeedItemAdapter.ViewHolder holder) {
        Intent intent = new Intent(getActivity(), FullImageViewActivity.class);
        intent.putExtra("USERNAME", mList.get(position).getmUserTitle());
        intent.putExtra("IMAGE", mList.get(position).getmContentImage());
        intent.putExtra("USERIMAGE", mList.get(position).getmUserImage());
        intent.putExtra("LIKES", mList.get(position).getmLikes());
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            try {

               *//* Bitmap header = ((BitmapDrawable) ((ImageView) getActivity().findViewById(R.id.img_kneedle)).getDrawable()).getBitmap();
                ByteArrayOutputStream headerstream = new ByteArrayOutputStream();
                header.compress(Bitmap.CompressFormat.PNG, 100, headerstream);
                byte[] headerByteArray = headerstream.toByteArray();
*//*
                Bitmap bmp = ((BitmapDrawable) holder.imgContent.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] mainImageByteArray = stream.toByteArray();

               *//* byte[] userImageByteArray = new byte[0];
                Bitmap imgUser = ((BitmapDrawable) holder.imgUser.getDrawable()).getBitmap();
                ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
                imgUser.compress(Bitmap.CompressFormat.PNG, 100, imgStream);
                userImageByteArray = imgStream.toByteArray();

                byte[] likeByteArray = new byte[0];
                Bitmap imglike = ((BitmapDrawable) holder.imgHeart.getDrawable()).getBitmap();
                ByteArrayOutputStream likeStream = new ByteArrayOutputStream();
                imglike.compress(Bitmap.CompressFormat.PNG, 100, likeStream);
                likeByteArray = likeStream.toByteArray();*//*


                intent.putExtra("transition", false);
                //intent.putExtra("HEADER_IMAGE", headerByteArray);
                intent.putExtra("MAIN_IMAGE", mainImageByteArray);
                //intent.putExtra("USER_IMAGE", userImageByteArray);
                //intent.putExtra("HEART_IMAGE", likeByteArray);
                ActivityOptions options;

                options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        new Pair<View, String>(holder.imgContent, context.getResources().getString(R.string.main_image)),
                        //new Pair<View, String>(holder.imgUser, context.getResources().getString(R.string.user_image)),
                        //new Pair<View, String>(holder.imgHeart, context.getResources().getString(R.string.heart_image)),
                        //new Pair<View, String>(((ImageView) getActivity().findViewById(R.id.img_kneedle)), context.getResources().getString(R.string.header_image)),
                        new Pair<View, String>(holder.tvLikes, context.getResources().getString(R.string.likes)),
                        new Pair<View, String>(holder.tvTitle, context.getResources().getString(R.string.user_name)));


                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {*/
            startActivity(intent);
        //}
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

