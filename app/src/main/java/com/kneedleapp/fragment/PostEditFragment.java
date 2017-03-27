package com.kneedleapp.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.CustomMultipartRequest;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;


public class PostEditFragment extends BaseFragment {
    private View view;
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_post_edit, container, false);

        view.findViewById(R.id.img_back).setOnClickListener(this);

        bitmap = getArguments().getParcelable("POSTIMAGE");
        if (bitmap != null) {
            ((ImageView) view.findViewById(R.id.img_post)).setImageBitmap(bitmap);
        }
        Config.LAST_PAGE = "";

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

        File filesDir = getContext().getFilesDir();
        File imageFile = new File(filesDir, ((BaseActivity) getActivity()).TAG + "_" + Math.random() + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
        params.put("caption", ((EditText) view.findViewById(R.id.txt_caption)).getText().toString().trim());
        params.put("privacy", "");
        params.put("cur_date", Utils.getCurrentDate());


        CustomMultipartRequest requestPost = new CustomMultipartRequest(Config.POST_COMMENT, params, imageFile, "file.jpg", "file",
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                },
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
                                ((MainActivity) getActivity()).selectTab(MainActivity.BottomBarTab.HOME);


                            } else {
                                Toast.makeText(getContext(), "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        requestPost.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue postqueue = Volley.newRequestQueue(getContext());
        postqueue.add(requestPost);
    }
}
