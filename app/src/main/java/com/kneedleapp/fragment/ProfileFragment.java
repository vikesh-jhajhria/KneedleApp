package com.kneedleapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


        ((RelativeLayout) getActivity().findViewById(R.id.rl_toolbar)).setVisibility(View.GONE);

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


        view.findViewById(R.id.txt_edit_profile).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_chat).setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.txt_edit_profile:
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();

                /*Fragment fragment = new UserFollowRequest();
                getFragmentManager().beginTransaction().add(R.id.main_frame,fragment).addToBackStack(null).commit();
*/

                break;
            case R.id.img_back:

                break;
            case R.id.img_chat:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.unfollow_popup, null);
                builder.setView(view1);

                String text = "UNFOLLOW  ABIELKUNST?";
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colourRed)), 0, 8, 0);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorPrimary)), 9, text.length(), 0);
                ((TextView) view1.findViewById(R.id.txt_unfollow)).setText(spannableString);
                final AlertDialog alertDialog = builder.create();

                ((TextView) view1.findViewById(R.id.txt_cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                ((TextView) view1.findViewById(R.id.txt_confirm)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

                break;


        }
    }

}







