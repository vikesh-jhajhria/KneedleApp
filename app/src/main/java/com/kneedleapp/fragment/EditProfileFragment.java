package com.kneedleapp.fragment;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.kneedleapp.BaseActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;


public class EditProfileFragment extends BaseFragment implements View.OnClickListener {
    ArrayList<String> spinnerDataList;
    private View view;
    private String mUserName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        applyFonts(view);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_location).setOnClickListener(this);
        view.findViewById(R.id.img_homme).setOnClickListener(this);
        view.findViewById(R.id.img_femme).setOnClickListener(this);
        ((Spinner) view.findViewById(R.id.spinner_profile_type)).getBackground().setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);

        spinnerDataList = new ArrayList<>();
        spinnerDataList.add("PROFILE TYPE");
        spinnerDataList.add("PROFILE 1");
        spinnerDataList.add("PROFILE 2");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, spinnerDataList);
        ((Spinner) view.findViewById(R.id.spinner_profile_type)).setAdapter(spinnerAdapter);

        ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female);
        ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male);
        editProfile();


        return view;
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
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(getActivity(), label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(spinnerDataList.get(position));
            return row;
        }
    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()) {
            case R.id.img_location:
                Fragment fragment = new LocationFragment();
                fragmentManager.beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();
                break;
            case R.id.img_femme:
                ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female_red);
                ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male);
                break;
            case R.id.img_homme:
                ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female);
                ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male_red);
                break;
        }
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_edit_profile), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_name), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_edit), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_bio_title), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_bio), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_email), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_gender), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_homme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_femme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.btn_save_changes), Config.CENTURY_GOTHIC_REGULAR);
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
                    fragmentManager.popBackStack();

                    return true;
                }
                return false;
            }
        });

    }

    public void editProfile() {
        ((BaseActivity) getActivity()).showProgessDialog("Please wait...");
        StringRequest editProfile = new StringRequest(Request.Method.POST, Config.GET_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                JSONObject jsonObject = jObject.getJSONObject("user_data");
                                ((TextView) view.findViewById(R.id.txt_name)).setText(jsonObject.getString("fullname"));
                                ((TextView) view.findViewById(R.id.txt_username)).setText(jsonObject.getString("username"));
                                ((TextView) view.findViewById(R.id.txt_password)).setText(jsonObject.getString("password"));
                                ((TextView) view.findViewById(R.id.txt_bio)).setText(jsonObject.getString("bio"));
                                ((TextView) view.findViewById(R.id.txt_email)).setText(jsonObject.getString("email"));
                                if (jsonObject.getString("gender").equalsIgnoreCase("male")) {
                                    ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male_red);
                                } else {
                                    ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female_red);
                                }
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_NAME));
                params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        editProfile.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(editProfile);
    }


}
