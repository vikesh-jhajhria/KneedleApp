package com.kneedleapp.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kneedleapp.R;


public class PostEditFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_post_edit, container, false);

        Bitmap bitmap = getArguments().getParcelable("POSTIMAGE");
        if (bitmap != null) {
            ((ImageView) view.findViewById(R.id.img_post)).setImageBitmap(bitmap);
        }


        return view;


    }

}
