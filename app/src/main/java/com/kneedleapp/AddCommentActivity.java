package com.kneedleapp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class AddCommentActivity extends BaseActivity {


    private RecyclerView mRecyclerView;
    private ArrayList<CommentVo> mList;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentAdapter mAdapter;
    private static final String FEEDID = "FEEDID";
    private String mComment;
    private EditText mEdtComment;
    private String feedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        feedId = getIntent().getExtras().getString(FEEDID);
        CURRENT_PAGE = "ADD_COMMENT";
        findViews();
        applyFonts();
        if (Utils.isNetworkConnected(AddCommentActivity.this, true)) {
            loadComments(AppPreferences.getAppPreferences(AddCommentActivity.this).getUserId(), feedId, 30, 0);
        }
    }


    private void applyFonts() {
        Utils.setTypeface(AddCommentActivity.this, (TextView) findViewById(R.id.txt_comment), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(AddCommentActivity.this, (TextView) findViewById(R.id.edt_comment), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(AddCommentActivity.this, (TextView) findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_BOLD);
    }

    private void findViews() {
        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_comments);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(AddCommentActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CommentAdapter(AddCommentActivity.this, mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mEdtComment = ((EditText) findViewById(R.id.edt_comment));
        findViewById(R.id.txt_post).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {

            case R.id.txt_post:

                if (mEdtComment.getText().toString().isEmpty()) {
                    Toast.makeText(AddCommentActivity.this, "Please write the comment", Toast.LENGTH_LONG).show();
                } else {
                    mComment = mEdtComment.getText().toString();
                    addComment(AppPreferences.getAppPreferences(AddCommentActivity.this).getUserId(), feedId, mComment, "");
                    mEdtComment.setText("");
                    hideKeyboard();
                }
                break;
        }
    }


    public void loadComments(final String userId, final String feedId, final int limit, final int page) {

        showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.GET_FEED_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONArray jsonArray = jObject.getJSONObject("comment_data").getJSONArray("comments");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = (JSONObject) jsonArray.get(i);
                                    CommentVo obj = new CommentVo();
                                    obj.setmCommentId(commentObj.getString("id"));
                                    obj.setUserName(commentObj.getString("username"));
                                    obj.setUserId(commentObj.getString("user_id"));
                                    obj.setmCommentFrom(commentObj.getString("comment_from"));
                                    obj.setmUserImageUrl(commentObj.getString("user_pic"));
                                    obj.setmComment(commentObj.getString("comment"));
                                    obj.setmDate(commentObj.getString("created_at"));

                                    mList.add(obj);

                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(AddCommentActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AddCommentActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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

        showProgessDialog();
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.ADD_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONArray jsonArray = jObject.getJSONObject("comment_data").getJSONArray("comments");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = (JSONObject) jsonArray.get(i);
                                    CommentVo obj = new CommentVo();
                                    obj.setmCommentId(commentObj.getString("id"));
                                    obj.setUserName(commentObj.getString("username"));
                                    obj.setmCommentFrom(commentObj.getString("comment_from"));
                                    obj.setmUserImageUrl(commentObj.getString("user_pic"));
                                    obj.setmComment(commentObj.getString("comment"));
                                    obj.setmDate(commentObj.getString("created_at"));

                                    mList.add(obj);

                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(AddCommentActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(AddCommentActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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
