package com.kneedleapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kneedleapp.adapter.NotificationDataAdapter;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.NotificationItemVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.barrenechea.widget.recyclerview.decoration.DividerDecoration;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;


public class NotificationActivity extends BaseActivity implements RecyclerView.OnItemTouchListener {


    private NotificationDataAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<NotificationItemVo> mList;
    private LinearLayoutManager mLayoutManager;
    private TextView emptyView;


    private StickyHeaderDecoration decor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        findViewById(R.id.rl_notification_selected).setVisibility(View.VISIBLE);
        mList = new ArrayList<>();

        emptyView = (TextView) findViewById(R.id.empty_view);
        Utils.setTypeface(this, emptyView, Config.CENTURY_GOTHIC_REGULAR);
        CURRENT_PAGE = "NOTIFICATION";
        final DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.default_divider_height)
                .setColorResource(R.color.gray)
                .build();
        mRecyclerView = (RecyclerView) findViewById(R.id.list_notification);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(divider);


        setAdapterAndDecor(mRecyclerView);
        if (Utils.isNetworkConnected(this, true)) {
            getNotification();
        }
        ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkConnected(NotificationActivity.this, true)) {
                    getNotification();
                }
                ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View v = rv.findChildViewUnder(e.getX(), e.getY());
        return v == null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Kneedle", "Notification");
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_UP) {
            return;
        }

        // find the header that was clicked
        View view = decor.findHeaderViewUnder(e.getX(), e.getY());

        if (view instanceof TextView) {
            Toast.makeText(this, ((TextView) view).getText() + " clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //Do nothing
    }


    protected void setAdapterAndDecor(RecyclerView list) {
        final NotificationDataAdapter adapter = new NotificationDataAdapter(this, mList);
        decor = new StickyHeaderDecoration(adapter);

        list.setAdapter(adapter);
        list.addItemDecoration(decor, 1);
        list.addOnItemTouchListener(this);
    }


    private void loadDataToList(JSONArray jsonArray) {
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject commentObj = (JSONObject) jsonArray.get(i);
                    NotificationItemVo obj = new NotificationItemVo();
                    obj.setTime(commentObj.getString("time"));
                    obj.setFullName(commentObj.getString("fullname"));
                    obj.setUsername(commentObj.getString("username"));
                    obj.setImgUser(Config.USER_IMAGE_URL + commentObj.getString("profile_pic"));
                    switch (commentObj.getString("notificationType")) {
                        case "Like":
                            obj.setId(commentObj.getString("id"));
                            obj.setFeedId(commentObj.getString("feed_id"));
                            obj.setUserId(commentObj.getString("user_id"));
                            obj.setType(BaseActivity.NotificationType.LIKE);
                            break;
                        case "Comment":
                            obj.setId(commentObj.getString("id"));
                            obj.setFeedId(commentObj.getString("feed_id"));
                            obj.setUserId(commentObj.getString("user_id"));
                            obj.setComment(commentObj.getString("comment"));
                            obj.setType(BaseActivity.NotificationType.COMMENT);
                            break;
                        case "Follow":
                            obj.setFollowingId(commentObj.getString("f_id"));
                            obj.setFollowerId(commentObj.getString("follower_id"));
                            obj.setUserId(commentObj.getString("user_id"));
                            obj.setType(BaseActivity.NotificationType.FOLLOW);
                            break;
                        case "Taged":
                            obj.setId(commentObj.getString("id"));
                            obj.setFeedId(commentObj.getString("feed_id"));
                            obj.setTaggedUserId(commentObj.getString("taged_user_id"));
                            obj.setType(BaseActivity.NotificationType.TAGGED);
                            break;

                    }
                    mList.add(obj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sortList() {
        try {
            final SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");

            Collections.sort(mList, new Comparator<NotificationItemVo>() {
                @Override
                public int compare(NotificationItemVo n1, NotificationItemVo n2) {
                    Date date1 = null, date2 = null;
                    try {
                        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                        date1 = curFormater.parse(n1.getTime());
                        date2 = curFormater.parse(n2.getTime());
                    } catch (Exception e) {
                        if(date1 == null){
                            mList.remove(n1);
                        }else{
                            mList.remove(n2);
                        }
                        e.printStackTrace();
                    }
                    if (date1 != null && date2 != null) {
                        return date1.compareTo(date2);
                    } else {
                        return 0;
                    }
                }
            });
            mAdapter = new NotificationDataAdapter(NotificationActivity.this, mList);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNotification() {
        emptyView.setVisibility(View.GONE);
        showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.GET_NOTIFICATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                mList.clear();
                                if(!jObject.isNull("result")) {
                                    loadDataToList(jObject.getJSONObject("result").optJSONArray("likes"));
                                    loadDataToList(jObject.getJSONObject("result").optJSONArray("comments"));
                                    loadDataToList(jObject.getJSONObject("result").optJSONArray("followers"));
                                    loadDataToList(jObject.getJSONObject("result").optJSONArray("taged_users"));
                                    sortList();
                                } else{
                                    emptyView.setVisibility(View.VISIBLE);
                                }
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
                        Toast.makeText(NotificationActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", AppPreferences.getAppPreferences(NotificationActivity.this).getStringValue(AppPreferences.USER_ID));
                    Log.v(TAG, "Params : " + params.toString());
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
}
