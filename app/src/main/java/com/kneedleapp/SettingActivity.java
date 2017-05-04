package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends BaseActivity {

    private String userId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("USER_ID");
        }
        findViewById(R.id.txt_logout).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_blocked_user).setOnClickListener(this);
        findViewById(R.id.txt_report_problem).setOnClickListener(this);
        findViewById(R.id.txt_privacy_policy).setOnClickListener(this);
        findViewById(R.id.txt_terms_of_service).setOnClickListener(this);
        CURRENT_PAGE = "SETTING";

        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_options), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_support), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_report_problem), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_terms_of_service), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_privacy_policy), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_prefernces), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_blocked_user), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_push_noti), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_save_original_photo), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SettingActivity.this, (TextView) findViewById(R.id.txt_logout), Config.CENTURY_GOTHIC_REGULAR);


    }

    

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.txt_logout:
                Utils.showDecisionDialog(SettingActivity.this, "Logout", getString(R.string.logout_message), new Utils.AlertCallback() {

                    public void callback() {
                        if (Utils.isNetworkConnected(SettingActivity.this, true)) {
                            logout();
                        }
                    }
                });

                break;
            case R.id.txt_report_problem:
              /*  Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"some@email.address"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));*/
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                startActivity(emailIntent);
                break;
            case R.id.txt_privacy_policy:
                startActivity(new Intent(getApplicationContext(), InfoActivity.class)
                        .putExtra("INFO_TYPE","POLICY")
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.txt_terms_of_service:
                startActivity(new Intent(getApplicationContext(), InfoActivity.class)
                        .putExtra("INFO_TYPE","TERMS")
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.txt_blocked_user:
                startActivity(new Intent(getApplicationContext(), BlockedUsersActivity.class)
                        .putExtra("USER_ID",userId)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
        }
    }

    public void logout() {
        ((BaseActivity) SettingActivity.this).showProgessDialog();
        StringRequest logout = new StringRequest(Request.Method.POST, Config.LOG_OUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) SettingActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                AppPreferences.getAppPreferences(SettingActivity.this).setUserName("");
                                AppPreferences.getAppPreferences(SettingActivity.this).setUserId("");
                                SettingActivity.this.finishAffinity();

                            } else {
                                Toast.makeText(SettingActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(SettingActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity) SettingActivity.this).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", AppPreferences.getAppPreferences(SettingActivity.this).getStringValue(AppPreferences.USER_ID));


                return params;
            }
        };

        logout.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(SettingActivity.this);
        queue.add(logout);
    }


}
