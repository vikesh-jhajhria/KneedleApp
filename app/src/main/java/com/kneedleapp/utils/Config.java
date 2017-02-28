package com.kneedleapp.utils;

/**
 * Created by Vikesh on 02/14/2017.
 */

public class Config {

    public final static String CENTURY_GOTHIC = "CENTURY_GOTHIC";


    public final static int SPLASH_TIME = 2000;

    public final static String BASE_URL = "http://kneedleapp.com/restAPIs/api/";
    public final static String LOGIN = BASE_URL + "user_login";
    public final static String SOCIAL_LOGIN = BASE_URL + "user/fb_connect/?insecure=cool";
    public final static String REGISTER = BASE_URL + "create_user";
    public final static String NONCE = BASE_URL + "get_nonce/?controller=user&method=register";
    public final static String FORGOT_PASSWORD = BASE_URL + "forgot_password";
    public final static String FEED_DATA = BASE_URL + "get_home_feeds";

}
