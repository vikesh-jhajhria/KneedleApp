package com.kneedleapp;

import android.os.Bundle;
import android.widget.TextView;

import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

public class MailSentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sent);
        applyFonts();
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_email_sent), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(this, (TextView) findViewById(R.id.txt_send_again), Config.CENTURY_GOTHIC_BOLD);
    }
}
