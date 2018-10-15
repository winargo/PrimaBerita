package prima.optimasi.indonesia.primaberita.core.config;

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
    public static final int SLIDER_CATEGORY_ID = 3321;

    /**
     * Instagram Setup
     */
    public static final String INSTAGRAM_TOKEN = "6790192441.5615b1e.58537afbb5a54c01b9fbe1a4e33f4088";

    public static final String INSTAGRAM_TOKEN_PLUS = "8386293646.0460d13.461788ace92745759cf8190da957ec96";
    /*
    * https://www.instagram.com/oauth/authorize?client_id=YOUR_CLIENT&redirect_uri=YOUR_REDIRECT_URI&scope=basic+likes+comments+relationships+public_content&response_type=token
    * */
    /**
     * Facebook Setup
     */
    public static final String FACEBOOK_TOKEN = "1913814258917899|75Pjb6-zQIBbCCpuQ3mLU5aA1s4";

    /**
     * Youtube Setup
     */
    public static final String YOUTUBE_TOKEN = "AIzaSyCOLoLXdeZk2p7oVruZN6HESSqTiQVIVXw";

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
    public static final String ABMOB_APP_ID = "ca-app-pub-5101466294034847~2892823049";
    public static final String ADMOB_AD_UNIT_ID =  "ca-app-pub-5101466294034847/5650848326";// "ca-app-pub-5101466294034847/5650848326";//

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