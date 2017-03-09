package com.kneedleapp.vo;

public class FeedItemVo {
    private String mId, mUserImage, mUserTitle, mUserSubTitle, mContentImage,
            mDescription, mComment_1, mComment_2, mDate;
    private int mCommentCount, mLikes;
    private boolean mLiked;

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
