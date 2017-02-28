package com.kneedleapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kneedleapp.EditProfileActivity;
import com.kneedleapp.R;
import com.kneedleapp.vo.ListVo;

import java.util.ArrayList;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private com.kneedleapp.adapter.ListAdapter listAdapter;
    private ArrayList<ListVo> List = new ArrayList<ListVo>();
    public static RecyclerView recyclerView;
    public boolean checkFlag = true;
    LinearLayoutManager layoutManager;
    private ImageView avatar, list, grid;

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        List = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            ListVo check = new ListVo();
            check.ProjectName = "Android";
            check.image = getResources().getDrawable(R.drawable.image);
            List.add(check);
        }
        list = (ImageView) view.findViewById(R.id.list);
        grid = (ImageView) view.findViewById(R.id.grid);
        listAdapter = new com.kneedleapp.adapter.ListAdapter(List, getContext(), "grid");
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(listAdapter);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                listAdapter = new com.kneedleapp.adapter.ListAdapter(List, getContext(), "list");
                recyclerView.setAdapter(listAdapter);
            }
        });
        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFlag = false;
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
                listAdapter = new com.kneedleapp.adapter.ListAdapter(List, getContext(), "grid");
                recyclerView.setAdapter(listAdapter);
            }
        });


        view.findViewById(R.id.edit_profile).setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.edit_profile:

                startActivity(new Intent(getActivity(), EditProfileActivity.class));

                break;


        }
    }
}







