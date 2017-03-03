package com.kneedleapp.fragment;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import java.util.ArrayList;


public class EditProfileFragment extends Fragment implements View.OnClickListener {
    ArrayList<String> spinnerDataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        applyFonts(view);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_location).setOnClickListener(this);
        ((Spinner) view.findViewById(R.id.spinner_profile_type)).getBackground().setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);

        spinnerDataList = new ArrayList<>();
        spinnerDataList.add("PROFILE TYPE");
        spinnerDataList.add("PROFILE 1");
        spinnerDataList.add("PROFILE 2");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, spinnerDataList);
        ((Spinner) view.findViewById(R.id.spinner_profile_type)).setAdapter(spinnerAdapter);
        return view;
    }
    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.txt_item);
            Utils.setTypeface(getActivity(), label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(spinnerDataList.get(position));
            return row;
        }
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

    private void applyFonts(View view){
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_edit_profile), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_name), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_password), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_edit), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_bio_title), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_bio), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_email), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_gender), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_homme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.txt_femme), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getContext(),(TextView) view.findViewById(R.id.btn_save_changes), Config.CENTURY_GOTHIC_REGULAR);
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
