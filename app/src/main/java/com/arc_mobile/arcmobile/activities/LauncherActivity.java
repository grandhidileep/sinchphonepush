package com.arc_mobile.arcmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.utils.MyApplication;

/**
 * Created by dgrandhi on 23/01/18.
 */

public class LauncherActivity extends Activity {
    private static int SPLASH_TIME_OUT=4000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent intent;
                if(MyApplication.getInstance().getPreferenceManager().getUser()==null){
                    intent=new Intent(LauncherActivity.this,MainActivity.class);
                }else{
                    intent=new Intent(LauncherActivity.this,InvokeVoiceVideoCallListener.class);
                }
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);
    }
}
