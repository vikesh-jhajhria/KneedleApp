package com.kneedleapp;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.CategoryVo;

import java.util.ArrayList;

import static com.kneedleapp.RegistrationActivity.mStoreList;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.rl_search_selected).setVisibility(View.VISIBLE);
        mStoreList.clear();
        CURRENT_PAGE = "SEARCH";
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
        Utils.setTypeface(SearchActivity.this, (TextView)findViewById(R.id.btn_search), Config.CENTURY_GOTHIC_REGULAR);
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
        findViewById(R.id.btn_search).setOnClickListener(this);

        ((EditText) findViewById(R.id.txt_search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH) {

                    if (((EditText) findViewById(R.id.txt_search)).getText().toString().trim().isEmpty()) {
                        ((EditText) findViewById(R.id.txt_search)).setError("Please enter key to search.");

                    }


                    Bundle bundle = new Bundle();
                    bundle.putString("SEARCHTEXT", ((EditText) findViewById(R.id.txt_search)).getText().toString().trim());
                    String profileType = ((TextView) findViewById(R.id.txt_category)).getText().toString().trim();
                    bundle.putString("CATEGORY", profileType.equalsIgnoreCase("Search By Category") ? "" : profileType);
                    if (((CheckBox) findViewById(R.id.check_near_me)).isChecked()) {
                        bundle.putString("ZIP", ((EditText) findViewById(R.id.txt_zip)).getText().toString().trim());
                        bundle.putString("RANGE", (String) ((Spinner) findViewById(R.id.spinner_within)).getSelectedItem());
                    }

                    startActivity(new Intent(getApplicationContext(), SearchResultActivity.class)
                            .putExtras(bundle)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    hideKeyboard();

                }
                ((TextView) findViewById(R.id.txt_category)).setText("Search By Category");

                return false;
            }
        });

        findViewById(R.id.rl_profile_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CategoriesActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

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
            View row = inflater.inflate(R.layout.layout_spinner_item_white, parent, false);
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
            case R.id.btn_search:
                searchText = ((EditText) findViewById(R.id.txt_search)).getText().toString().trim();
                if (searchText.isEmpty()) {
                    ((EditText) findViewById(R.id.txt_search)).setError("Please enter key to search.");
                    break;
                }
                startActivity(new Intent(getApplicationContext(), SearchResultActivity.class)
                        .putExtra("SEARCHTEXT",searchText)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
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


        if (mStoreList != null) {
            if(mStoreList.isEmpty()){
                ((TextView) findViewById(R.id.txt_category)).setText("Search By Category");
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mStoreList.size(); i++) {
                CategoryVo categoryVo = mStoreList.get(i);
                if (categoryVo.isChecked()) {
                    if (i < mStoreList.size() - 1) {
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
