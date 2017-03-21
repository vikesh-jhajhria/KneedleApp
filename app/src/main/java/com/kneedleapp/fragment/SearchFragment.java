package com.kneedleapp.fragment;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.adapter.CategoryAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

import java.util.ArrayList;

public class SearchFragment extends BaseFragment {

    private String searchText = "";

    ArrayList<String> spinnerDataList;
    ArrayList<String> withinList;
    private View mView;
    private ArrayList<String> mListSearchData;


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mListSearchData = getArguments().getStringArrayList("ARRAY");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_search, container, false);

        ((CheckBox) mView.findViewById(R.id.check_near_me)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mView.findViewById(R.id.rl_zip).setVisibility(View.VISIBLE);
                } else {
                    mView.findViewById(R.id.rl_zip).setVisibility(View.INVISIBLE);
                }
            }
        });

        withinList = new ArrayList<>();
        withinList.add("10 KM");
        withinList.add("50 KM");
        withinList.add("100 KM");
        withinList.add("200 KM");
        WithinSpinnerAdapter withinAdapter = new WithinSpinnerAdapter(getActivity(), R.layout.layout_spinner_item, withinList);
        ((Spinner) mView.findViewById(R.id.spinner_within)).setAdapter(withinAdapter);

        mView.findViewById(R.id.img_search).setOnClickListener(this);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.layout_spinner_item, spinnerDataList);


        applyFonts(mView);

        ((EditText) mView.findViewById(R.id.txt_search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH) {

                    if (((EditText) mView.findViewById(R.id.txt_search)).getText().toString().trim().isEmpty()) {
                        ((EditText) mView.findViewById(R.id.txt_search)).setError("Please enter key to search.");

                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("SEARCHTEXT", ((EditText) mView.findViewById(R.id.txt_search)).getText().toString().trim());

                    SearchResultFragment fragment = SearchResultFragment.newInstance();
                    fragment.setArguments(bundle);
                    Config.fragmentManager.beginTransaction()
                            .add(R.id.main_frame, fragment, "SEARCH_RESULT")
                            .addToBackStack(null)
                            .commit();
                    hideKeyboard();

                }


                return false;
            }
        });

        ((RelativeLayout) mView.findViewById(R.id.rl_profile_type)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new CategoriesFragment();
                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();
            }
        });


        return mView;
    }


    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textmViewResourceId, ArrayList<String> objects) {
            super(context, textmViewResourceId, objects);
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
                searchText = ((EditText) mView.findViewById(R.id.txt_search)).getText().toString().trim();
                if (searchText.isEmpty()) {
                    ((EditText) mView.findViewById(R.id.txt_search)).setError("Please enter key to search.");
                    break;
                }

                Bundle bundle = new Bundle();
                bundle.putString("SEARCHTEXT", searchText);

                SearchResultFragment fragment = SearchResultFragment.newInstance();
                fragment.setArguments(bundle);
                Config.fragmentManager.beginTransaction()
                        .add(R.id.main_frame, fragment, "SEARCH_RESULT")
                        .addToBackStack(null)
                        .commit();
                hideKeyboard();
                break;
        }
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_search), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_zip), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.check_near_me), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_category), Config.CENTURY_GOTHIC_REGULAR);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListSearchData != null) {
            StringBuilder builder = new StringBuilder();
            for (String details : mListSearchData) {
                builder.append(details + ", ");
            }
            builder.setLength(builder.length() - 2);
            ((TextView) mView.findViewById(R.id.txt_category)).setText(builder.toString());
        }
    }
}
