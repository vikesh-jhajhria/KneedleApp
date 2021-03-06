package com.kneedleapp.vo;

public class FeedItemVo {
    private String mId;
    private String mUserImage;
    private String mFullName;
    private String mUserName;
    private String mContentImage;
    private String mDescription;
    private String mComment_1;
    private String mComment_2;
    private String mDate;
    private String mUserId;
    private String mUsername1;
    private String mUsername2;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private String city;
    private String state;
    private int mCommentCount, mLikes;
    private boolean mLiked;


    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public int getmCommentCount() {
        return mCommentCount;
    }

    public void setmCommentCount(int value) {
        this.mCommentCount = value;
    }

    public String getmComment_1() {
        return mComment_1;
    }

    public void setmComment_1(String value) {
        this.mComment_1 = value;
    }

    public String getmComment_2() {
        return mComment_2;
    }

    public void setmComment_2(String value) {
        this.mComment_2 = value;
    }

    public String getmUsername1() {
        return mUsername1;
    }

    public void setmUsername1(String value) {
        this.mUsername1 = value;
    }

    public String getmUsername2() {
        return mUsername2;
    }

    public void setmUsername2(String value) {
        this.mUsername2 = value;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public boolean ismLiked() {
        return mLiked;
    }

    public void setmLiked(boolean mLiked) {
        this.mLiked = mLiked;
    }

    public String getmUserImage() {
        return mUserImage;
    }

    public void setmUserImage(String mUserImage) {
        this.mUserImage = mUserImage;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String fullName) {
        this.mFullName = fullName;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String username) {
        this.mUserName = username;
    }

    public String getmContentImage() {
        return mContentImage;
    }

    public void setmContentImage(String mContentImage) {
        this.mContentImage = mContentImage;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getmLikes() {
        return mLikes;
    }

    public void setmLikes(int mLikes) {
        this.mLikes = mLikes;
    }

    public boolean getLiked() {
        return mLiked;
    }

    public void setLiked(boolean value) {
        this.mLiked = value;
    }
}
