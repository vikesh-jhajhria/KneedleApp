package com.kneedleapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;


public class FullImageViewActivity extends BaseActivity {

    private String mUsername, mLikes;
    private String mImgBitmapContent, mImgBitmapUser;
    boolean transitionFlag;
    private boolean mImageLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);
CURRENT_PAGE = "FULL_IMAGE";
        Bundle extras = getIntent().getExtras();

        mUsername = extras.getString("USERNAME");
        mImgBitmapContent = extras.getString("IMAGE");
        mImgBitmapUser = extras.getString("USERIMAGE");
        mLikes = ""+extras.getInt("LIKES");
        mImageLike = extras.getBoolean("LIKEDORNOT");

        transitionFlag = getIntent().getBooleanExtra("transition", false);
        if (transitionFlag) {
            ((TextView) findViewById(R.id.txt_user_name)).setText(extras.getString("USERNAME"));
            ((TextView) findViewById(R.id.txt_likes)).setText(extras.getString("LIKES"));
            byte[] imgMain = extras.getByteArray("MAIN_IMAGE");
            Bitmap bmpMain = BitmapFactory.decodeByteArray(imgMain, 0, imgMain.length);
            ((ImageView) findViewById(R.id.img_full_image)).setImageBitmap(bmpMain);
        } else {
            ((TextView) findViewById(R.id.txt_user_name)).setText(mUsername);
            ((TextView) findViewById(R.id.txt_likes)).setText(mLikes);
            Utils.setTypeface(this, ((TextView) findViewById(R.id.txt_user_name)), Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(this, ((TextView) findViewById(R.id.txt_likes)), Config.CENTURY_GOTHIC_REGULAR);
            Glide.with(this).load(mImgBitmapContent).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into((ImageView) findViewById(R.id.img_full_image));
            Glide.with(this).load(mImgBitmapUser).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into((ImageView) findViewById(R.id.img_user));
            if (mImageLike) {
                ((ImageView) findViewById(R.id.img_heart)).setImageResource(R.drawable.heart);
            } else {
                ((ImageView) findViewById(R.id.img_heart)).setImageResource(R.drawable.heart_unselected);
            }
        }
    }
}