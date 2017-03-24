package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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
import com.kneedleapp.adapter.CommentAdapter;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CommentVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.kneedleapp.utils.Config.fragmentManager;


public class AddCommentFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private ArrayList<CommentVo> mList;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentAdapter mAdapter;
    private View view;
    private static final String FEEDID = "FEEDID";
    private String mComment;
    private EditText mEdtComment;
    private String feedId;

    public static AddCommentFragment newInstance(String feedId) {
        AddCommentFragment fragment = new AddCommentFragment();
        Bundle args = new Bundle();
        args.putString(FEEDID, feedId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedId = getArguments().getString(FEEDID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_comment, container, false);
        findViews();
        applyFonts(view);
        if (Utils.isNetworkConnected(getActivity(), true)) {
            loadComments(AppPreferences.getAppPreferences(getActivity()).getUserId(), feedId, 30, 0);
        }
        return view;
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_comment), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.edt_comment), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_BOLD);
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
        view.findViewById(R.id.img_back).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_post:

                if (mEdtComment.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please write the comment", Toast.LENGTH_LONG).show();
                } else {
                    mComment = mEdtComment.getText().toString();
                    addComment(AppPreferences.getAppPreferences(getActivity()).getUserId(), feedId, mComment, "");
                    mEdtComment.setText("");
                    hideKeyboard();
                }
                break;
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(this);
        view.findViewById(R.id.edt_comment).setOnKeyListener(this);

    }

    public void loadComments(final String userId, final String feedId, final int limit, final int page) {

        ((MainActivity) getActivity()).showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.GET_FEED_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        ((MainActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONArray jsonArray = jObject.getJSONObject("comment_data").getJSONArray("comments");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = (JSONObject) jsonArray.get(i);
                                    CommentVo obj = new CommentVo();
                                    obj.setmId(commentObj.getString("id"));
                                    obj.setmUserName(commentObj.getString("username"));
                                    obj.setmCommentFrom(commentObj.getString("comment_from"));
                                    obj.setmUserImageUrl(commentObj.getString("user_pic"));
                                    obj.setmComment(commentObj.getString("comment"));
                                    obj.setmDate(commentObj.getString("created_at"));

                                    mList.add(obj);

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
                    params.put("user_id", userId);
                    params.put("feed_id", feedId);
                    params.put("lmt", limit + "");
                    params.put("offset", page + "");
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

    public void addComment(final String userId, final String feedId, final String comment, final String tag_user) {

        ((MainActivity) getActivity()).showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.ADD_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        ((MainActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONArray jsonArray = jObject.getJSONObject("comment_data").getJSONArray("comments");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = (JSONObject) jsonArray.get(i);
                                    CommentVo obj = new CommentVo();
                                    obj.setmId(commentObj.getString("id"));
                                    obj.setmUserName(commentObj.getString("username"));
                                    obj.setmCommentFrom(commentObj.getString("comment_from"));
                                    obj.setmUserImageUrl(commentObj.getString("user_pic"));
                                    obj.setmComment(commentObj.getString("comment"));
                                    obj.setmDate(commentObj.getString("created_at"));

                                    mList.add(obj);

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
                    params.put("user_id", userId);
                    params.put("feed_id", feedId);
                    params.put("comment", comment);
                    params.put("taged_user_name", tag_user);
                    params.put("comment_date", Utils.getCurrentDate());

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
