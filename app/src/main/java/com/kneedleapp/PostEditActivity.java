package com.kneedleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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

import static com.kneedleapp.utils.Config.postBitmap;

public class PostEditActivity extends BaseActivity {

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.rl_post_selected).setVisibility(View.VISIBLE);
        bitmap = postBitmap;
        if (bitmap != null) {
            ((ImageView) findViewById(R.id.img_post)).setImageBitmap(bitmap);
        }
        CURRENT_PAGE = "POST";

        applyFonts();

        ((TextView) findViewById(R.id.txt_post)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });
    }


    private void applyFonts() {
        Utils.setTypeface(PostEditActivity.this, (TextView) findViewById(R.id.txt_new_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(PostEditActivity.this, (TextView) findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(PostEditActivity.this, (TextView) findViewById(R.id.txt_caption), Config.CENTURY_GOTHIC_REGULAR);
    }


    public void postComment() {
        showProgessDialog("Please wait...");

        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, ((BaseActivity) PostEditActivity.this).TAG + "_" + Math.random() + ".jpg");

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
        params.put("user_id", AppPreferences.getAppPreferences(PostEditActivity.this).getStringValue(AppPreferences.USER_ID));
        params.put("caption", ((EditText) findViewById(R.id.txt_caption)).getText().toString().trim());
        params.put("privacy", "");
        params.put("cur_date", Utils.getCurrentDate());


        CustomMultipartRequest requestPost = new CustomMultipartRequest(Config.POST_COMMENT, params, imageFile, "file.jpg", "file",
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ((BaseActivity) PostEditActivity.this).dismissProgressDialog();
                        Toast.makeText(PostEditActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) PostEditActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(PostEditActivity.this, "no data available", Toast.LENGTH_SHORT).show();
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

        RequestQueue postqueue = Volley.newRequestQueue(PostEditActivity.this);
        postqueue.add(requestPost);
    }
}
