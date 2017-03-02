package com.kneedleapp.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import java.io.IOException;


public class NewPostFragment extends Fragment implements View.OnClickListener {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView mImgContent;
    private String userChoosenTask;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean result;
    private View view_main;

    public static NewPostFragment newInstance(String param1, String param2) {

        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_main = inflater.inflate(R.layout.fragment_new_post, container, false);
        mImgContent = (ImageView) view_main.findViewById(R.id.img_content);
        result = checkPermission(getContext());

        ((ImageView) view_main.findViewById(R.id.img_next)).setOnClickListener(this);
        ((TextView) view_main.findViewById(R.id.txt_library)).setOnClickListener(this);
        ((TextView) view_main.findViewById(R.id.txt_photo)).setOnClickListener(this);
        Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_new_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_REGULAR);
        checkPermission(getContext());
        return view_main;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_next:

                Fragment fragment = new PostEditFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("POSTIMAGE", ((BitmapDrawable) mImgContent.getDrawable()).getBitmap());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();

                break;

            case R.id.txt_library:
                userChoosenTask = "Choose From Library";
                if (result)
                    gallaryIntent();
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_REGULAR);
                ((TextView) view_main.findViewById(R.id.txt_library)).setTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView) view_main.findViewById(R.id.txt_photo)).setTextColor(getResources().getColor(R.color.colourBlack));
                break;

            case R.id.txt_photo:
                userChoosenTask = "Take Photo";
                if (result)
                    cameraIntent();
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_BOLD);
                ((TextView) view_main.findViewById(R.id.txt_photo)).setTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView) view_main.findViewById(R.id.txt_library)).setTextColor(getResources().getColor(R.color.colourBlack));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            } else if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (userChoosenTask.equals("Take Photo")) {
                        Log.e("TAG", "aman");
                        cameraIntent();
                    } else if (userChoosenTask.equals("Choose From Library")) {
                        gallaryIntent();
                    }
                }
        }
    }


    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
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

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void gallaryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select file"), SELECT_FILE);


    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        mImgContent.setImageBitmap(bitmap);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Log.e("TAG", "aman");
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mImgContent.setImageBitmap(bm);
    }


}
