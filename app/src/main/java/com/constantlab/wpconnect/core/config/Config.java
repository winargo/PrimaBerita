package com.constantlab.wpconnect.core.config;

/**
 * Created by Constant-Lab LLP on 19-04-2017.
 */

public class Config {

    /**
     * BASE API URL
     */
    public static final String PRIMARY_BASE_URL = "https://primaberita.com";

    /**
     * Enable or disable slider on main page
     */
    public static final boolean MAIN_PAGE_SLIDER_REQUIRED = true;

    /**
     * Category ID for slider
     */
    public static final int SLIDER_CATEGORY_ID = 25;

    /**
     * Instagram Setup
     */
    public static final String INSTAGRAM_TOKEN = "6790192441.f677f0e.ac5dfb87a24b410e9e71b1fe7ba5940a";

    /**
     * Facebook Setup
     */
    public static final String FACEBOOK_TOKEN = "";

    /**
     * Youtube Setup
     */
    public static final String YOUTUBE_TOKEN = "AIzaSyDmVCYLk6FWpJNFjXEvoFAS8ZApWliIbtU";

    /**
     * Favorites Options
     */
    public static final boolean SHOW_YOUTUBE_FAVORITES = true;
    public static final boolean SHOW_SOCIAL_FAVORITES = true;
    public static final boolean SHOW_WORDPRESS_FAVORITES = true;

    /**
     * Navigation Setup (Drawer/Sidebar)
     */
    public static final boolean ONLINE_NAVIGATION = false;
    public static final String URL_FOR_ONLINE_NAVIGATION = "https://wpconnect.constant-lab.com/navigation-test.json";
    public static final boolean SECTION_NAME_FOR_SINGLE_SECTION = true;

    /**
     * Wordpress Registration URL
     */
    public static final String REGISTER_URL = "https://primaberita.com/wp-login.php?action=register";

    /**
     * Lost Password URL
     */
    public static final String LOST_PASSWORD_URL = "https://primaberita.com/wp-login.php?action=lostpassword";

    /**
     * Admob Setup
     */
    public static final boolean ABMOB_IN_PRODUCTION = true;
    public static final String ABMOB_APP_ID = "ca-app-pub-5101466294034847~2851654543";
    public static final String ADMOB_AD_UNIT_ID = "ca-app-pub-5101466294034847/4012299763";// "ca-app-pub-5101466294034847/4012299763";

    /**
     * Wordpress Post Rendering
     */
    public static final boolean VISUAL_COMPOSER_RENDER = true;


    /**
     * Webview
     */

    public static final boolean ENABLE_PULL_TO_REFRESH = true;
    public static final boolean WEBVIEW_ZOOM_ALLOWED = true;

}