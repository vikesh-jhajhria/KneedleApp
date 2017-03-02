package com.kneedleapp.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;


public class PostEditFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_edit, container, false);

        Bitmap bitmap = getArguments().getParcelable("POSTIMAGE");
        if (bitmap != null) {
            ((ImageView) view.findViewById(R.id.img_post)).setImageBitmap(bitmap);
        }

        applyFonts(view);
        return view;
    }

    private void applyFonts(View view){
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_new_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_caption), Config.CENTURY_GOTHIC_REGULAR);
    }

}
