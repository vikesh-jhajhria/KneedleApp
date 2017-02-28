package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.kneedleapp.R;
import com.kneedleapp.adapter.CommentAdapter;
import com.kneedleapp.vo.CommentVo;

import java.util.ArrayList;


public class AddCommentFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private ArrayList<CommentVo> mList;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentAdapter mAdapter;
    private View view;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String names[] = {"aman", "ravi", "mukesh", "gurubhai"};
    private String mComment;
    private EditText mEdtComment;
    private CommentVo obj;

    public static AddCommentFragment newInstance(String param1, String param2) {
        AddCommentFragment fragment = new AddCommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_comment, container, false);
        findViews();

        return view;
    }

    private void findViews() {
        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_comments);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CommentAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mEdtComment = ((EditText) view.findViewById(R.id.edt_comment));
        view.findViewById(R.id.txt_post).setOnClickListener(this);

    }

    private void addDataIntoList() {
        obj = new CommentVo();
        obj.setmUserName("aman");
        obj.setmDescription(mComment);
        obj.setmImageUrl("");
        mList.add(obj);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_post:

                if (mEdtComment.getText().toString().isEmpty()) {

                    Toast.makeText(getContext(), "Please write the comment", Toast.LENGTH_LONG).show();
                } else {

                    mComment = mEdtComment.getText().toString();
                    addDataIntoList();
                    Log.e("comment", mComment);
                    mEdtComment.setText("");
                    hideKeyboard();
                }


                break;
        }
    }
}
