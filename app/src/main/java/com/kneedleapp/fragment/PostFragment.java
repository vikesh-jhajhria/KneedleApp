package com.kneedleapp.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.kneedleapp.BaseActivity;
import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.kneedleapp.utils.Config.fragmentManager;


public class PostFragment extends BaseFragment {
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public ImageView mImgContent;
    private String userChoosenTask;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private boolean result;
    private View view_main;
    private Bitmap bitmap;

    public static PostFragment newInstance() {

        PostFragment fragment = new PostFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_main = inflater.inflate(R.layout.fragment_post, container, false);
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

                if (bitmap != null) {
                    Fragment fragment = new PostEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("POSTIMAGE", ((BitmapDrawable) mImgContent.getDrawable()).getBitmap());
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();
                } else {
                    Toast.makeText(getContext(), "Please choose image", Toast.LENGTH_SHORT).show();
                }


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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (data != null)
                onCaptureImageResult(data);
        } else if (requestCode == SELECT_FILE) {
            if (data != null)
                onSelectFromGalleryResult(data);
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
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        ((MainActivity) getActivity()).startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void gallaryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((MainActivity) getActivity()).startActivityForResult(galleryIntent, SELECT_FILE);
    }


    public void onCaptureImageResult(Intent data) {
        File f = new File(Environment.getExternalStorageDirectory().toString());
        for (File temp : f.listFiles()) {
            if (temp.getName().equals("temp.jpg")) {
                f = temp;
                break;
            }
        }
        try {

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                    bitmapOptions);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            mImgContent.setImageDrawable(d);

            String path = android.os.Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + "Phoenix" + File.separator + "default";
            f.delete();
            OutputStream outFile = null;
            File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
            try {
                outFile = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                outFile.flush();
                outFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSelectFromGalleryResult(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = ((BaseActivity) getContext()).getContentResolver().query(selectedImage, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        bitmap = (BitmapFactory.decodeFile(picturePath));
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        mImgContent.setImageDrawable(drawable);
    }

}
