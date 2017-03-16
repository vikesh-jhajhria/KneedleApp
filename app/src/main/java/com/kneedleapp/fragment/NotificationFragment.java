package com.kneedleapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kneedleapp.KneedleApp;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.adapter.NotificationDataAdapter;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.vo.NotificationItemVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class NotificationFragment extends BaseFragment {
    private NotificationDataAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<NotificationItemVo> mList;
    private LinearLayoutManager mLayoutManager;
    private View mView;

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notification, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview_notification);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationDataAdapter(getContext(), getData());
        mRecyclerView.setAdapter(mAdapter);

        ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificatios();
                ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });

        return mView;
    }

    public ArrayList<NotificationItemVo> getData() {

        String offer = "Aishwarya Kaushik commented on your photo";
        SpannableString offerspannable = new SpannableString(offer);
        offerspannable.setSpan(new ForegroundColorSpan(Color.RED), 0, 17, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String tomorrowAsString = dateFormat.format(tomorrow);
        Log.e("yesterday", tomorrowAsString);


        calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        String todayAsString = dateFormat.format(today);
        Log.e("today", todayAsString);

        ArrayList<NotificationItemVo> list = new ArrayList<>();

        if (todayAsString.equals(todayAsString)) {

            list.add(new NotificationItemVo("Today", null, NotificationItemVo.DAY));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
        }
        if (!tomorrowAsString.equals(todayAsString)) {
            list.add(new NotificationItemVo("Yesterday", null, NotificationItemVo.DAY));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));
            list.add(new NotificationItemVo(null, offerspannable, NotificationItemVo.NOTIFICATION));

        }
        return list;
    }

    public void getNotificatios() {

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
                                JSONArray jsonArray = jObject.getJSONObject("result").getJSONArray("likes");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = (JSONObject) jsonArray.get(i);
                                    //NotificationItemVo obj = new NotificationItemVo();

                                    //mList.add(obj);

                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
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
