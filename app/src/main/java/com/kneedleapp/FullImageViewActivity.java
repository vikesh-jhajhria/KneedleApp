package com.kneedleapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.squareup.picasso.Picasso;


public class FullImageViewActivity extends BaseActivity {

    private String mUsername, mLikes;
    private String mImgBitmapContent, mImgBitmapUser;
    boolean transitionFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        Bundle extras = getIntent().getExtras();

        mUsername = extras.getString("USERNAME");
        mImgBitmapContent = extras.getString("IMAGE");
        mImgBitmapUser = extras.getString("USERIMAGE");
        mLikes = extras.getString("LIKES");

        transitionFlag = getIntent().getBooleanExtra("transition", false);
        if (transitionFlag) {
            ((TextView) findViewById(R.id.txt_user_name)).setText(extras.getString("USERNAME"));
            ((TextView) findViewById(R.id.txt_likes)).setText(extras.getString("LIKES"));

           /* byte[] imgHeader = extras.getByteArray("HEADER_IMAGE");
            Bitmap bmpHeader = BitmapFactory.decodeByteArray(imgHeader, 0, imgHeader.length);
            ((ImageView) findViewById(R.id.img_kneedle)).setImageBitmap(bmpHeader);
*/
            byte[] imgMain = extras.getByteArray("MAIN_IMAGE");
            Bitmap bmpMain = BitmapFactory.decodeByteArray(imgMain, 0, imgMain.length);
            ((ImageView) findViewById(R.id.img_full_image)).setImageBitmap(bmpMain);

            /*byte[] imgUser = extras.getByteArray("USER_IMAGE");
            Bitmap bmpUser = BitmapFactory.decodeByteArray(imgUser, 0, imgUser.length);
            ((ImageView) findViewById(R.id.img_user)).setImageBitmap(bmpUser);


            byte[] imgLike = extras.getByteArray("HEART_IMAGE");
            Bitmap bmpLike = BitmapFactory.decodeByteArray(imgLike, 0, imgLike.length);
            ((ImageView) findViewById(R.id.img_heart)).setImageBitmap(bmpLike);*/


        } else {


            ((TextView) findViewById(R.id.txt_user_name)).setText(mUsername);
            ((TextView) findViewById(R.id.txt_likes)).setText(mLikes);
            Utils.setTypeface(this, ((TextView) findViewById(R.id.txt_user_name)), Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(this, ((TextView) findViewById(R.id.txt_likes)), Config.CENTURY_GOTHIC_REGULAR);
            Picasso.with(this).load(mImgBitmapContent).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into((ImageView) findViewById(R.id.img_full_image));
            Picasso.with(this).load(mImgBitmapUser).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into((ImageView) findViewById(R.id.img_user));
        }
    }


}