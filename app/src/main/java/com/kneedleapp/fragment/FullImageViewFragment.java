package com.kneedleapp.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.kneedleapp.utils.Config.fragmentManager;


public class FullImageViewFragment extends Fragment {

    private String mUsername, mLikes;
    private String mImgBitmapContent, mImgBitmapUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_image_view, container, false);

        Bundle bundle = getArguments();

        if (bundle != null) {
            mUsername = bundle.getString("USERNAME");
            mImgBitmapContent = bundle.getString("IMAGE");
            mImgBitmapUser = bundle.getString("USERIMAGE");
            mLikes = bundle.getString("LIKES");
        }


        ((TextView) view.findViewById(R.id.txt_user_name_pop)).setText(mUsername);
        ((TextView) view.findViewById(R.id.txt_likes_pop)).setText(mLikes);
        Utils.setTypeface(getActivity(), ((TextView) view.findViewById(R.id.txt_user_name_pop)), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), ((TextView) view.findViewById(R.id.txt_likes_pop)), Config.CENTURY_GOTHIC_REGULAR);
        Picasso.with(getContext()).load(mImgBitmapContent).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into((ImageView) view.findViewById(R.id.img_full_image));
        Picasso.with(getContext()).load(mImgBitmapUser).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into((ImageView) view.findViewById(R.id.img_popup));

        return view;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("TAG", "on back pressed");
                    fragmentManager.popBackStack();
                    return true;
                }
                return false;
            }
        });
    }
}