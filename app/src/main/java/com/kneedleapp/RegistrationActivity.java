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
import com.kneedleapp.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViewById(R.id.btn_let_me_in).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_let_me_in:
                RegisterData();
                break;
        }
    }

    public void RegisterData() {

        showProgessDialog("Please wait...");
        StringRequest requestRegister = new StringRequest(Request.Method.POST, Config.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        try {
                            Log.e("responce...::>>", response);
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(RegistrationActivity.this, "not successd", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(RegistrationActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", ((TextView) findViewById(R.id.txt_name)).getText().toString().trim());
                params.put("profiletype", ((EditText) findViewById(R.id.edt_profile_type)).getText().toString().trim());
                params.put("companyInfo", "vertex plus");
                params.put("city", "jaipur");
                params.put("password", ((TextView) findViewById(R.id.txt_password)).getText().toString().trim());
                params.put("gender", "male");
                params.put("state", "rajasthan");
                params.put("country", "india");
                params.put("username", ((TextView) findViewById(R.id.txt_username)).getText().toString().trim());
                params.put("latitude", "0.00");
                params.put("langitude", "0.00");
                params.put("email", "vikeshkumar@gmail.com");
                params.put("devicekey", "aman");
                params.put("category", "job");


                return params;
            }
        };

        requestRegister.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue registerqueue = Volley.newRequestQueue(RegistrationActivity.this);
        registerqueue.add(requestRegister);
    }

}
