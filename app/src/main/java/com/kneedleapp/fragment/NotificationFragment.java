package com.kneedleapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kneedleapp.R;
import com.kneedleapp.adapter.NotificationDataAdapter;
import com.kneedleapp.vo.NotificationItemVo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NotificationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private NotificationDataAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<NotificationItemVo> mList;
    private LinearLayoutManager mLayoutManager;

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_notification);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationDataAdapter(getContext(), getData());
        mRecyclerView.setAdapter(mAdapter);


        return view;
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

}
