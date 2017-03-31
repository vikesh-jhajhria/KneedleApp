package com.kneedleapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kneedleapp.utils.Config.CENTURY_GOTHIC_REGULAR;


public class Utils {

    public static HashMap getDeviceSize(Activity activity) {
        HashMap hashMap = new HashMap();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        hashMap.put("Height", displaymetrics.heightPixels);
        hashMap.put("Width", displaymetrics.widthPixels);
        return hashMap;
    }

    public static void setTypeface(Context context, TextView textview, String fontName) {
        if (fontName.equalsIgnoreCase(CENTURY_GOTHIC_REGULAR)) {
            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/GOTHICR.TTF");
            textview.setTypeface(face);
        }
        if (fontName.equalsIgnoreCase(Config.CENTURY_GOTHIC_BOLD)) {
            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/GOTHICB.TTF");
            textview.setTypeface(face);
        }


    }

    public static void openGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, Config.GALLERYIMAGE);

    }

    public static void launchCamera(Activity activity) {
        Config.CAMERAFILEURI = null;
        Config.CAMERAFILEURI = Utils.getOutputMediaFileUri();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Config.CAMERAFILEURI != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Config.CAMERAFILEURI);
            activity.startActivityForResult(intent, Config.CAMERAIMAGE);
        }

    }


    public static Uri getOutputMediaFileUri() {
        File file = getOutputMediaFile();
        Uri uri = null;
        if (file != null) {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Config.IMAGE_DIRECTORY_NAME, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static boolean isNetworkConnected(Context context, boolean showToast) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.isConnected();
        if (!isConnected && showToast)
            Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public static void showAlertDialog(Context context, String title, String message, boolean isCancelable) {

        try {
            Builder alertDialogBuilder = new Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(isCancelable);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {

                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDecisionDialog(Context context, String title, String message, final AlertCallback callbackListener) {

        try {
            Builder alertDialogBuilder = new Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    callbackListener.callback();
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {

                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSnakeBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        Utils.setTypeface(view.getContext(), textView, CENTURY_GOTHIC_REGULAR);
        textView.setTextColor(Color.WHITE);
        snackbar.show();

    }

    public static void setBounceEffect(final View view) {
        PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, .5f);
        PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, .5f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scalex, scaley);
        anim.setRepeatCount(1);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f);
                PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f);
                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scalex, scaley);
                anim.setRepeatCount(1);
                anim.setRepeatMode(ValueAnimator.REVERSE);
                anim.setDuration(100);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.start();
            }
        });
    }

    public static String getCurrentDate()  {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

        return simpleDateFormat.format(date);
    }




    public static class SpinnerAdapter extends ArrayAdapter<String> {

        private ArrayList<String> list;
        private Activity context;

        public SpinnerAdapter(Activity context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            list = objects;
            this.context = context;
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
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(context, label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(list.get(position));
            return row;
        }
    }


    public static void getCategories(final Context context) {
        StringRequest getCategory = new StringRequest(Request.Method.POST, Config.GET_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Config.PROFILE_TYPE.clear();
                                Config.PROFILE_TYPE.add("Profile Type");
                                JSONArray jsonArray = jObject.getJSONArray("category_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Config.PROFILE_TYPE.add(jsonObject.getString("category_name"));
                                }
                            } else {
                                Toast.makeText(context, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }
        };

        getCategory.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getCategory);
    }
    public interface AlertCallback {
        void callback();
    }

    public interface GPSSettingListener {
        void onLocationSettingStatus(boolean status);
    }

    public interface MediaPermissionListener {
        void onMediaPermissionStatus(boolean status);
    }

    public interface LocationFoundListener {
        void onLocationFoundStatus(double latitude, double longitude, String message);
    }

}
