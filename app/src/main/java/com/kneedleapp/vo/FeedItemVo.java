package com.kneedleapp.vo;

/**
 * Created by aman.sharma on 2/21/2017.
 */

public class FeedItemVo {

    private String mUserImage, mUserTitle, mUserSubTitle, mContentImage, mDesciption, mLikes;
    private boolean mLiked;

    public String getmUserImage() {
        return mUserImage;
    }

    public void setmUserImage(String mUserImage) {
        this.mUserImage = mUserImage;
    }

    public String getmUserTitle() {
        return mUserTitle;
    }

    public void setmUserTitle(String mUserTitle) {
        this.mUserTitle = mUserTitle;
    }

    public String getmUserSubTitle() {
        return mUserSubTitle;
    }

    public void setmUserSubTitle(String mUserSubTitle) {
        this.mUserSubTitle = mUserSubTitle;
    }

    public String getmContentImage() {
        return mContentImage;
    }

    public void setmContentImage(String mContentImage) {
        this.mContentImage = mContentImage;
    }

    public String getmDesciption() {
        return mDesciption;
    }

    public void setmDesciption(String mDesciption) {
        this.mDesciption = mDesciption;
    }

    public String getmLikes() {
        return mLikes;
    }

    public void setmLikes(String mLikes) {
        this.mLikes = mLikes;
    }

    public boolean getLiked() {
        return mLiked;
    }

    public void setLiked(boolean value) {
        this.mLiked = value;
    }
}
