package com.arc_mobile.arcmobile.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.arc_mobile.arcmobile.activities.IncomingCallScreenActivity;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.sinch.android.rtc.video.VideoController;

import java.util.Map;

public class SinchServicePush extends Service {

    private static final String APP_KEY = "da34e4d0-0fcd-4644-9738-94bb8b56d8f1";
    private static final String APP_SECRET = "ycq8FgcMQ0+eTtjXqs1vfg==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    public static final String CALL_ID = "CALL_ID";
    static final String TAG = SinchServicePush.class.getSimpleName();
    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private SinchClient mSinchClient;

    private StartFailedListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        String userName = MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile();
        if (!userName.isEmpty()) {
            start(userName);
        }
    }

    private void createClient(String username) {
        mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(username)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportCalling(true);
        mSinchClient.setSupportMessaging(true);
        mSinchClient.setSupportManagedPush(true);
        mSinchClient.setSupportActiveConnectionInBackground(true);
        mSinchClient.registerPushNotificationData(MyApplication.getInstance().getPreferenceManager().getUser().getUserGcmId().getBytes());
        mSinchClient.checkManifest();

        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        super.onDestroy();
    }

    private void start(String username) {
        if (mSinchClient == null) {
            createClient(username);
        }
        Log.d(TAG, "Starting SinchClient");
        mSinchClient.start();
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public Call callUserVideo(String userId) {
            return mSinchClient.getCallClient().callUserVideo(userId);
        }

        public Call callUser(String userId){
            return mSinchClient.getCallClient().callUser(userId);
        }


        public String getUserName() {
            return mSinchClient != null ? mSinchClient.getLocalUserId() : "";
        }

        public boolean isStarted() {
            return SinchServicePush.this.isStarted();
        }

        public void startClient(String userName) {
            start(userName);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }

        public void sendMessage(String recipientUserId, String textBody) {
            SinchServicePush.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            SinchServicePush.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            SinchServicePush.this.removeMessageClientListener(listener);
        }

        public VideoController getVideoController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getVideoController();
        }



        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        public NotificationResult relayRemotePushNotificationPayload(final Map payload) {
            if (mSinchClient == null && !MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile().isEmpty()) {
                createClient(MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
            } else if (mSinchClient == null && MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile().isEmpty()) {
                Log.e(TAG, "Can't start a SinchClient as no username is available, unable to relay push.");
                return null;
            }
            return mSinchClient.relayRemotePushNotificationPayload(payload);
        }

//        public NotificationResult relayRemotePushNotificationPayload(final Map payload) {
//            if (mSinchClient == null && !mSettings.getUsername().isEmpty()) {
//                createClient(mSettings.getUsername());
//            } else if (mSinchClient == null && mSettings.getUsername().isEmpty()) {
//                Log.e(TAG, "Can't start a SinchClient as no username is available, unable to relay push.");
//                return null;
//            }
//            return mSinchClient.relayRemotePushNotificationPayload(payload);
//        }
    }

    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }


        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                ClientRegistration clientRegistration) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");
            Intent intent = new Intent(SinchServicePush.this, IncomingCallScreenActivity.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SinchServicePush.this.startActivity(intent);
        }
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (isStarted()) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            mSinchClient.getMessageClient().send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().removeMessageClientListener(listener);
        }
    }


}
