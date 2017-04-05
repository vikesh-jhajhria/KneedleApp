package com.kneedleapp.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import com.kneedleapp.BaseActivity;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.CustomMultipartRequest;
import com.kneedleapp.utils.ImageCompression;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CountryVO;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.kneedleapp.utils.Config.fragmentManager;


public class EditProfileFragment extends BaseFragment {
    ArrayList<CountryVO> countrySpinnerList;
    ArrayList<CountryVO> stateSpinnerList;
    ArrayList<CountryVO> citySpinnerList;

    SpinnerAdapter countrySpinnerAdapter;
    SpinnerAdapter stateSpinnerAdapter;
    SpinnerAdapter citySpinnerAdapter;

    Spinner citySpinner, stateSpinner, countrySpinner, profileSpinner;

    private View view;
    private Bitmap bitmap;
    String imagePath = "";
    String gender = "male";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Config.LAST_PAGE = "";
        applyFonts(view);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_location).setOnClickListener(this);
        view.findViewById(R.id.img_homme).setOnClickListener(this);
        view.findViewById(R.id.img_femme).setOnClickListener(this);
        view.findViewById(R.id.btn_save_changes).setOnClickListener(this);
        view.findViewById(R.id.img_profile).setOnClickListener(this);

        citySpinner = ((Spinner) view.findViewById(R.id.spinner_city));
        stateSpinner = ((Spinner) view.findViewById(R.id.spinner_state));
        countrySpinner = ((Spinner) view.findViewById(R.id.spinner_country));
        profileSpinner = ((Spinner) view.findViewById(R.id.spinner_profile_type));

        countrySpinnerList = new ArrayList<>();
        stateSpinnerList = new ArrayList<>();
        citySpinnerList = new ArrayList<>();
        countrySpinnerList.add(new CountryVO("COUNTRY"));
        stateSpinnerList.add(new CountryVO("STATE"));
        citySpinnerList.add(new CountryVO("CITY"));

        Utils.SpinnerAdapter profileSpinnerAdapter = new Utils.SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, Config.PROFILE_TYPE);
        profileSpinner.setAdapter(profileSpinnerAdapter);

        countrySpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, countrySpinnerList);
        ((Spinner) view.findViewById(R.id.spinner_country)).setAdapter(countrySpinnerAdapter);
        ((Spinner) view.findViewById(R.id.spinner_country)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


                getDropdownValues(countrySpinnerList.get(position).getCountryID(), "STATE");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateSpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, stateSpinnerList);
        ((Spinner) view.findViewById(R.id.spinner_state)).setAdapter(stateSpinnerAdapter);
        ((Spinner) view.findViewById(R.id.spinner_state)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySpinnerList.clear();
                CountryVO city = new CountryVO("CITY");
                citySpinnerList.add(city);
                if (citySpinner != null)
                    citySpinner.setSelection(0);

                getDropdownValues(stateSpinnerList.get(position).getStateID(), "CITY");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, citySpinnerList);
        ((Spinner) view.findViewById(R.id.spinner_city)).setAdapter(citySpinnerAdapter);


        ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female);
        ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male);
        editProfile();
        getDropdownValues("1", "COUNTRY");

        return view;
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
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(getActivity(), label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(list.get(position).getName());
            return row;
        }
    }

    @Override
    public void onClick(View mView) {
        super.onClick(mView);
        switch (mView.getId()) {
            case R.id.img_location:
                Fragment fragment = new LocationFragment();
                fragmentManager.beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();
                break;
            case R.id.img_femme:
                gender = "female";
                ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female_red);
                ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male);
                break;
            case R.id.img_homme:
                gender = "male";
                ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female);
                ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male_red);
                break;
            case R.id.img_profile:
                selectImage();
                break;
            case R.id.btn_save_changes:
                if (Utils.isNetworkConnected(getActivity(), true)) {
                    updateProfile();
                }
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
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_zip), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(), (TextView) view.findViewById(R.id.txt_website), Config.CENTURY_GOTHIC_REGULAR);
    }


    @Override
    public void onResume() {
        super.onResume();

        MainActivity.isPost = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(EditProfileFragment.this);
            }
        }, 500);

    }


    public void editProfile() {
        ((BaseActivity) getActivity()).showProgessDialog();
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
                                ((TextView) view.findViewById(R.id.txt_website)).setText(jsonObject.getString("website"));
                                ((TextView) view.findViewById(R.id.txt_zip)).setText(jsonObject.getString("zipcode"));
                                if (jsonObject.getString("gender").equalsIgnoreCase("male")) {
                                    gender = "male";
                                    ((ImageView) view.findViewById(R.id.img_homme)).setImageResource(R.drawable.male_red);
                                } else {
                                    gender = "female";
                                    ((ImageView) view.findViewById(R.id.img_femme)).setImageResource(R.drawable.female_red);
                                }

                                Picasso.with(getContext()).load(Config.USER_IMAGE_URL + jsonObject.getString("image")).placeholder(R.drawable.profile_img).error(R.drawable.profile_img).into(((ImageView) view.findViewById(R.id.img_profile)));


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


    public void getDropdownValues(final String id, final String type) {
        if (id != null && !id.isEmpty()) {
            ((BaseActivity) getActivity()).showProgessDialog();
            StringRequest editProfile = new StringRequest(Request.Method.POST, Config.GET_DROPDOWN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ((BaseActivity) getActivity()).dismissProgressDialog();
                            try {
                                final JSONObject jObject = new JSONObject(response);
                                if (jObject.getString("status_id").equals("1")) {
                                    Log.e("reponce...::>>", response);
                                    JSONArray arr = jObject.getJSONArray("result_data");
                                    for (int i = 0; i < arr.length(); i++) {
                                        CountryVO countryVO;
                                        switch (type) {
                                            case "COUNTRY":
                                                countryVO = new CountryVO(arr.getJSONObject(i).getString("countryName"));
                                                countryVO.setWebCode(arr.getJSONObject(i).getString("webCode"));
                                                countryVO.setCountryID(arr.getJSONObject(i).getString("countryID"));
                                                countrySpinnerList.add(countryVO);
                                                countrySpinnerAdapter.notifyDataSetChanged();
                                                break;
                                            case "STATE":
                                                countryVO = new CountryVO(arr.getJSONObject(i).getString("stateName"));
                                                countryVO.setStateID(arr.getJSONObject(i).getString("stateID"));
                                                countryVO.setCountryID(arr.getJSONObject(i).getString("countryID"));
                                                stateSpinnerList.add(countryVO);
                                                stateSpinnerAdapter.notifyDataSetChanged();
                                                break;
                                            case "CITY":
                                                countryVO = new CountryVO(arr.getJSONObject(i).getString("cityName"));
                                                citySpinnerList.add(countryVO);
                                                citySpinnerAdapter.notifyDataSetChanged();
                                                break;
                                        }


                                    }


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

                            Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
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

            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(editProfile);
        }
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if (((MainActivity) getActivity()).hasPermission(Config.MEDIA_PERMISSION)) {
                        Utils.launchCamera(getActivity());
                    } else {
                        ((MainActivity) getActivity()).setMediaPermissionListener(new Utils.MediaPermissionListener() {
                            @Override
                            public void onMediaPermissionStatus(boolean status) {
                                Utils.launchCamera(getActivity());
                            }
                        });
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    if (((MainActivity) getActivity()).hasPermission(Config.MEDIA_PERMISSION)) {
                        Utils.openGallery(getActivity());
                    } else {
                        ((MainActivity) getActivity()).setMediaPermissionListener(new Utils.MediaPermissionListener() {
                            @Override
                            public void onMediaPermissionStatus(boolean status) {
                                Utils.openGallery(getActivity());
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
                    imagePath = Utils.getRealPathFromURI(getActivity(), Config.CAMERAFILEURI);
                } else if (requestCode == Config.GALLERYIMAGE) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        imagePath = Utils.getRealPathFromURI(getActivity(), selectedImageUri);
                    }
                }
                new ImageCompression(getActivity(), new ImageCompression.ImageCompressListener() {
                    @Override
                    public void onImageCompressed(String destinationUrl) {
                        if (destinationUrl.isEmpty()) {
                            Toast.makeText(getActivity(), "Selected media is invalid, please try again or use another image.", Toast.LENGTH_LONG).show();
                        }
                        bitmap = BitmapFactory.decodeFile(destinationUrl);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        ((ImageView) view.findViewById(R.id.img_profile)).setImageDrawable(drawable);
                        updateProfilePic();
                    }
                }).execute(imagePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong, please try again or use another image.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == 0)
                    Utils.launchCamera(getActivity());
                break;
            case 2:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Config.GALLERYIMAGE);
                break;
        }
    }


    public void updateProfilePic() {
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

        CustomMultipartRequest requestPost = new CustomMultipartRequest(Config.UPDATE_PROFILE_PIC, params, imageFile, "file.jpg", "file",
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

                            }
                            Toast.makeText(getContext(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
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

    public void updateProfile() {
        ((BaseActivity) getActivity()).showProgessDialog();
        StringRequest editProfile = new StringRequest(Request.Method.POST, Config.UPDATE_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {

                            }
                            Toast.makeText(getContext(), jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
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
                //user_id,fullname,bio,password,profiletype,companyInfo,city,website,gender,country,state,username,email,privacy,zipcode
                params.put("user_id", AppPreferences.getAppPreferences(getContext()).getStringValue(AppPreferences.USER_ID));
                params.put("username", ((EditText) view.findViewById(R.id.txt_username)).getText().toString().trim());
                params.put("fullname", ((EditText) view.findViewById(R.id.txt_name)).getText().toString().trim());
                params.put("bio", ((EditText) view.findViewById(R.id.txt_bio)).getText().toString().trim());
                params.put("email", ((EditText) view.findViewById(R.id.txt_email)).getText().toString().trim());
                params.put("password", ((EditText) view.findViewById(R.id.txt_password)).getText().toString().trim());
                params.put("profiletype",((CountryVO)profileSpinner.getSelectedItem()).getName());
                params.put("companyInfo","");
                params.put("city",((CountryVO)citySpinner.getSelectedItem()).getName());
                params.put("state",((CountryVO)stateSpinner.getSelectedItem()).getName());
                params.put("country",((CountryVO)countrySpinner.getSelectedItem()).getName());
                params.put("website",((EditText) view.findViewById(R.id.txt_website)).getText().toString().trim());
                params.put("gender",gender);
                params.put("privacy","");
                params.put("zipcode",((EditText) view.findViewById(R.id.txt_zip)).getText().toString().trim());
                Log.v("kneedle", "params:" + params);

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

