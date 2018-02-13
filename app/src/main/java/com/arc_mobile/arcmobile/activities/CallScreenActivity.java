package com.arc_mobile.arcmobile.activities;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.services.SinchServicePush;
import com.arc_mobile.arcmobile.utils.AudioPlayer;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallId = getIntent().getStringExtra(SinchServicePush.CALL_ID);
    }

    @Override
    public void onServiceConnected() {
        Log.e("here","1");
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if(call.getRemoteUserId()!=MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile())
                invokeServerWithStatistics(call);
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    public void invokeServerWithStatistics(Call call){
        String url="http://api.arcmobile.vizag.co/3.php?fromuserid=#FROMUSERID#" +
                "&touserid=" +
                "&fromnumber=#FROMNUMBER#&" +
                "tonumber=#TONUMBER#&" +
                "starttime=#STARTTIME#&" +
                "endtime=#ENDTIME#&" +
                "establishtime=#ESTABLISHEDTIME#&" +
                "duration=#DURATION#&" +
                "terminationcause=#TERMINATIONCAUSE#&" +
                "errorcode=#ERRORCODE#" +
                "&video=#VIDEO#";

        url=url.replace("#FROMUSERID#", MyApplication.getInstance().getPreferenceManager().getUser().getUserId());
        url=url.replace("#FROMNUMBER#",MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
        url=url.replace("#TONUMBER#",call.getRemoteUserId());
        url=url.replace("#STARTTIME#",""+call.getDetails().getStartedTime());
        url=url.replace("#ENDTIME#",""+call.getDetails().getEndedTime());
        url=url.replace("#ESTABLISHEDTIME#",""+call.getDetails().getEstablishedTime());
        url=url.replace("#DURATION#",""+call.getDetails().getDuration());
        url=url.replace("#TERMINATIONCAUSE#",call.getDetails().getEndCause().toString());
        if(call.getDetails().getError()!=null)
            url=url.replace("#ERRORCODE#",call.getDetails().getError().toString());
        url=url.replace("#VIDEO#",""+call.getDetails().isVideoOffered());
        StringRequest request=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().getmRequestQueue().add(request);
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.e(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.e(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }
}
