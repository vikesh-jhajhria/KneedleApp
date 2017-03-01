package com.kneedleapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationActivity extends BaseActivity {
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        applyFonts();
        findViews();
        setArrayAdapter();
        showCalender();

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_let_me_in:
                if (!((TextView) findViewById(R.id.txt_name)).getText().toString().isEmpty() && !((TextView) findViewById(R.id.txt_username)).getText().toString().isEmpty() && !((TextView) findViewById(R.id.txt_password)).getText().toString().isEmpty() && !((TextView) findViewById(R.id.txt_dob)).getText().toString().isEmpty()) {
                    RegisterData();
                } else {
                    ((TextView) findViewById(R.id.textview_error_show)).setVisibility(View.VISIBLE);

                    ((TextView) findViewById(R.id.txt_name)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_name_red), null, null, null);
                    ((TextView) findViewById(R.id.txt_username)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_username_red), null, null, null);
                    ((TextView) findViewById(R.id.txt_password)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_password_red), null, null, null);
                    ((TextView) findViewById(R.id.txt_dob)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_calender_red), null, null, null);
                }
                break;
            case R.id.txt_dob:
                new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.ll_homme:
                ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.ic_homme_red);
                ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.ic_femme_white);
                break;
            case R.id.ll_femme:
                ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.ic_femme_red);
                ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.ic_homme_white);
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
                params.put("profiletype", "1");
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
    ArrayList<String> spinnerDataList;
    private void setArrayAdapter() {

        spinnerDataList = new ArrayList<>();
        spinnerDataList.add("PROFILE TYPE");
        spinnerDataList.add("PROFILE 1");
        spinnerDataList.add("PROFILE 2");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegistrationActivity.this, R.layout.layout_spinner_item, spinnerDataList);
        ((Spinner) findViewById(R.id.spinner_profile_type)).setAdapter(spinnerAdapter);
    }

    private void showCalender() {

        Log.e("aman", "edittext");
        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

    }

    private void updateLabel() {
        String myFormat = "MM / dd / yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        ((TextView) findViewById(R.id.txt_dob)).setText(sdf.format(myCalendar.getTime()));
    }

    private void findViews() {
        findViewById(R.id.btn_let_me_in).setOnClickListener(this);
        ((Spinner) findViewById(R.id.spinner_profile_type)).getBackground().setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        ((Spinner) findViewById(R.id.spinner_profile_type)).setPrompt("PROFILE TYPE");
        findViewById(R.id.ll_homme).setOnClickListener(this);
        findViewById(R.id.ll_femme).setOnClickListener(this);
        findViewById(R.id.txt_dob).setOnClickListener(this);
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.textview_error_show), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_name), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_dob), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_homme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_femme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_let_me_in), Config.CENTURY_GOTHIC_REGULAR);
    }








    public class SpinnerAdapter extends ArrayAdapter<String>{

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.txt_item);
            Utils.setTypeface(RegistrationActivity.this, label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(spinnerDataList.get(position));
            return row;
        }
    }

}
