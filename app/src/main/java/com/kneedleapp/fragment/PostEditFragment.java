package com.kneedleapp.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;


public class PostEditFragment extends BaseFragment {
    private View view;
    private byte[] byteArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_post_edit, container, false);

        view.findViewById(R.id.img_back).setOnClickListener(this);

        Bitmap bitmap = getArguments().getParcelable("POSTIMAGE");
        if (bitmap != null) {
            ((ImageView) view.findViewById(R.id.img_post)).setImageBitmap(bitmap);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        Log.e("Byte Array", "" + byteArray);


        applyFonts(view);

        ((TextView) view.findViewById(R.id.txt_post)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });


        return view;
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_new_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_caption), Config.CENTURY_GOTHIC_REGULAR);
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
                    fragmentManager.popBackStackImmediate();

                    return true;
                }
                return false;
            }
        });

    }

    public void postComment() {
        ((BaseActivity) getActivity()).showProgessDialog("Please wait...");
        StringRequest requestPost = new StringRequest(Request.Method.POST, Config.POST_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                             /*   Fragment fragment = new HomeFragment();
                                getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();*/
                                ((MainActivity) getActivity()).selectTab(BaseActivity.BottomBarTab.HOME);


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
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
                params.put("caption", ((EditText) view.findViewById(R.id.txt_caption)).getText().toString().trim());
                params.put("privacy", "");
                params.put("file", "" +"{\"serialDataByte\":\""+ new String(byteArray) +"\"}");
                try {
                    params.put("cur_date", Utils.getCurrentDate());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };

        requestPost.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue postqueue = Volley.newRequestQueue(getContext());
        postqueue.add(requestPost);
    }
}
