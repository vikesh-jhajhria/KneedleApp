package com.kneedleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.ImageCompression;
import com.kneedleapp.utils.Utils;

import static com.kneedleapp.utils.Config.postBitmap;

public class PostActivity extends BaseActivity {


    public ImageView mImgContent;
    private Bitmap bitmap;
    String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        findViewById(R.id.rl_post_selected).setVisibility(View.VISIBLE);
        postBitmap = null;
        mImgContent = (ImageView) findViewById(R.id.img_content);
        CURRENT_PAGE = "POST";
        findViewById(R.id.img_next).setOnClickListener(this);
        findViewById(R.id.txt_library).setOnClickListener(this);
        findViewById(R.id.txt_photo).setOnClickListener(this);
        findViewById(R.id.txt_info).setVisibility(View.VISIBLE);
        Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_new_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_info), Config.CENTURY_GOTHIC_REGULAR);
        hasPermission(Config.MEDIA_PERMISSION);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.img_next:

                if (bitmap != null) {
                    postBitmap = ((BitmapDrawable) mImgContent.getDrawable()).getBitmap();
                    startActivity(new Intent(getApplicationContext(), PostEditActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else {
                    Toast.makeText(PostActivity.this, "Please choose image", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txt_library:
                if (hasPermission(Config.MEDIA_PERMISSION)) {
                    Utils.openGallery(PostActivity.this);
                } else {
                    setMediaPermissionListener(new Utils.MediaPermissionListener() {
                        @Override
                        public void onMediaPermissionStatus(boolean status) {
                            Utils.openGallery(PostActivity.this);
                        }
                    });
                }
                Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_REGULAR);
                ((TextView) findViewById(R.id.txt_library)).setTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView) findViewById(R.id.txt_photo)).setTextColor(getResources().getColor(R.color.colourBlack));
                break;

            case R.id.txt_photo:
                if (hasPermission(Config.MEDIA_PERMISSION)) {
                    Utils.launchCamera(PostActivity.this);
                } else {
                    setMediaPermissionListener(new Utils.MediaPermissionListener() {
                        @Override
                        public void onMediaPermissionStatus(boolean status) {
                            Utils.launchCamera(PostActivity.this);
                        }
                    });
                }
                Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_library), Config.CENTURY_GOTHIC_REGULAR);
                Utils.setTypeface(PostActivity.this, (TextView) findViewById(R.id.txt_photo), Config.CENTURY_GOTHIC_BOLD);
                ((TextView) findViewById(R.id.txt_photo)).setTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView) findViewById(R.id.txt_library)).setTextColor(getResources().getColor(R.color.colourBlack));
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == Config.CAMERAIMAGE) {
                    imagePath = Utils.getRealPathFromURI(PostActivity.this, Config.CAMERAFILEURI);
                } else if (requestCode == Config.GALLERYIMAGE) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        imagePath = Utils.getRealPathFromURI(PostActivity.this, selectedImageUri);
                    }
                }
                new ImageCompression(PostActivity.this, new ImageCompression.ImageCompressListener() {
                    @Override
                    public void onImageCompressed(String destinationUrl) {
                        if (destinationUrl.isEmpty()) {
                            Toast.makeText(PostActivity.this, "Selected media is invalid, please try again or use another image.", Toast.LENGTH_LONG).show();
                        }
                        bitmap = BitmapFactory.decodeFile(destinationUrl);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        findViewById(R.id.txt_info).setVisibility(View.GONE);
                        mImgContent.setImageDrawable(drawable);

                        postBitmap = ((BitmapDrawable) mImgContent.getDrawable()).getBitmap();
                        startActivity(new Intent(getApplicationContext(), PostEditActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }).execute(imagePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(PostActivity.this, "Something went wrong, please try again or use another image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == 0)
                    Utils.launchCamera(PostActivity.this);
                break;
            case 2:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Config.GALLERYIMAGE);
                break;
        }
    }
}
