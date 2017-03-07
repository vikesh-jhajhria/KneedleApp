package com.kneedleapp.vo;

import android.text.SpannableString;

/**
 * Created by aman.sharma on 2/22/2017.
 */

public class NotificationItemVo {

    public static final int DAY = 0;
    public static final int NOTIFICATION = 1;

    private String mTvHeader, mImgUser, mImgContent;
    private SpannableString mTvNotiText;
    private int mType;

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public NotificationItemVo(String mTvHeader, SpannableString mTvNotiText, int mType) {
        this.mTvHeader = mTvHeader;
        this.mTvNotiText = mTvNotiText;
        this.mType = mType;

    }

    public String getmTvHeader() {
        return mTvHeader;
    }

    public void setmTvHeader(String mTvHeader) {
        this.mTvHeader = mTvHeader;
    }

    public SpannableString getmTvNotiText() {
        return mTvNotiText;
    }

    public void setmTvNotiText(SpannableString mTvNotiText) {
        this.mTvNotiText = mTvNotiText;
    }

    public String getmImgUser() {
        return mImgUser;
    }

    public void setmImgUser(String mImgUser) {
        this.mImgUser = mImgUser;
    }

    public String getmImgContent() {
        return mImgContent;
    }

    public void setmImgContent(String mImgContent) {
        this.mImgContent = mImgContent;
    }
}