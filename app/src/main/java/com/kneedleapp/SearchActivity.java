package com.kneedleapp;

import android.content.Context;
import android.os.Bundle;
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

import com.kneedleapp.fragment.SearchResultFragment;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CategoryVo;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Config.LAST_PAGE = "HOME";
        ((CheckBox) findViewById(R.id.check_near_me)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.rl_zip).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rl_zip).setVisibility(View.INVISIBLE);
                }
            }
        });

        withinList = new ArrayList<>();
        withinList.add("10 KM");
        withinList.add("50 KM");
        withinList.add("100 KM");
        withinList.add("200 KM");
        WithinSpinnerAdapter withinAdapter = new WithinSpinnerAdapter(SearchActivity.this, R.layout.layout_spinner_item, withinList);
        ((Spinner) findViewById(R.id.spinner_within)).setAdapter(withinAdapter);

        findViewById(R.id.img_search).setOnClickListener(this);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(SearchActivity.this, R.layout.layout_spinner_item, spinnerDataList);


        applyFonts();

        ((EditText) findViewById(R.id.txt_search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH) {

                    if (((EditText) findViewById(R.id.txt_search)).getText().toString().trim().isEmpty()) {
                        ((EditText) findViewById(R.id.txt_search)).setError("Please enter key to search.");

                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("SEARCHTEXT", ((EditText) findViewById(R.id.txt_search)).getText().toString().trim());
                    bundle.putString("CATEGORY", ((TextView) findViewById(R.id.txt_category)).getText().toString().trim());
                    if (((CheckBox) findViewById(R.id.check_near_me)).isChecked()) {
                        bundle.putString("ZIP", ((EditText) findViewById(R.id.txt_zip)).getText().toString().trim());
                        bundle.putString("RANGE", (String) ((Spinner) findViewById(R.id.spinner_within)).getSelectedItem());
                    }
                    SearchResultFragment fragment = SearchResultFragment.newInstance();
                    fragment.setArguments(bundle);
                    Config.fragmentManager.beginTransaction()
                            .add(R.id.main_frame, fragment, "SEARCH_RESULT")
                            .addToBackStack(null)
                            .commit();
                    hideKeyboard();

                }
                ((TextView) findViewById(R.id.txt_category)).setText("Profile Type");

                return false;
            }
        });

        ((RelativeLayout) findViewById(R.id.rl_profile_type)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Bundle bundle = new Bundle();
                Fragment fragment = new CategoriesFragment();
                bundle.putString("KEY", "2");
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();*/
            }
        });

    }

    private String searchText = "";

    ArrayList<String> spinnerDataList;
    ArrayList<String> withinList;


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
            LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(SearchActivity.this, label, Config.CENTURY_GOTHIC_REGULAR);
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
            LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.txt_item);
            Utils.setTypeface(SearchActivity.this, label, Config.CENTURY_GOTHIC_REGULAR);
            label.setText(withinList.get(position));
            return row;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.img_search:
                searchText = ((EditText) findViewById(R.id.txt_search)).getText().toString().trim();
                if (searchText.isEmpty()) {
                    ((EditText) findViewById(R.id.txt_search)).setError("Please enter key to search.");
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

    private void applyFonts() {
        Utils.setTypeface(SearchActivity.this, (TextView) findViewById(R.id.txt_search), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SearchActivity.this, (TextView) findViewById(R.id.txt_zip), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SearchActivity.this, (TextView) findViewById(R.id.check_near_me), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(SearchActivity.this, (TextView) findViewById(R.id.txt_category), Config.CENTURY_GOTHIC_REGULAR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Kneedle", "Search");


        if (RegistrationActivity.mStoreList != null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < RegistrationActivity.mStoreList.size(); i++) {
                CategoryVo categoryVo = RegistrationActivity.mStoreList.get(i);
                if (categoryVo.isChecked()) {
                    if (i < RegistrationActivity.mStoreList.size() - 1) {
                        builder.append(categoryVo.getmCategoryName() + ", ");
                    } else {
                        builder.append(categoryVo.getmCategoryName());
                    }
                    ((TextView) findViewById(R.id.txt_category)).setText(builder.toString());
                }

            }

        }

    }
}
