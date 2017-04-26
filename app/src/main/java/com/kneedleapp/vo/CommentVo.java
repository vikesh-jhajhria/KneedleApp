package com.kneedleapp.vo;

/**
 * Created by aman.sharma on 2/23/2017.
 */

public class CommentVo {


    private String mCommentId;
    private String mUserId;
    private String mUserName;
    private String mComment;
    private String mUserImageUrl;
    private String mCommentFrom;
    private String mDate;
    
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }
    public String getmCommentFrom() {
        return mCommentFrom;
    }

    public void setmCommentFrom(String mCommentFrom) {
        this.mCommentFrom = mCommentFrom;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }


    public String getmCommentId() {
        return mCommentId;
    }

    public void setmCommentId(String mCommentId) {
        this.mCommentId = mCommentId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public String getmUserImageUrl() {
        return mUserImageUrl;
    }

    public void setmUserImageUrl(String mUserImageUrl) {
        this.mUserImageUrl = mUserImageUrl;
    }
}
