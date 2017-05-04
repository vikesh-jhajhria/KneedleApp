package com.kneedleapp;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends BaseActivity {

    String type;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        CURRENT_PAGE = "INFO";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("INFO_TYPE");
        }

        Utils.setTypeface(InfoActivity.this, (TextView) findViewById(R.id.txt_title), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(InfoActivity.this, (TextView) findViewById(R.id.txt_detail), Config.CENTURY_GOTHIC_REGULAR);

        if (type.equalsIgnoreCase("TERMS")) {
            ((TextView) findViewById(R.id.txt_title)).setText(getResources().getString(R.string.terms_of_service));
            if (Utils.isNetworkConnected(InfoActivity.this, true)) {
                termsCondition();
            }
        } else if (type.equalsIgnoreCase("POLICY")) {
            ((TextView) findViewById(R.id.txt_title)).setText(getResources().getString(R.string.privacy_policy));
            if (Utils.isNetworkConnected(InfoActivity.this, true)) {
                privacyPolicy();
            }
        }
        findViewById(R.id.img_back).setOnClickListener(this);

    }


    public void termsCondition() {
        ((BaseActivity) InfoActivity.this).showProgessDialog();
        StringRequest termsCondition = new StringRequest(Request.Method.POST, Config.TERMS_CONDITION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) InfoActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONObject jsonObject = jObject.getJSONObject("terms_data");

                                byte[] data = Base64.decode(jsonObject.getString("message"), Base64.DEFAULT);
                                String mTxtTerms = null;
                                try {
                                    mTxtTerms = new String(data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                ((TextView) findViewById(R.id.txt_detail)).setText(mTxtTerms);


                            } else {
                                Toast.makeText(InfoActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(InfoActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity) InfoActivity.this).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        termsCondition.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(InfoActivity.this);
        queue.add(termsCondition);
    }
    public void privacyPolicy() {
        ((BaseActivity) InfoActivity.this).showProgessDialog();
        StringRequest termsCondition = new StringRequest(Request.Method.POST, Config.PRIVACY_POLICY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) InfoActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONObject jsonObject = jObject.getJSONObject("privacy_data");

                                byte[] data = Base64.decode(jsonObject.getString("message"), Base64.DEFAULT);
                                String mTxtTerms = null;
                                try {
                                    mTxtTerms = new String(data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                ((TextView) findViewById(R.id.txt_detail)).setText(mTxtTerms);


                            } else {
                                Toast.makeText(InfoActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(InfoActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity) InfoActivity.this).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        termsCondition.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(InfoActivity.this);
        queue.add(termsCondition);
    }
}
