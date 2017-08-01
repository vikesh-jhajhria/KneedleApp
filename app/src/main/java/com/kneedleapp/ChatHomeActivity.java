package com.kneedleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatHomeActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView emptyView;
    boolean loading, isLastPage;
    int limit, offset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_list);

        CURRENT_PAGE = "CHAT_HOME";

        ((TextView)findViewById(R.id.txt_title)).setText("NEW MESSAGE");
        findViewById(R.id.img_title).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_title).setOnClickListener(this);
        findViewById(R.id.img_title).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyView = (TextView) findViewById(R.id.empty_view);

        applyFont();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.img_title:
            case R.id.txt_title:

                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    private void applyFont(){
        Utils.setTypeface(ChatHomeActivity.this, emptyView, Config.CENTURY_GOTHIC_REGULAR);
    }

    public void getChatList(final String user_id) {
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

                               /* JSONArray jsonArray = jObject.getJSONArray("feed_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    FeedItemVo feedItemVo = new FeedItemVo();
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
                                    feedItemVo.setmComment_2(jsonObject.getString("comment_2"));
                                    feedItemVo.setCity(jsonObject.getString("city"));
                                    feedItemVo.setState(jsonObject.getString("state"));
                                    feedItemVo.setmUsername1(jsonObject.getString("user_name_1"));
                                    feedItemVo.setmUsername2(jsonObject.getString("user_name_2"));
                                    feedItemVo.setLiked(jsonObject.getString("likes_status").equals("1"));*/

                                    //mList.add(feedItemVo);
                                //}
                                loading = false;
                                //chatHomeAdapter.notifyDataSetChanged();

                            } else {
                                loading = false;
                                isLastPage = true;
                                /*if (mList.size() == 0) {
                                    emptyView.setText(jObject.getString("status_msg"));
                                    emptyView.setVisibility(View.VISIBLE);
                                } else {
                                    emptyView.setVisibility(View.GONE);
                                }*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ChatHomeActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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

        RequestQueue feedqueue = Volley.newRequestQueue(ChatHomeActivity.this);
        feedqueue.add(requestFeed);
    }
}
