package com.arc_mobile.arcmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.services.SinchServicePush;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.arc_mobile.arcmobile.utils.User;
import com.sinch.android.rtc.SinchError;

/**
 * Created by dgrandhi on 23/01/18.
 */

public class InvokeVoiceVideoCallListener extends BaseActivity implements SinchServicePush.StartFailedListener {
    private ProgressDialog mSpinner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        User user=MyApplication.getInstance().getPreferenceManager().getUser();
        if (!user.getUserMobile().equals(MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile())) {
            getSinchServiceInterface().stopClient();
        }
        Log.e("user",user.toString());
        if (getSinchServiceInterface()!=null) {
            getSinchServiceInterface().startClient(MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
            showSpinner();
        } else {
            openDashBoardActivity();
        }
    }

    private void openDashBoardActivity() {
        Intent mainActivity = new Intent(this, DashBoardActivity.class);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openDashBoardActivity();
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }


}
