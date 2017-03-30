package com.kneedleapp.vo;

import android.text.SpannableString;

import com.kneedleapp.BaseActivity;

/**
 * Created by aman.sharma on 2/22/2017.
 */

public class NotificationItemVo {

    public static final int DAY = 0;
    public static final int NOTIFICATION = 1;

    private String mId = "";
    private String mFeedId = "";
    private String mTime = "";
    private String mUserId = "";
    private String mFullName = "";
    private String mUsername = "";
    private String mComment = "";
    private String mFollowerId = "";
    private String mFollowingId = "";
    private String mTaggedUserId = "";
    private String mImgUser = "";
    private String mImgContent = "";
    private BaseActivity.NotificationType mType;

    /*"id":"119",
    "feed_id":"75",
    "time":"2015-09-21 09:48:29 +0530",
    "user_id":"4",
    "fullname":"Srikanth Naladala",
    "username":"fashion09",
    "profile_pic":"http:\/\/kneedleapp.com\/restAPIs\/uploads\/user_images\/1439098099_9173074.jpg",
    "notificationType":"Like"

    "id":"542",
    "feed_id":"75",
    "comment":"444444",
    "time":"2015-10-23 14:39:41 +0530",
    "user_id":"39",
    "fullname":"Srikant",
    "username":"fashion08",
    "profile_pic":"",
    "notificationType":"Comment"

    "f_id":"368",
    "time":"2016-09-25 12:28:04 +0530",
    "user_id":"39",
    "follower_id":"22",
    "fullname":"Srikant",
    "username":"fashion08",
    "profile_pic":"",
    "notificationType":"Follow"

    "id":"44",
    "taged_user_id":"22",
    "time":"2015-07-29 15:00:13 +0530",
    "feed_id":"75",
    "fullname":"Srikanth Naladala",
    "username":"fashion09",
    "profile_pic":"1439098099_9173074.jpg",
    "notificationType":"Taged"
    */


    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getFeedId() {
        return mFeedId;
    }

    public void setFeedId(String mFeedId) {
        this.mFeedId = mFeedId;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public String getFollowerId() {
        return mFollowerId;
    }

    public void setFollowerId(String mFollowerId) {
        this.mFollowerId = mFollowerId;
    }

    public String getFollowingId() {
        return mFollowingId;
    }

    public void setFollowingId(String mFollowingId) {
        this.mFollowingId = mFollowingId;
    }

    public String getTaggedUserId() {
        return mTaggedUserId;
    }

    public void setTaggedUserId(String mTaggedUserId) {
        this.mTaggedUserId = mTaggedUserId;
    }
    public BaseActivity.NotificationType getType() {
        return mType;
    }

    public void setType(BaseActivity.NotificationType mType) {
        this.mType = mType;
    }

    public NotificationItemVo() {}

    public String getImgUser() {
        return mImgUser;
    }

    public void setImgUser(String mImgUser) {
        this.mImgUser = mImgUser;
    }

    public String getImgContent() {
        return mImgContent;
    }

    public void setImgContent(String mImgContent) {
        this.mImgContent = mImgContent;
    }

}
