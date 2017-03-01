package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kneedleapp.R;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_location).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.img_location:
                Fragment fragment = new LocationFragment();
                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();
                break;


        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().popBackStackImmediate();

                    return true;
                }
                return false;
            }
        });

    }
}
