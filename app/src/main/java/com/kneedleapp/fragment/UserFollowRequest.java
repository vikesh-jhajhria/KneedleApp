package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kneedleapp.R;
import com.kneedleapp.adapter.UserRequestAdapter;
import com.kneedleapp.vo.UserRequestVo;

import java.util.ArrayList;

import static com.kneedleapp.utils.Config.fragmentManager;


public class UserFollowRequest extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserRequestAdapter mAdapter;
    private View view;
    private ArrayList<UserRequestVo> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_follow_detail, container, false);
        findViews();
        return view;
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.follow_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new UserRequestAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        view.findViewById(R.id.img_back).setOnClickListener(this);


    }

    private void addDataIntoList() {
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_back:
                fragmentManager.popBackStackImmediate();
                break;
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {

                    fragmentManager.popBackStackImmediate();
                    return true;
                }
                return false;
            }
        });

    }
}
