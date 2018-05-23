package jgc.asai.gwtoauth.client;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import jgc.asai.gwtoauth.shared.UrlResources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Utils {
    static final Logger logger = java.util.logging.Logger.getLogger("Utils");

    private final static String SESSION_ID_COOKIE         = "gwtoauth_google_session_id";
    private final static String AUTH_PROVIDER_COOKIE      = "gwtoauth_google_provider";
    private final static String AUTH_PROVIDER_NAME_COOKIE = "gwtoauth_google_provider_name";
    private final static String USERNAME_COOKIE           = "gwtoauth_google_user";
    private final static String REDIRECT_URL_COOKIE       = "gwtoauth_google_redirect_url";

    public final static String FACEBOOK = "facebook";
    public final static String GOOGLE = "google";
    public final static String GOOGLE_PLUS = "google plus";
    public final static String GOOGLE_DRIVE = "google drive";
    public final static String DEFAULT = "default";
    public final static String UNKNOWN = "unknown";

    public static String getAuthProviderName(String authProvider){
        logger.info("getAuthProviderName");
        if (authProvider.toLowerCase().equals(FACEBOOK))
            return FACEBOOK;
        else if (authProvider.toLowerCase().equals(GOOGLE))
            return GOOGLE;
        return DEFAULT;
    }

    public static String getApiName(String apiName){
        logger.info("getApiName: "+apiName);
        if (apiName.toLowerCase().equals(GOOGLE_PLUS))
            return GOOGLE_PLUS;
        else if (apiName.toLowerCase().equals(GOOGLE_DRIVE))
            return GOOGLE_DRIVE;
        return UNKNOWN;
    }

    public static String getUrlResource(String apiName){
        logger.info("getUrlResource: "+apiName);
        switch(apiName){
            case GOOGLE_DRIVE:
                return UrlResources.GOOGLE_DRIVE_FILES;
            case GOOGLE_PLUS:
                return UrlResources.GOOGLE_PLUS_ME;
        }
        return UNKNOWN;
    }

    public static String getSessionIdFromCookie(){
        logger.info("getSessionIdFromCookie: "+Cookies.getCookie(SESSION_ID_COOKIE));
        return Cookies.getCookie(SESSION_ID_COOKIE);
    }

    public static String getAuthProviderFromCookie(){
        logger.info("getAuthProviderFromCookie: "+Cookies.getCookie(AUTH_PROVIDER_COOKIE));
        return Cookies.getCookie(AUTH_PROVIDER_COOKIE);
    }

    public static void clearCookies(){
        logger.info("clearCookies");
        Cookies.removeCookie(SESSION_ID_COOKIE);
        Cookies.removeCookie(AUTH_PROVIDER_COOKIE);
        Cookies.removeCookie(AUTH_PROVIDER_NAME_COOKIE);
        Cookies.removeCookie(USERNAME_COOKIE);
        Cookies.removeCookie(REDIRECT_URL_COOKIE);
    }
    public static String getAuthProviderNameFromCookie(){
        return Cookies.getCookie(AUTH_PROVIDER_NAME_COOKIE);
    }

    public static String getUsernameFromCookie(){
        return Cookies.getCookie(USERNAME_COOKIE);
    }

    public static boolean alreadyLoggedIn(){
        logger.info("alreadyLoggedIn: "+getSessionIdFromCookie());
        if (getSessionIdFromCookie() != null)
            return true;
        return false;
    }

    public static void saveSessionId(String sessionId){
        Cookies.setCookie(SESSION_ID_COOKIE,sessionId);
    }

    public static void saveAuthProvider(String authProvider){
        logger.info("saveAuthProvider: "+authProvider);
        Cookies.setCookie(AUTH_PROVIDER_COOKIE,authProvider);
        String authProviderName = getAuthProviderName(authProvider);
        saveAuthProviderName(authProviderName);
    }

    public static void saveAuthProviderName(String authProviderName){
        logger.info("saveAuthProviderName: "+authProviderName);
        Cookies.setCookie(AUTH_PROVIDER_NAME_COOKIE,authProviderName);
    }

    public static void saveUsername(String username){
        Cookies.setCookie(USERNAME_COOKIE,username);
    }

    public static void saveRediretUrl(String url){
        logger.info("saveRediretUrl: "+url);
        Cookies.setCookie(REDIRECT_URL_COOKIE,url);
    }

    public static String getRedirectUrlFromCookie(){
        logger.info("getRedirectUrlFromCookie");
        return Cookies.getCookie(REDIRECT_URL_COOKIE);
    }

    public static void redirect(String url){
        logger.info("redirect: "+url);
        Window.Location.assign(url);
    }

    public static boolean redirected(){
        logger.info("redirected");
        String authProvider = getAuthProviderFromCookie();
        if (authProvider == null){
            return false;
        }
        if (Window.Location.getParameter("code") != null)
            return true;
        String error = Window.Location.getParameter("error");
        if (error != null){
            String errorMessage = Window.Location.getParameter("error_description");
            Window.alert("Error: " + error + ":" + errorMessage);
            reload();
            return false;
        }
        return false;
    }

    public static void reload(){
        logger.info("reload");
        String appUrl = getRedirectUrlFromCookie();
        String savedAuthProvider = getAuthProviderFromCookie();

        clearCookies();

        if (savedAuthProvider.equals(DEFAULT)){
        //    BaseApp.get().updateLoginStatus();
        }

        if (appUrl != null){
            redirect(appUrl);
        }
    }
}
