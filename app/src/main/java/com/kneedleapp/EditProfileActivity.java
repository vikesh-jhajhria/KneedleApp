package com.kneedleapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.CustomMultipartRequest;
import com.kneedleapp.utils.ImageCompression;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CountryVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    ArrayList<CountryVO> countrySpinnerList;
    ArrayList<CountryVO> stateSpinnerList;
    ArrayList<CountryVO> citySpinnerList;

    SpinnerAdapter countrySpinnerAdapter;
    SpinnerAdapter stateSpinnerAdapter;
    SpinnerAdapter citySpinnerAdapter;

    Spinner citySpinner, stateSpinner, countrySpinner, profileSpinner;

    private Bitmap bitmap;
    String imagePath = "";
    String gender = "male", profiletype = "", country = "", state = "", city = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        findViewById(R.id.rl_profile_selected).setVisibility(View.VISIBLE);
        CURRENT_PAGE = "PROFILE";
        applyFonts();
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_location).setOnClickListener(this);
        findViewById(R.id.img_homme).setOnClickListener(this);
        findViewById(R.id.img_femme).setOnClickListener(this);
        findViewById(R.id.btn_save_changes).setOnClickListener(this);
        findViewById(R.id.img_profile).setOnClickListener(this);
        findViewById(R.id.txt_edit).setOnClickListener(this);

        citySpinner = ((Spinner) findViewById(R.id.spinner_city));
        stateSpinner = ((Spinner) findViewById(R.id.spinner_state));
        countrySpinner = ((Spinner) findViewById(R.id.spinner_country));
        profileSpinner = ((Spinner) findViewById(R.id.spinner_profile_type));

        countrySpinnerList = new ArrayList<>();
        stateSpinnerList = new ArrayList<>();
        citySpinnerList = new ArrayList<>();
        countrySpinnerList.add(new CountryVO("COUNTRY"));
        stateSpinnerList.add(new CountryVO("STATE"));
        citySpinnerList.add(new CountryVO("CITY"));

        Utils.SpinnerAdapter profileSpinnerAdapter = new Utils.SpinnerAdapter(EditProfileActivity.this, R.layout.layout_spinner_item, Config.PROFILE_TYPE);
        profileSpinner.setAdapter(profileSpinnerAdapter);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profiletype = Config.PROFILE_TYPE.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        countrySpinnerAdapter = new SpinnerAdapter(EditProfileActivity.this, R.layout.layout_spinner_item, countrySpinnerList);
        ((Spinner) findViewById(R.id.spinner_country)).setAdapter(countrySpinnerAdapter);
        ((Spinner) findViewById(R.id.spinner_country)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateSpinnerList.clear();
                CountryVO state = new CountryVO("STATE");
                stateSpinnerList.add(state);
                if (stateSpinner != null)
                    stateSpinner.setSelection(0);

                citySpinnerList.clear();
                CountryVO city = new CountryVO("CITY");
                citySpinnerList.add(city);
                if (citySpinner != null)
                    citySpinner.setSelection(0);

                if (Utils.isNetworkConnected(EditProfileActivity.this, true)) {
                    country = countrySpinnerList.get(position).getName();
                    getDropdownValues(countrySpinnerList.get(position).getCountryID(), "STATE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateSpinnerAdapter = new SpinnerAdapter(EditProfileActivity.this, R.layout.layout_spinner_item, stateSpinnerList);
        ((Spinner) findViewById(R.id.spinner_state)).setAdapter(stateSpinnerAdapter);
        ((Spinner) findViewById(R.id.spinner_state)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySpinnerList.clear();
                CountryVO city = new CountryVO("CITY");
                citySpinnerList.add(city);
                if (citySpinner != null)
                    citySpinner.setSelection(0);
                if (Utils.isNetworkConnected(EditProfileActivity.this, true)) {
                    state = stateSpinnerList.get(position).getName();
                    getDropdownValues(stateSpinnerList.get(position).getStateID(), "CITY");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpinnerAdapter = new SpinnerAdapter(EditProfileActivity.this, R.layout.layout_spinner_item, citySpinnerList);
        ((Spinner) findViewById(R.id.spinner_city)).setAdapter(citySpinnerAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = citySpinnerList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.female);
        ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.male);
        if (Utils.isNetworkConnected(this, true)) {
            editProfile();
        }
    }

    public class SpinnerAdapter extends ArrayAdapter<CountryVO> {

        private ArrayList<CountryVO> list;

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<CountryVO> objects) {
            super(context, textViewResourceId, objects);
            list = objects;
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
            LayoutInflater inflater = EditProfileActivity.this.getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(EditProfileActivity.this, label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(list.get(position).getName());
            return row;
        }
    }

    @Override
    public void onClick(View mView) {
        super.onClick(mView);
        switch (mView.getId()) {
            case R.id.img_femme:
                gender = "female";
                ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.female_red);
                ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.male);
                break;
            case R.id.img_homme:
                gender = "male";
                ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.female);
                ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.male_red);
                break;
            case R.id.img_profile:
            case R.id.txt_edit:
                selectImage();
                break;
            case R.id.btn_save_changes:
                if (Utils.isNetworkConnected(EditProfileActivity.this, true)) {
                    updateProfile();
                }
                break;
        }
    }

    private void applyFonts() {
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_edit_profile), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_company_name), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_edit), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_bio_title), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_bio), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_email), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_gender), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_homme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_femme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.btn_save_changes), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_zip), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(EditProfileActivity.this, (TextView) findViewById(R.id.txt_website), Config.CENTURY_GOTHIC_REGULAR);
    }


    public void editProfile() {
        ((BaseActivity) EditProfileActivity.this).showProgessDialog();
        StringRequest editProfile = new StringRequest(Request.Method.POST, Config.GET_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) EditProfileActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                JSONObject jsonObject = jObject.getJSONObject("user_data");
                                ((TextView) findViewById(R.id.txt_company_name)).setText(jsonObject.getString("fullname"));
                                ((TextView) findViewById(R.id.txt_username)).setText(jsonObject.getString("username"));
                                ((TextView) findViewById(R.id.txt_password)).setText(jsonObject.getString("password"));
                                ((TextView) findViewById(R.id.txt_bio)).setText(jsonObject.getString("bio"));
                                ((TextView) findViewById(R.id.txt_email)).setText(jsonObject.getString("email"));
                                ((TextView) findViewById(R.id.txt_website)).setText(jsonObject.getString("website"));
                                ((TextView) findViewById(R.id.txt_company)).setText(jsonObject.getString("company_info"));
                                ((TextView) findViewById(R.id.txt_zip)).setText(jsonObject.getString("zipcode"));
                                profiletype = jsonObject.getString("profiletype");
                                country = jsonObject.getString("country");
                                state = jsonObject.getString("state");
                                city = jsonObject.getString("city");
                                if (jsonObject.getString("gender").equalsIgnoreCase("male")) {
                                    gender = "male";
                                    ((ImageView) findViewById(R.id.img_homme)).setImageResource(R.drawable.male_red);
                                } else {
                                    gender = "female";
                                    ((ImageView) findViewById(R.id.img_femme)).setImageResource(R.drawable.female_red);
                                }

                                if(!jsonObject.getString("image").isEmpty()) {
                                    Glide.with(EditProfileActivity.this).load(Config.USER_IMAGE_URL
                                            + jsonObject.getString("image")).asBitmap().placeholder(R.drawable.profile_pic)
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                    ((ImageView) findViewById(R.id.img_profile)).setImageBitmap(resource);
                                                }
                                            });
                                }
                                for (String pt : Config.PROFILE_TYPE) {
                                    if (pt.equalsIgnoreCase(profiletype)) {
                                        profileSpinner.setSelection(Config.PROFILE_TYPE.indexOf(pt));
                                        break;
                                    }
                                }
                                if (Utils.isNetworkConnected(EditProfileActivity.this, true)) {
                                    getDropdownValues("1", "COUNTRY");
                                }

                            } else {
                                Toast.makeText(EditProfileActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(EditProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", AppPreferences.getAppPreferences(EditProfileActivity.this).getStringValue(AppPreferences.USER_NAME));
                params.put("user_id", AppPreferences.getAppPreferences(EditProfileActivity.this).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        editProfile.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
        queue.add(editProfile);
    }


    public void getDropdownValues(final String id, final String type) {
        if (id != null && !id.isEmpty()) {
            //((BaseActivity) EditProfileActivity.this).showProgessDialog();
            StringRequest editProfile = new StringRequest(Request.Method.POST, Config.GET_DROPDOWN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //((BaseActivity) EditProfileActivity.this).dismissProgressDialog();
                            try {
                                final JSONObject jObject = new JSONObject(response);
                                if (jObject.getString("status_id").equals("1")) {
                                    Log.e("reponce...::>>", response);
                                    JSONArray arr = jObject.getJSONArray("result_data");
                                    int country_index = 0, state_index = 0, city_index = 0;
                                    for (int i = 0; i < arr.length(); i++) {
                                        CountryVO countryVO;
                                        switch (type) {
                                            case "COUNTRY":
                                                countryVO = new CountryVO(arr.getJSONObject(i).getString("countryName"));
                                                countryVO.setWebCode(arr.getJSONObject(i).getString("webCode"));
                                                countryVO.setCountryID(arr.getJSONObject(i).getString("countryID"));
                                                if (countryVO.getName().equalsIgnoreCase(country)) {
                                                    country_index = countrySpinnerList.size();
                                                }
                                                countrySpinnerList.add(countryVO);
                                                countrySpinnerAdapter.notifyDataSetChanged();
                                                break;
                                            case "STATE":
                                                countryVO = new CountryVO(arr.getJSONObject(i).getString("stateName"));
                                                countryVO.setStateID(arr.getJSONObject(i).getString("stateID"));
                                                countryVO.setCountryID(arr.getJSONObject(i).getString("countryID"));
                                                if (countryVO.getName().equalsIgnoreCase(state)) {
                                                    state_index = stateSpinnerList.size();
                                                }
                                                stateSpinnerList.add(countryVO);
                                                stateSpinnerAdapter.notifyDataSetChanged();
                                                break;
                                            case "CITY":
                                                countryVO = new CountryVO(arr.getJSONObject(i).getString("cityName"));
                                                if (countryVO.getName().equalsIgnoreCase(city)) {
                                                    city_index = citySpinnerList.size();
                                                }
                                                citySpinnerList.add(countryVO);
                                                citySpinnerAdapter.notifyDataSetChanged();
                                                break;
                                        }


                                    }
                                    switch (type) {
                                        case "COUNTRY":
                                            countrySpinner.setSelection(country_index);
                                            break;
                                        case "STATE":
                                            stateSpinner.setSelection(state_index);
                                            break;
                                        case "CITY":
                                            citySpinner.setSelection(city_index);
                                            break;
                                    }

                                } else {
                                    Toast.makeText(EditProfileActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            Toast.makeText(EditProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("error", volleyError.getMessage());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    switch (type) {
                        case "COUNTRY":
                            params.put("country_id", id);
                            break;
                        case "STATE":
                            params.put("state_country_id", id);
                            break;
                        case "CITY":
                            params.put("city_state_id", id);
                            break;
                    }


                    return params;
                }
            };

            editProfile.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
            queue.add(editProfile);
        }
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if (hasPermission(Config.MEDIA_PERMISSION)) {
                        Utils.launchCamera(EditProfileActivity.this);
                    } else {
                        setMediaPermissionListener(new Utils.MediaPermissionListener() {
                            @Override
                            public void onMediaPermissionStatus(boolean status) {
                                Utils.launchCamera(EditProfileActivity.this);
                            }
                        });
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    if (hasPermission(Config.MEDIA_PERMISSION)) {
                        Utils.openGallery(EditProfileActivity.this);
                    } else {
                        setMediaPermissionListener(new Utils.MediaPermissionListener() {
                            @Override
                            public void onMediaPermissionStatus(boolean status) {
                                Utils.openGallery(EditProfileActivity.this);
                            }
                        });
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == Config.CAMERAIMAGE) {
                    imagePath = Utils.getRealPathFromURI(EditProfileActivity.this, Config.CAMERAFILEURI);
                } else if (requestCode == Config.GALLERYIMAGE) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        imagePath = Utils.getRealPathFromURI(EditProfileActivity.this, selectedImageUri);
                    }
                }
                new ImageCompression(EditProfileActivity.this, new ImageCompression.ImageCompressListener() {
                    @Override
                    public void onImageCompressed(String destinationUrl) {
                        if (destinationUrl.isEmpty()) {
                            Toast.makeText(EditProfileActivity.this, "Selected media is invalid, please try again or use another image.", Toast.LENGTH_LONG).show();
                        }
                        bitmap = BitmapFactory.decodeFile(destinationUrl);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        ((ImageView) findViewById(R.id.img_profile)).setImageDrawable(drawable);
                        if (Utils.isNetworkConnected(EditProfileActivity.this, true)) {
                            updateProfilePic();
                        }
                    }
                }).execute(imagePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, "Something went wrong, please try again or use another image.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == 0)
                    Utils.launchCamera(EditProfileActivity.this);
                break;
            case 2:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Config.GALLERYIMAGE);
                break;
        }
    }


    public void updateProfilePic() {
        ((BaseActivity) EditProfileActivity.this).showProgessDialog("Please wait...");

        File filesDir = EditProfileActivity.this.getFilesDir();
        File imageFile = new File(filesDir, ((BaseActivity) EditProfileActivity.this).TAG + "_" + Math.random() + ".jpg");

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
        params.put("user_id", AppPreferences.getAppPreferences(EditProfileActivity.this).getStringValue(AppPreferences.USER_ID));

        CustomMultipartRequest requestPost = new CustomMultipartRequest(Config.UPDATE_PROFILE_PIC, params, imageFile, "file.jpg", "file",
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ((BaseActivity) EditProfileActivity.this).dismissProgressDialog();
                        Toast.makeText(EditProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) EditProfileActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Config.updateProfile = true;
                            }
                            Toast.makeText(EditProfileActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        requestPost.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue postqueue = Volley.newRequestQueue(EditProfileActivity.this);
        postqueue.add(requestPost);
    }

    public void updateProfile() {
        ((BaseActivity) EditProfileActivity.this).showProgessDialog();
        StringRequest editProfile = new StringRequest(Request.Method.POST, Config.UPDATE_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) EditProfileActivity.this).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Config.updateProfile = true;
                            }
                            Toast.makeText(EditProfileActivity.this, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(EditProfileActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //user_id,fullname,bio,password,profiletype,companyInfo,city,website,gender,country,state,username,email,privacy,zipcode
                params.put("user_id", AppPreferences.getAppPreferences(EditProfileActivity.this).getStringValue(AppPreferences.USER_ID));
                params.put("username", ((EditText) findViewById(R.id.txt_username)).getText().toString().trim());
                params.put("fullname", ((EditText) findViewById(R.id.txt_company_name)).getText().toString().trim());
                params.put("bio", ((EditText) findViewById(R.id.txt_bio)).getText().toString().trim());
                params.put("email", ((EditText) findViewById(R.id.txt_email)).getText().toString().trim());
                params.put("password", ((EditText) findViewById(R.id.txt_password)).getText().toString().trim());
                params.put("profiletype", profiletype);
                params.put("companyInfo", ((EditText) findViewById(R.id.txt_company)).getText().toString().trim());
                params.put("city", city);
                params.put("state", state);
                params.put("country", country);
                params.put("website", ((EditText) findViewById(R.id.txt_website)).getText().toString().trim());
                params.put("gender", gender);
                params.put("privacy", "");
                params.put("zipcode", ((EditText) findViewById(R.id.txt_zip)).getText().toString().trim());
                Log.v("kneedle", "params:" + params);

                return params;
            }
        };

        editProfile.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
        queue.add(editProfile);
    }
}
