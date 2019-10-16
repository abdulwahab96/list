package com.handicape.MarketCreators.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.handicape.MarketCreators.Account.PreferencesUtility.EMAIL;
import static com.handicape.MarketCreators.Account.PreferencesUtility.E_PAYPAL;
import static com.handicape.MarketCreators.Account.PreferencesUtility.IMAGE;
import static com.handicape.MarketCreators.Account.PreferencesUtility.LOGGED_IN_PREF;
import static com.handicape.MarketCreators.Account.PreferencesUtility.URL_IMAGE;
import static com.handicape.MarketCreators.Account.PreferencesUtility.USER_NAME;

public class SessionSharedPreference {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     *
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn, String userName, String email) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.putString(USER_NAME, userName);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public static void setImageSherPref(Context context, String image) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(IMAGE, image);
        editor.apply();
    }

    /**
     * Get the Login Status
     *
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getUserName(Context context) {
        return getPreferences(context).getString(USER_NAME, "");
    }

    public static String getEmail(Context context) {
        return getPreferences(context).getString(EMAIL, "");
    }

    public static String getImage(Context context) {
        return getPreferences(context).getString(IMAGE, "");
    }

    public static void setEPaypal(String E_Paypal, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(E_PAYPAL, E_Paypal);
        editor.apply();

    }
    public static String getEPaypal(Context context) {
        return getPreferences(context).getString(E_PAYPAL, "");
    }

    public static void setUrlImage(String url_image, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(URL_IMAGE, url_image);
        editor.apply();

    }
    public static String getUrlImage(Context context) {
        return getPreferences(context).getString(URL_IMAGE, "");
    }

}
