package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends BaseActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViewById(R.id.btn_send_email).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_send_email:
                email = ((EditText) findViewById(R.id.txt_email)).getText().toString().trim();
                if (email.isEmpty()) {
                    ((EditText) findViewById(R.id.txt_email)).setError(getString(R.string.error_email_empty));
                    break;
                }
                if (!Utils.isEmailValid(email)) {
                    ((EditText) findViewById(R.id.txt_email)).setError(getString(R.string.error_email_valid));
                    break;
                }
                if (Utils.isNetworkConnected(this, true)) {
                    ForgotPasswordData();
                }
                break;
        }
    }

    public void ForgotPasswordData() {

        showProgessDialog("Please wait...");
        StringRequest requestForgotPassword = new StringRequest(Request.Method.POST, Config.FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {

                                Log.e(TAG, "responce forgot...::>>>" + response);
                                startActivity(new Intent(ForgotPasswordActivity.this, MailSentActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ForgotPasswordActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                Log.e(TAG, "params forgot...::>>>" + params);
                return params;
            }
        };

        requestForgotPassword.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue forgotpasswordqueue = Volley.newRequestQueue(ForgotPasswordActivity.this);
        forgotpasswordqueue.add(requestForgotPassword);


    }

}
