package com.arc_mobile.arcmobile.fcm;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.arc_mobile.arcmobile.utils.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by dgrandhi on 02/12/17.
 */

public class MyFbInsanceIDService extends FirebaseInstanceIdService {
    @Nullable
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("New Token", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String newToken){
        //hit server with mobile number and save token to server if mobile is registered .
        String url="http://api.arcmobile.vizag.co/1.php?mobile=#MOBILE#&country=India&usergcm=#GCMID#";
        User user= MyApplication.getInstance().getPreferenceManager().getUser();
        if(user==null)
            return;
        url=url.replace("#MOBILE#",user.getUserMobile());
        url=url.replace("#GCMID#",newToken);
        MyApplication.getInstance().getPreferenceManager().storeGcmId(newToken);
        StringRequest request=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("resp",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        });
        MyApplication.getInstance().getmRequestQueue().add(request);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String CurrentToken = FirebaseInstanceId.getInstance().getToken();
        if(MyApplication.getInstance().getPreferenceManager().getUser()!=null) {
            sendRegistrationToServer(CurrentToken);
            Log.e("currentToken",CurrentToken);
            MyApplication.getInstance().getPreferenceManager().storeGcmId(CurrentToken);
        }
    }
}
