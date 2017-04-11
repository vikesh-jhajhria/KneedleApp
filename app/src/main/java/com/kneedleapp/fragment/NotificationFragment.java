package com.kneedleapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.KneedleApp;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
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

import static android.content.ContentValues.TAG;


public class NotificationFragment extends BaseFragment implements RecyclerView.OnItemTouchListener {
    private NotificationDataAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<NotificationItemVo> mList;
    private LinearLayoutManager mLayoutManager;
    private View mView;
    private TextView emptyView;

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    private StickyHeaderDecoration decor;


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View v = rv.findChildViewUnder(e.getX(), e.getY());
        return v == null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Kneedle", "Notification");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(NotificationFragment.this);
            }
        }, 500);

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_UP) {
            return;
        }

        // find the header that was clicked
        View view = decor.findHeaderViewUnder(e.getX(), e.getY());

        if (view instanceof TextView) {
            Toast.makeText(this.getActivity(), ((TextView) view).getText() + " clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //Do nothing
    }


    protected void setAdapterAndDecor(RecyclerView list) {
        final NotificationDataAdapter adapter = new NotificationDataAdapter(getContext(), mList);
        decor = new StickyHeaderDecoration(adapter);
        setHasOptionsMenu(true);

        list.setAdapter(adapter);
        list.addItemDecoration(decor, 1);
        list.addOnItemTouchListener(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notification, container, false);
        emptyView = (TextView) mView.findViewById(R.id.empty_view);
        Utils.setTypeface(getActivity(), emptyView, Config.CENTURY_GOTHIC_REGULAR);
        Config.LAST_PAGE = "HOME";
        final DividerDecoration divider = new DividerDecoration.Builder(this.getActivity())
                .setHeight(R.dimen.default_divider_height)
                .setColorResource(R.color.gray)
                .build();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.list_notification);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(divider);


        setAdapterAndDecor(mRecyclerView);
        if (Utils.isNetworkConnected(getActivity(), true)) {
            getNotification();
        }
        ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkConnected(getActivity(), true)) {
                    getNotification();
                }
                ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });

        return mView;
    }

    private void loadDataToList(JSONArray jsonArray) {
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

    private void sortList() {
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
                    e.printStackTrace();
                }
                if(date1 != null && date2 != null) {
                    return date1.compareTo(date2);
                } else{
                    return 0;
                }
            }
        });
        mAdapter = new NotificationDataAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getNotification() {
        emptyView.setVisibility(View.GONE);
        ((MainActivity) getActivity()).showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.GET_NOTIFICATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        ((MainActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                mList.clear();
                                loadDataToList(jObject.getJSONObject("result").getJSONArray("likes"));
                                loadDataToList(jObject.getJSONObject("result").getJSONArray("comments"));
                                loadDataToList(jObject.getJSONObject("result").getJSONArray("followers"));
                                loadDataToList(jObject.getJSONObject("result").getJSONArray("taged_users"));
                                sortList();

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
                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
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
