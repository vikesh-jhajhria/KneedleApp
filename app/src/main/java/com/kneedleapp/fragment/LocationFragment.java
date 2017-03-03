package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;


public class LocationFragment extends Fragment implements View.OnClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        applyFonts(view);
        view.findViewById(R.id.img_back).setOnClickListener(this);


        return view;
    }
    private void applyFonts(View view){
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_edit_profile), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_location), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_state), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_city), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_zip), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_info1), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_info2), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_search_profile), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_btn_save), Config.CENTURY_GOTHIC_REGULAR);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
        }
    }
}
