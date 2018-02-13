package com.arc_mobile.arcmobile.utils;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arc_mobile.arcmobile.fcm.FcmListenerService;
import com.arc_mobile.arcmobile.fcm.MyFbInsanceIDService;

/**
 * Created by dgrandhi on 29/07/17.
 */

public class MyApplication extends Application {
    public static final String TAG="Application";
    private RequestQueue mRequestQueue;
    private static MyApplication mInstance ;
    private MyPreferenceManager preferenceManager;

    public void onCreate(){
        super.onCreate();
        mInstance=this;
        new MyFbInsanceIDService().onCreate();
        new FcmListenerService();
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getmRequestQueue(){
        if(this.mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
            Log.e("request queue","1");
        }
        return mRequestQueue;
    }

    public MyPreferenceManager getPreferenceManager(){
        if(preferenceManager==null)
            preferenceManager=new MyPreferenceManager(this);
        return preferenceManager;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getmRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getmRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void logout() {
        preferenceManager.clear();

        /*************************** replace MainAcivity with Login screen ************************/
       // Intent intent = new Intent(this, MainActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // startActivity(intent);
    }
}
