package com.arc_mobile.arcmobile.activities;

import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.services.SinchServicePush;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class PlaceCallActivity extends BaseActivity {

    //private Button mCallButton;
    private ImageButton videoCallButton, voiceCallButton;
    private EditText mCallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_call);
        mCallName = findViewById(R.id.callName);
        voiceCallButton = findViewById(R.id.callButton);
        voiceCallButton.setEnabled(false);
        voiceCallButton.setOnClickListener(buttonClickListener);
        videoCallButton = findViewById(R.id.videoCallButton);
        videoCallButton.setEnabled(false);
        videoCallButton.setOnClickListener(buttonClickListener);
    }

    @Override
    protected void onServiceConnected() {
        TextView userName = (TextView) findViewById(R.id.loggedInName);
        userName.setText(MyApplication.getInstance().getPreferenceManager().getUser().getUserName());
        voiceCallButton.setEnabled(true);
        videoCallButton.setEnabled(true);
    }


    private void callButtonClicked() {
        String userName = mCallName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a mobile no to call", Toast.LENGTH_LONG).show();
            return;
        }
        requestUserId(userName,false);

//        try {
//            Log.e("here", "1");
////            Call call = getSinchServiceInterface().callUser(userName);
////            if (call == null) {
////                // Service failed for some reason, show a Toast and abort
////                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
////                        + "placing a call.", Toast.LENGTH_LONG).show();
////                return;
////            }
////            String callId = call.getCallId();
////            Log.e("callId",callId);
////            Intent callScreen = new Intent(this, CallScreenActivity.class);
////            callScreen.putExtra(SinchService.CALL_ID, callId);
////            startActivity(callScreen);
//        } catch (MissingPermissionException e) {
//            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
//        }

    }

    private void videoCallButtonClicked() {
        String userName = mCallName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }
        requestUserId(userName,true);

//        Call call = getSinchServiceInterface().callUserVideo(userName);
//        String callId = call.getCallId();
//
//        Intent callScreen = new Intent(this, VideoCallScreenActivity.class);
//        callScreen.putExtra(SinchService.CALL_ID, callId);
//        startActivity(callScreen);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("buttonClicked", "" + v.getId());
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.videoCallButton:
                    videoCallButtonClicked();
                    break;
            }
        }
    };

    public void placeVoiceOrVideoCall(Boolean flag, String userName) {
        Log.e("here","1");
        try {
            Call call;
            Intent callScreen;
            if (flag) {
                call = getSinchServiceInterface().callUserVideo(userName);
                callScreen = new Intent(this, VideoCallScreenActivity.class);
            } else {
                call = getSinchServiceInterface().callUser(userName);
                callScreen = new Intent(this, CallScreenActivity.class);
            }
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG);
                return;
            }
            String callId = call.getCallId();
            callScreen.putExtra(SinchServicePush.CALL_ID, callId);
            startActivity(callScreen);


        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }

    public void requestUserId(String username, final Boolean flag){
        Log.e("requestUserId",username);
        if(username.startsWith("+"))
            placeVoiceOrVideoCall(flag,username.substring(1,username.length()));
        else
            placeVoiceOrVideoCall(flag,username);
    }


}
