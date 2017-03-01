package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class LoginActivity extends BaseActivity {

    String username, password;
    private AppPreferences mPrefernce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        applyFonts();

        mPrefernce = AppPreferences.getAppPreferences(getApplicationContext());

        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.txt_register_user).setOnClickListener(this);
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_name), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_forgot_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_register_user), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_login), Config.CENTURY_GOTHIC_REGULAR);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.txt_forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.btn_login:
                username = ((EditText) findViewById(R.id.txt_name)).getText().toString().trim();
                if (username.isEmpty()) {
                    ((EditText) findViewById(R.id.txt_name)).setError(getString(R.string.error_username_empty));
                    break;
                }
                password = ((EditText) findViewById(R.id.txt_password)).getText().toString().trim();
                if (password.isEmpty()) {
                    ((EditText) findViewById(R.id.txt_password)).setError(getString(R.string.error_password_empty));
                    break;
                }
                if (Utils.isNetworkConnected(this, true)) {
                    LoginData();
                }
                break;
            case R.id.txt_register_user:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }

    public void LoginData() {

        showProgessDialog("Please wait...");
        StringRequest requestLogin = new StringRequest(Request.Method.POST, Config.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response : " + response.toString());
                        dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                mPrefernce.putStringValue(AppPreferences.USER_NAME,username);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(LoginActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(LoginActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("devicekey", "fashion01");
                Log.v(TAG, "Params : " + params.toString());
                return params;
            }
        };

        requestLogin.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue loginqueue = Volley.newRequestQueue(LoginActivity.this);
        loginqueue.add(requestLogin);


    }


}

