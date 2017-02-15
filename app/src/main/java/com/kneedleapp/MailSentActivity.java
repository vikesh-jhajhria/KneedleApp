package com.kneedleapp;

import android.os.Bundle;

public class MailSentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sent);

        findViewById(R.id.img_back).setOnClickListener(this);
    }
}
