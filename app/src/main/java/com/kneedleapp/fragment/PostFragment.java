package com.kneedleapp.fragment;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.ImageCompression;
import com.kneedleapp.utils.Utils;

import static android.app.Activity.RESULT_OK;
import static com.kneedleapp.utils.Config.fragmentManager;

@SuppressWarnings("ConstantConditions")
public class PostFragment extends BaseFragment {
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public ImageView mImgContent;
    private String userChoosenTask;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private boolean result;
    private View view_main;
    private Bitmap bitmap;
    String imagePath = "";

    public static PostFragment newInstance() {

        PostFragment fragment = new PostFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_main = inflater.inflate(R.layout.fragment_post, container, false);
        mImgContent = (ImageView) view_main.findViewById(R.id.img_content);
        Config.LAST_PAGE = "HOME";
        ((ImageView) view_main.findViewById(R.id.img_next)).setOnClickListener(this);
        ((TextView) view_main.findViewById(R.id.txt_library)).setOnClickListener(this);
        ((TextView) view_main.findViewById(R.id.txt_photo)).setOnClickListener(this);
        Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_new_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_REGULAR);
        ((MainActivity) getActivity()).hasPermission(Config.MEDIA_PERMISSION);
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
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_REGULAR);
                ((TextView) view_main.findViewById(R.id.txt_library)).setTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView) view_main.findViewById(R.id.txt_photo)).setTextColor(getResources().getColor(R.color.colourBlack));
                break;

            case R.id.txt_photo:
                userChoosenTask = "Take Photo";
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
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(getActivity(), (TextView) view_main.findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_BOLD);
                ((TextView) view_main.findViewById(R.id.txt_photo)).setTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView) view_main.findViewById(R.id.txt_library)).setTextColor(getResources().getColor(R.color.colourBlack));
                break;
        }
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
                        if(destinationUrl.isEmpty()){
                            Toast.makeText(getActivity(), "Selected media is invalid, please try again or use another image.",        Toast.LENGTH_LONG).show();
                        }
                        bitmap = BitmapFactory.decodeFile(destinationUrl);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        mImgContent.setImageDrawable(drawable);
                    }
                }).execute(imagePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong, please try again or use another image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Kneedle","Post");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(PostFragment.this);
            }
        },500);
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
}
