package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

public class LandingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        applyFonts();
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;
        }
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_login), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_register), Config.CENTURY_GOTHIC_REGULAR);

    }


}