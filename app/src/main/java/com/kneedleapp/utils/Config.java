package com.kneedleapp.utils;

import android.net.Uri;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by Vikesh on 02/14/2017.
 */

public class Config {

    public final static String CENTURY_GOTHIC_REGULAR = "CENTURY_GOTHIC_REGULAR";
    public final static String CENTURY_GOTHIC_BOLD = "CENTURY_GOTHIC_BOLD";
    public static final int LOCATION_PERMISSION = 1001;
    public static final int MEDIA_PERMISSION = 1002;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static FragmentManager fragmentManager;
    public static Uri CAMERAFILEURI;
    public static String LAST_PAGE = "";
    public static final int CAMERAIMAGE = 123;
    public static final int GALLERYIMAGE = 124;
    public static final int CROPIMAGE = 127;
    public static final int ROTATEIMAGE = 128;
    public static final String IMAGE_DIRECTORY_NAME = "KNEEDLE";

    public static ArrayList<String> PROFILE_TYPE = new ArrayList<>();

    public static final String TWITTER_KEY = "IPXy5QI9bvsbg0E6X5RPyI8V3";
    public static final String TWITTER_SECRET = "KIheMUxPOpnnsCZkC6kxwKGVTiAOGoRhecPcnwee1eyPzOLjxF";

    public final static int SPLASH_TIME = 2000;
    public final static String BASE_URL = "http://kneedleapp.com/restAPIs/api/";
    public final static String USER_IMAGE_URL = "http://kneedleapp.com/restAPIs/uploads/user_images/";
    public final static String FEED_IMAGE_URL = "http://kneedleapp.com/restAPIs/uploads/post_images/";
    public final static String LOGIN = BASE_URL + "user_login";
    public final static String SOCIAL_LOGIN = BASE_URL + "user/fb_connect/?insecure=cool";
    public final static String REGISTER = BASE_URL + "create_user";
    public final static String NONCE = BASE_URL + "get_nonce/?controller=user&method=register";
    public final static String FORGOT_PASSWORD = BASE_URL + "forgot_password";
    public final static String FEED_DATA = BASE_URL + "get_home_feeds";
    public final static String FOLLOWERS = BASE_URL + "get_followers";
    public final static String FOLLOWING = BASE_URL + "get_following";
    public final static String FOLLOW_UNFOLLOW_USER = BASE_URL + "add_followers";
    public final static String USER_DETAILS = BASE_URL + "get_user_details";
    public final static String ADD_LIKE = BASE_URL + "add_likes";
    public final static String GET_FEED_COMMENTS = BASE_URL + "get_feed_comments";
    public final static String ADD_COMMENT = BASE_URL + "add_comment";
    public final static String GET_NOTIFICATIONS = BASE_URL + "get_notifications";
    public final static String GET_SEARCH_ITEM = BASE_URL + "searchtext";
    public final static String POST_COMMENT = BASE_URL + "add_feed";
    public final static String REPORT_PROBLEM = BASE_URL + "report_a_problem";
    public final static String BLOCK = BASE_URL + "block_unblock_user";
    public final static String DELETE_FEED = BASE_URL + "delete_feed";
    public final static String GET_USER_DETAILS = BASE_URL + "get_user_details";
    public final static String LOG_OUT = BASE_URL + "logout";
    public final static String TERMS_CONDITION = BASE_URL + "terms_conditions";
    public final static String GET_CATEGORY = BASE_URL + "get_categories";
    public final static String GET_USER_FEEDS = BASE_URL + "get_user_feeds";
    public final static String UPDATE_PROFILE_PIC = BASE_URL + "update_profile_pic";
    public final static String UPDATE_USER_PROFILE = BASE_URL + "update_user_profile";
    public final static String GET_DROPDOWN = BASE_URL + "get_dropdowns";
    public final static String GET_BLOCKED_USERS = BASE_URL + "blocked_users";
    public final static String GET_FEED_INFO = BASE_URL + "get_feed_info";

}
