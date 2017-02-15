package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.txt_forgot_password:
                startActivity(new Intent(this,ForgotPasswordActivity.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(this,RegistrationActivity.class));
                break;
        }
    }
}

