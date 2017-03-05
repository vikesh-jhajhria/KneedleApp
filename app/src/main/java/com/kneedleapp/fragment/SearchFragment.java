package com.kneedleapp.fragment;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import java.util.ArrayList;

public class SearchFragment extends BaseFragment {

    private String searchText = "";

    ArrayList<String> spinnerDataList;
    ArrayList<String> withinList;

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        ((Spinner) view.findViewById(R.id.spinner_home)).getBackground().setColorFilter(getResources().getColor(R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);

        ((CheckBox)view.findViewById(R.id.check_near_me)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    view.findViewById(R.id.rl_zip).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.rl_zip).setVisibility(View.INVISIBLE);
                }
            }
        });
        spinnerDataList = new ArrayList<>();
        spinnerDataList.add("PROFILE TYPE");
        spinnerDataList.add("PROFILE 1");
        spinnerDataList.add("PROFILE 2");

        withinList = new ArrayList<>();
        withinList.add("10 KM");
        withinList.add("50 KM");
        withinList.add("100 KM");
        withinList.add("200 KM");
        WithinSpinnerAdapter withinAdapter = new WithinSpinnerAdapter(getActivity(), R.layout.layout_spinner_item, withinList);
        ((Spinner) view.findViewById(R.id.spinner_within)).setAdapter(withinAdapter);

        view.findViewById(R.id.img_search).setOnClickListener(this);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, spinnerDataList);
        ((Spinner) view.findViewById(R.id.spinner_home)).setAdapter(spinnerAdapter);

        applyFonts(view);
        return view;
    }



    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(getActivity(), label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(spinnerDataList.get(position));
            return row;
        }
    }


    public class WithinSpinnerAdapter extends ArrayAdapter<String> {

        public WithinSpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(getActivity(), label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(withinList.get(position));
            return row;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_search:
                searchText = ((EditText) getView().findViewById(R.id.txt_search)).getText().toString().trim();
                if (searchText.isEmpty()) {
                    ((EditText) getView().findViewById(R.id.txt_search)).setError("Please enter key to search.");
                    break;
                }
                SearchResultFragment fragment = SearchResultFragment.newInstance();
                Config.fragmentManager.beginTransaction()
                        .add(R.id.main_frame, fragment, "SEARCH_RESULT")
                        .disallowAddToBackStack()
                        .commit();
                break;
        }
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_search), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_zip), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.check_near_me), Config.CENTURY_GOTHIC_REGULAR);
    }

}
