package com.kneedleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class CategoriesActivity extends BaseActivity {


    private ArrayList<CategoryVo> mList;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        CURRENT_PAGE = "CATEGORIES";
        Utils.setTypeface(CategoriesActivity.this, (TextView) findViewById(R.id.btn_done), Config.CENTURY_GOTHIC_REGULAR);
        findViews();
        getCategory();

    }

    private void findViews() {

        mList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_category);
        mLayoutManager = new LinearLayoutManager(CategoriesActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CategoryAdapter(CategoriesActivity.this, mList);
        mRecyclerView.setAdapter(mAdapter);
         findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoriesActivity.this.finish();
            }
        });

    }

    private void getCategory() {
        showProgessDialog();
        StringRequest getCategory = new StringRequest(Request.Method.POST, Config.GET_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
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
                                Toast.makeText(CategoriesActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(CategoriesActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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

        RequestQueue queue = Volley.newRequestQueue(CategoriesActivity.this);
        queue.add(getCategory);
    }


}
