package com.arc_mobile.arcmobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dgrandhi on 29/07/17.
 */

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    private static final String PREF_NAME = "ArcMobileAppPref";
    private static final String KEY_USER_ID="user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_USER_MOBILE="user_mobile";
    private static final String KEY_USER_COUNTRY="user_country";
    private static final String KEY_STOP_SERVICE="stop_service";
    private static final String KEY_GCM_ID="user_gcm_id";
    private String token=null;

    private Boolean exit_flag=false;

    public Boolean getExit_flag() {
        return exit_flag;
    }

    public void setExit_flag(Boolean exit_flag) {
        this.exit_flag = exit_flag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_MOBILE,user.getUserMobile());
        editor.putString(KEY_USER_ID,user.getUserId());
        editor.putString(KEY_USER_NAME,user.getUserName());
        editor.putString(KEY_USER_COUNTRY,user.getUserCountry());
        editor.commit();
    }

    public void storeGcmId(String gcmId){
        editor.putString(KEY_GCM_ID,gcmId);
        editor.commit();
    }
    public User getUser() {
        if (pref.getString(KEY_USER_MOBILE, null) != null) {
            String mobile;
            mobile=pref.getString(KEY_USER_MOBILE,null);
            User user = new User();
            user.setUserMobile(mobile);
            user.setUserName(pref.getString(KEY_USER_NAME,null));
            user.setUserId(pref.getString(KEY_USER_ID,null));
            user.setUserCountry(pref.getString(KEY_USER_COUNTRY,""));
            user.setUserGcmId(pref.getString(KEY_GCM_ID,""));
            return user;
        }
        return null;
    }

    public void stopSinchClient(){
        editor.putString(KEY_STOP_SERVICE,"Y");
        editor.commit();
    }

    public void startSinchClient(){
        editor.putString(KEY_STOP_SERVICE,"N");
        editor.commit();
    }
    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != "") {
            oldNotifications += "\n|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public void clearNotification(){
        editor.putString(KEY_NOTIFICATIONS,"");
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
