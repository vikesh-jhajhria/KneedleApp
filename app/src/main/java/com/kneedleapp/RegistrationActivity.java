package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViewById(R.id.btn_let_me_in).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_let_me_in:
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }
}
