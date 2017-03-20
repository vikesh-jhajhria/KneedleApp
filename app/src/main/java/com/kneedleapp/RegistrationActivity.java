package com.kneedleapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationActivity extends BaseActivity {
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private String gender = "male";
    private WebSettings wSettings;
    private ProgressDialog pd = null;
    private Dialog builder;
    private String mTxtTerms;
    private ProgressBar progressBar;
    private ArrayList<String> spinnerDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fetchUserLocation();

        applyFonts();
        findViews();
        setArrayAdapter();

        String terms = "I agree with Terms and Conditions";

        SpannableString spannableString = new SpannableString(terms);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 13, terms.length(), 0);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                builder = new Dialog(RegistrationActivity.this);

                builder.setContentView(R.layout.popup_window_webview);
                builder.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner_webview));
                progressBar = (ProgressBar) builder.findViewById(R.id.progressBar1);
                termsCondition();
                builder.show();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        };
        spannableString.setSpan(clickableSpan, 13, terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.txt_terms_condition)).setText(spannableString);
        ((TextView) findViewById(R.id.txt_terms_condition)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_let_me_in:
                if (!((EditText) findViewById(R.id.txt_name)).getText().toString().isEmpty() && !((EditText) findViewById(R.id.txt_username)).getText().toString().isEmpty() && !((EditText) findViewById(R.id.txt_password)).getText().toString().isEmpty() && !((EditText) findViewById(R.id.txt_email)).getText().toString().isEmpty()) {

                    if (!((CheckBox) findViewById(R.id.checkbox)).isChecked()) {
                        Toast.makeText(RegistrationActivity.this, "Please sign term and conditions", Toast.LENGTH_LONG).show();
                    } else if (!BaseActivity.isValidEmail(((EditText) findViewById(R.id.txt_email)).getText().toString().trim())) {
                        Toast.makeText(RegistrationActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
                    } else if (Utils.isNetworkConnected(this, true)) {
                        RegisterData();
                    }
                } else {
                    ((TextView) findViewById(R.id.textview_error_show)).setVisibility(View.VISIBLE);

                    ((EditText) findViewById(R.id.txt_name)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_name_red), null, null, null);
                    ((EditText) findViewById(R.id.txt_username)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_username_red), null, null, null);
                    ((EditText) findViewById(R.id.txt_password)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_password_red), null, null, null);
                    ((EditText) findViewById(R.id.txt_email)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.email_red), null, null, null);

                }
                break;

            case R.id.ll_homme:
                gender = "male";
                ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.ic_homme_red);
                ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.ic_femme_white);
                break;
            case R.id.ll_femme:
                gender = "female";
                ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.ic_femme_red);
                ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.ic_homme_white);
                break;
        }
    }

    public void RegisterData() {
        showProgessDialog();
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
                            }
                            Toast.makeText(RegistrationActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(RegistrationActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        dismissProgressDialog();
                        Log.e("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", ((EditText) findViewById(R.id.txt_name)).getText().toString().trim());
                params.put("profiletype", ((Spinner) findViewById(R.id.spinner_profile_type)).getSelectedItem().toString());
                params.put("companyInfo", "");
                params.put("city", "");
                params.put("password", ((EditText) findViewById(R.id.txt_password)).getText().toString().trim());
                params.put("gender", gender);
                params.put("state", "");
                params.put("country", "");
                params.put("username", ((EditText) findViewById(R.id.txt_username)).getText().toString().trim());
                params.put("latitude", preferences.getLatitude());
                params.put("langitude", preferences.getLongitude());
                params.put("email", ((EditText) findViewById(R.id.txt_email)).getText().toString().trim());
                params.put("devicekey", preferences.getFirebaseId());
                params.put("category", "profile");

                Log.v(TAG, "Params>> " + params.toString());
                return params;
            }
        };

        requestRegister.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.v(TAG, "URL>> " + requestRegister.getUrl());
        RequestQueue registerqueue = Volley.newRequestQueue(RegistrationActivity.this);
        registerqueue.add(requestRegister);
    }


    private void setArrayAdapter() {

        spinnerDataList = new ArrayList<>();
        spinnerDataList.add("PROFILE TYPE");
        spinnerDataList.add("PROFILE 1");
        spinnerDataList.add("PROFILE 2");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegistrationActivity.this, R.layout.layout_spinner_item, spinnerDataList);
        ((Spinner) findViewById(R.id.spinner_profile_type)).setAdapter(spinnerAdapter);
    }


    private void findViews() {
        findViewById(R.id.btn_let_me_in).setOnClickListener(this);
        ((Spinner) findViewById(R.id.spinner_profile_type)).getBackground().setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        ((Spinner) findViewById(R.id.spinner_profile_type)).setPrompt("PROFILE TYPE");
        findViewById(R.id.ll_homme).setOnClickListener(this);
        findViewById(R.id.ll_femme).setOnClickListener(this);
        findViewById(R.id.txt_email).setOnClickListener(this);
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.textview_error_show), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (EditText) findViewById(R.id.txt_name), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (EditText) findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (EditText) findViewById(R.id.txt_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_homme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_femme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (Button) findViewById(R.id.btn_let_me_in), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (EditText) findViewById(R.id.txt_email), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_terms_condition), Config.CENTURY_GOTHIC_REGULAR);
    }


    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(RegistrationActivity.this, label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(spinnerDataList.get(position));
            return row;
        }
    }

    public class WebClientClass extends WebViewClient {


        @Override
        public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!pd.isShowing())
                pd.show();

        }

        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {
            super.onPageFinished(view, url);
            if (pd.isShowing())
                pd.dismiss();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (builder != null) {
            builder.dismiss();
        }
    }

    public void termsCondition() {

        StringRequest termsCondition = new StringRequest(Request.Method.POST, Config.TERMS_CONDITION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                JSONObject jsonObject = jObject.getJSONObject("terms_data");

                                byte[] data = Base64.decode(jsonObject.getString("message"), Base64.DEFAULT);
                                try {
                                    mTxtTerms = new String(data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                ((TextView) builder.findViewById(R.id.txt_terms)).setText(mTxtTerms);


                            } else {
                                Toast.makeText(RegistrationActivity.this, "no data available", Toast.LENGTH_SHORT).show();
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
                        Log.d("error", volleyError.getMessage());
                        progressBar.setVisibility(View.GONE);
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

        RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
        queue.add(termsCondition);
    }
}



