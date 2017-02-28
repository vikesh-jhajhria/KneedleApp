package com.kneedleapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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

        findViews();
        setArrayAdapter();
        showCalender();

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_let_me_in:
                if (!((TextView) findViewById(R.id.txt_name)).getText().toString().isEmpty() && !((TextView) findViewById(R.id.txt_username)).getText().toString().isEmpty() && !((TextView) findViewById(R.id.txt_password)).getText().toString().isEmpty() && !((TextView) findViewById(R.id.txt_db)).getText().toString().isEmpty()) {
                    RegisterData();
                } else {
                    ((TextView) findViewById(R.id.textview_error_show)).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_db:
                new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

    private void setArrayAdapter() {

        ArrayList<String> spinnerDataList = new ArrayList<>();
        spinnerDataList.add("One");
        spinnerDataList.add("Two");
        spinnerDataList.add("Three");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(RegistrationActivity.this,R.layout.spinner_item, R.id.txt_spinner_item, spinnerDataList);
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
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        ((TextView) findViewById(R.id.txt_db)).setText(sdf.format(myCalendar.getTime()));
    }

    private void findViews() {
        findViewById(R.id.btn_let_me_in).setOnClickListener(this);
        ((Spinner) findViewById(R.id.spinner_profile_type)).getBackground().setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        ((Spinner) findViewById(R.id.spinner_profile_type)).setPrompt("PROFILE TYPE");
        findViewById(R.id.txt_db).setOnClickListener(this);
    }

}
