package com.kneedleapp.vo;

/**
 * Created by aman.sharma on 2/23/2017.
 */

public class CommentVo {


    private String mId;
    private String mUserName;
    private String mComment;
    private String mUserImageUrl;
    private String mCommentFrom;
    private String mDate;

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


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
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
