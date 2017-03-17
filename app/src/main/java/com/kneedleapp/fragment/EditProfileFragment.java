package com.kneedleapp.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;


public class EditProfileFragment extends BaseFragment implements View.OnClickListener {
    ArrayList<String> spinnerDataList;
    private View view;
    private String mUserName;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String userChoosenTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        checkPermission(getContext());
        applyFonts(view);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_location).setOnClickListener(this);
        view.findViewById(R.id.img_homme).setOnClickListener(this);
        view.findViewById(R.id.img_femme).setOnClickListener(this);
        view.findViewById(R.id.img_profile).setOnClickListener(this);
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
            case R.id.img_profile:
                selectImage();
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

                                Picasso.with(getContext()).load(Config.USER_IMAGE_URL + jsonObject.getString("image")).placeholder(R.drawable.profile_img).error(R.drawable.profile_img).into(((ImageView) view.findViewById(R.id.img_profile)));


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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null)
                onCaptureImageResult(data);
        } else if (requestCode == 2) {
            if (data != null)
                onSelectFromGalleryResult(data);
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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                    userChoosenTask = "Take Photo";
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                    userChoosenTask = "Choose from Gallery";
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ((ImageView) view.findViewById(R.id.img_profile)).setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((ImageView) view.findViewById(R.id.img_profile)).setImageBitmap(thumbnail);
    }

    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (userChoosenTask.equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, 1);

                    } else if (userChoosenTask.equals("Choose From Library")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                }
        }
    }

}
