package com.kneedleapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kneedleapp.RegistrationActivity;
import com.kneedleapp.adapter.CategoryAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CategoryVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CategoriesFragment extends Fragment implements CategoryAdapter.Sender {
    private String mData;
    private ArrayList<CategoryVo> mList;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View view;
    private ArrayList<String> mSendData;
    private String mType;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categories, container, false);
        Config.LAST_PAGE = "";
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.btn_done), Config.CENTURY_GOTHIC_REGULAR);
        findViews();
        getCategory();


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString("KEY");
        }
    }

    private void findViews() {

        mList = new ArrayList<>();
        mSendData = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_category);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CategoryAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        ((TextView) view.findViewById(R.id.btn_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mType.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                    intent.putStringArrayListExtra("ARRAY", mSendData);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    Bundle bundle = new Bundle();
                    Fragment fragment = new SearchFragment();
                    bundle.putStringArrayList("ARRAY", mSendData);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
                }


            }
        });

    }

    private void getCategory() {
        ((BaseActivity) getContext()).showProgessDialog();
        StringRequest getCategory = new StringRequest(Request.Method.POST, Config.GET_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getContext()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONArray jsonArray = jObject.getJSONArray("category_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    CategoryVo categoryVo = new CategoryVo();
                                    categoryVo.setmId(i);
                                    categoryVo.setmCategoryName(jsonObject.getString("category_name"));

                                    for (CategoryVo obj : RegistrationActivity.mStoreList) {
                                        if (obj.isChecked() && obj.getmCategoryName().trim().equalsIgnoreCase(categoryVo.getmCategoryName())) {
                                            categoryVo.setChecked(true);
                                        }
                                    }


                                    mList.add(categoryVo);
                                }
                                mAdapter.notifyDataSetChanged();
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


                return params;
            }
        };

        getCategory.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getCategory);
    }

    @Override
    public void sendData(ArrayList<String> data) {
        mSendData = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (mType.equalsIgnoreCase("1")) {
                    startActivity(new Intent(getActivity(), RegistrationActivity.class));
                    getActivity().finish();
                }else {
                    getFragmentManager().popBackStack();
                }

                return false;
            }
        });


    }
}
