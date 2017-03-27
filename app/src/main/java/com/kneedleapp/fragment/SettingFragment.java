package com.kneedleapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.kneedleapp.LoginActivity;
import com.kneedleapp.R;
import com.kneedleapp.WebViewActivity;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);

        mView.findViewById(R.id.txt_logout).setOnClickListener(this);
        mView.findViewById(R.id.img_back).setOnClickListener(this);
        mView.findViewById(R.id.txt_report_problem).setOnClickListener(this);
        mView.findViewById(R.id.txt_privacy_policy).setOnClickListener(this);
        mView.findViewById(R.id.txt_terms_of_service).setOnClickListener(this);
        Config.LAST_PAGE = "";

        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_options), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_photos_you_have_liked), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_support), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_report_problem), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_privacy_policy), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_prefernces), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_share_settings), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_push_noti), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_save_original_photo), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) mView.findViewById(R.id.txt_logout), Config.CENTURY_GOTHIC_REGULAR);


        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_logout:
                logout();
                break;
            case R.id.img_back:
                getFragmentManager().popBackStack();
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
                startActivity(new Intent(getActivity(), WebViewActivity.class));
                break;
            case R.id.txt_terms_of_service:
                startActivity(new Intent(getActivity(), WebViewActivity.class));
                break;
        }
    }

    public void logout() {
        ((BaseActivity) getContext()).showProgessDialog();
        StringRequest logout = new StringRequest(Request.Method.POST, Config.LOG_OUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getContext()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                AppPreferences.getAppPreferences(getContext()).setUserName("");
                                AppPreferences.getAppPreferences(getContext()).setUserId("");
                                getActivity().finishAffinity();

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
                        ((BaseActivity) getContext()).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));


                return params;
            }
        };

        logout.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(logout);
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });

    }
}
