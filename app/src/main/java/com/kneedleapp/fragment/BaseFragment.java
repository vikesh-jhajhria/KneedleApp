package com.kneedleapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.Config;

import static com.kneedleapp.BaseActivity.BottomBarTab.HOME;
import static com.kneedleapp.utils.Config.fragmentManager;


public class BaseFragment extends Fragment implements View.OnClickListener, View.OnKeyListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    public void hideKeyboard() {
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                goBack();
                break;
        }
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return false;
    }

    public void printLog(String message){
        Log.v("KNEEDLE",message);
    }
    private void goBack(){
        switch (Config.LAST_PAGE){
            case "HOME":
                ((MainActivity)getActivity()).selectTab(HOME);
                break;
            default:
                fragmentManager.popBackStack();
        }
    }
}
