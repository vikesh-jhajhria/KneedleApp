package com.kneedleapp.utils;

/**
 * Created by Vikesh on 02/14/2017.
 */

public class Config {


    public final static int SPLASH_TIME = 2000;

    public final static String BASE_URL = "http://workbench.octagonproject.com/api/";
    public final static String LOGIN = BASE_URL + "user/generate_auth_cookie/?insecure=cool";
    public final static String SOCIAL_LOGIN = BASE_URL + "user/fb_connect/?insecure=cool";
    public final static String REGISTER = BASE_URL + "user/register/?insecure=cool";
    public final static String NONCE = BASE_URL + "get_nonce/?controller=user&method=register";


}
