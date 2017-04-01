package com.kneedleapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
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
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class InfoFragment extends BaseFragment {
    private View view;
    String type;

    public static InfoFragment newInstance(String infoType) {
        InfoFragment fragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("INFO_TYPE", infoType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("INFO_TYPE");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_title), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_detail), Config.CENTURY_GOTHIC_REGULAR);

        if(type.equalsIgnoreCase("TERMS")){
             ((TextView) view.findViewById(R.id.txt_title)).setText(getResources().getString(R.string.terms_of_service));
            if(Utils.isNetworkConnected(getActivity(),true)){
                termsCondition();
            }
        } else if(type.equalsIgnoreCase("POLICY")){
            ((TextView) view.findViewById(R.id.txt_title)).setText(getResources().getString(R.string.privacy_policy));
            if(Utils.isNetworkConnected(getActivity(),true)){
                termsCondition();
            }
        }
        view.findViewById(R.id.img_back).setOnClickListener(this);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(InfoFragment.this);
            }
        }, 500);

    }

    public void termsCondition() {
        ((BaseActivity) getActivity()).showProgessDialog();
        StringRequest termsCondition = new StringRequest(Request.Method.POST, Config.TERMS_CONDITION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
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
                                ((TextView) view.findViewById(R.id.txt_detail)).setText(mTxtTerms);


                            } else {
                                Toast.makeText(getContext(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity)getActivity()).dismissProgressDialog();
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

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(termsCondition);
    }
}
