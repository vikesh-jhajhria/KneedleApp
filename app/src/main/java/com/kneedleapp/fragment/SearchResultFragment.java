package com.kneedleapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.R;
import com.kneedleapp.adapter.SearchResultAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.SearchResultVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;


public class SearchResultFragment extends BaseFragment {


    private SearchResultAdapter searchResultAdapter;
    private ArrayList<SearchResultVO> mList;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String mSearchText, mCategory, mZip;
    private View mView;


    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mSearchText = bundle.getString("SEARCHTEXT", "");
            mCategory = bundle.getString("CATEGORY", "");
            mZip = bundle.getString("ZIP", "");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search_result, container, false);
        applyFonts(mView);
        Config.LAST_PAGE = "";

        recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mList = new ArrayList<>();

        getSearchItem();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        searchResultAdapter = new SearchResultAdapter(mList, getActivity());
        recyclerView.setAdapter(searchResultAdapter);


        ((EditText) mView.findViewById(R.id.txt_title)).setText(mSearchText);


        ((EditText) mView.findViewById(R.id.txt_title)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchText = ((EditText) mView.findViewById(R.id.txt_title)).getText().toString().trim();
                    getSearchItem();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        ((ImageView) mView.findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) mView.findViewById(R.id.txt_title)).setText("");
            }
        });


        ((EditText) mView.findViewById(R.id.txt_title)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((ImageView) mView.findViewById(R.id.img_close)).setVisibility(View.VISIBLE);
                if (((EditText) mView.findViewById(R.id.txt_title)).getText().toString().trim().equals("")) {

                    ((ImageView) mView.findViewById(R.id.img_close)).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSearchItem();
                ((SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
            }
        });

        return mView;
    }


    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) mView.findViewById(R.id.txt_title), Config.CENTURY_GOTHIC_REGULAR);
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
                    fragmentManager.popBackStack();
                    return true;
                }
                return false;
            }
        });
    }


    public void getSearchItem() {
        ((BaseActivity) getContext()).showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_SEARCH_ITEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getContext()).dismissProgressDialog();
                        try {
                            mList.clear();
                            searchResultAdapter.notifyDataSetChanged();

                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                JSONArray jsonArray = jObject.getJSONArray("search_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    SearchResultVO searchResultVO = new SearchResultVO();
                                    searchResultVO.setmUserId(jsonObject.getString("user_id"));
                                    searchResultVO.setmUserName(jsonObject.getString("username"));
                                    searchResultVO.setmFullName(jsonObject.getString("fullname"));
                                    searchResultVO.setmProfileType(jsonObject.getString("profiletype"));
                                    searchResultVO.setmCityName(jsonObject.getString("city"));
                                    searchResultVO.setmImgUrl(jsonObject.getString("profile_pic"));
                                    mList.add(searchResultVO);
                                }

                                searchResultAdapter.notifyDataSetChanged();


                            } else {
                                Toast.makeText(getContext(), "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (mSearchText != null && !mSearchText.isEmpty()) {
                    params.put("searchtext", mSearchText);
                    params.put("category", mCategory);
                    params.put("zipcode", mZip);
                } else {
                    params.put("searchtext", ((EditText) mView.findViewById(R.id.txt_title)).getText().toString().trim());
                    Log.e("aman", "serach text");
                }
                Log.v("Kneedle", "Params: " + params);
                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(getContext());
        feedqueue.add(requestFeed);
    }
}
