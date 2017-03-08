package com.kneedleapp.vo;

public class FeedItemVo {


    /*"id":"108",
"user_id":"4",
"fullname":"Srikanth Naladala",
"username":"fashion09",
"caption":"#new",
"city":"Hyderabad",
"state":"Andhra Pradesh",
"date":"2015-09-16 19:19:07 +0530",
"mypic":"1439098099_9173074.jpg",
"country":"India",
"image":"1442411350_2313632.jpg",
"likes_count":"5",
"comment_count":"0",
"comment_1":"",
"comment_2":"",
"likes_status":"1"*/

    public String getmFeedId() {
        return mFeedId;
    }

    public void setmFeedId(String mFeedId) {
        this.mFeedId = mFeedId;
    }

    public String getmCommentCount() {
        return mCommentCount;
    }

    public void setmCommentCount(String value) {
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

    private String mFeedId, mUserImage, mUserTitle, mUserSubTitle, mContentImage,
            mDescription, mLikes, mCommentCount, mComment_1, mComment_2, mDate;
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

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
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
