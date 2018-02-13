package com.arc_mobile.arcmobile.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.arc_mobile.arcmobile.utils.User;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.arc_mobile.arcmobile.activities.MainActivity.INTENT_PHONENUMBER;

public class VerificationActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    Button proceed;

    private static final String TAG = "VerificationActivity";

    private static final String APPLICATION_KEY = "22f4e3f1-e4a2-4849-8b3b-4548dd5904fe";

    private Verification mVerification;
    private boolean mIsSmsVerification;
    private boolean mShouldFallback = true;
    private boolean mIsVerified;
    private String mPhoneNumber;
    private static final String[] SMS_PERMISSIONS = { Manifest.permission.INTERNET,
             Manifest.permission.READ_SMS,
             Manifest.permission.RECEIVE_SMS,
             Manifest.permission.ACCESS_NETWORK_STATE};
    private static final String[] FLASHCALL_PERMISSIONS = { Manifest.permission.INTERNET,
             Manifest.permission.READ_PHONE_STATE,
             Manifest.permission.READ_CALL_LOG,
             Manifest.permission.CALL_PHONE,
             Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        proceed =findViewById(R.id.btn_activity_verfication_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateUserId();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            mPhoneNumber = intent.getStringExtra(INTENT_PHONENUMBER);
            final String method = intent.getStringExtra(MainActivity.INTENT_METHOD);
            mIsSmsVerification = method.equalsIgnoreCase(MainActivity.SMS);
            TextView phoneText = (TextView) findViewById(R.id.numberText);
            phoneText.setText(mPhoneNumber);

            requestPermissions();
        } else {
            Log.e(TAG, "The provided intent is null.");
        }
    }

    private void requestPermissions() {
        List<String> missingPermissions;
        String methodText;

        if (mIsSmsVerification) {
            missingPermissions = getMissingPermissions(SMS_PERMISSIONS);
            methodText = "SMS";
        } else {
            missingPermissions = getMissingPermissions(FLASHCALL_PERMISSIONS);
            methodText = "calls";
        }

        if (missingPermissions.isEmpty()) {
            createVerification();
        } else {
            if (needPermissionsRationale(missingPermissions)) {
                Toast.makeText(this, "This application needs permissions to read your " + methodText + " to automatically verify your "
                        + "phone, you may disable the permissions once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
            ActivityCompat.requestPermissions(this,
                                              missingPermissions.toArray(new String[missingPermissions.size()]),
                                              0);
        }
    }

    private void createVerification() {
        // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        Config config = SinchVerification.config()
                                         .applicationKey(APPLICATION_KEY)
                                         .context(getApplicationContext())
                                         .build();
        TextView messageText = (TextView) findViewById(R.id.textView);

        VerificationListener listener = new MyVerificationListener();

        if (mIsSmsVerification) {
            messageText.setText(R.string.sending_sms);
            mVerification = SinchVerification.createSmsVerification(config, mPhoneNumber, listener);
            mVerification.initiate();
        } else {
            messageText.setText(R.string.flashcalling);
            mVerification = SinchVerification.createFlashCallVerification(config, mPhoneNumber, listener, mShouldFallback);
            mVerification.initiate();
        }

        showProgress();
    }

    private boolean needPermissionsRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Proceed with verification after requesting permissions.
        // If the verification SDK fails to intercept the code automatically due to missing permissions,
        // the VerificationListener.onVerificationFailed(1) method will be executed with an instance of
        // CodeInterceptionException. In this case it is still possible to proceed with verification
        // by asking the user to enter the code manually.
        createVerification();
    }

    private List<String> getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    public void onSubmitClicked(View view) {
        String code = ((EditText) findViewById(R.id.inputCode)).getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);
                showProgress();
                TextView messageText = (TextView) findViewById(R.id.textView);
                messageText.setText("Verification in progress");
                enableInputField(false);
            }
        }
    }

    private void enableInputField(boolean enable) {
        View container = findViewById(R.id.inputContainer);
        if (enable) {
            TextView hintText = (TextView) findViewById(R.id.enterToken);
            hintText.setText(mIsSmsVerification ? R.string.sms_enter_code : R.string.flashcall_enter_cli);
            container.setVisibility(View.VISIBLE);
            EditText input = (EditText) findViewById(R.id.inputCode);
            input.requestFocus();
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private void hideProgressAndShowMessage(int message) {
        hideProgress();
        TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);
    }

    private void hideProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
        TextView progressText = (TextView) findViewById(R.id.progressText);
        progressText.setVisibility(View.INVISIBLE);
    }

    private void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showCompleted() {
        ImageView checkMark = (ImageView) findViewById(R.id.checkmarkImage);
        checkMark.setVisibility(View.VISIBLE);
    }

    class MyVerificationListener implements VerificationListener {

        @Override
        public void onInitiated(InitiationResult result) {
            Log.d(TAG, "Initialized!");
            showProgress();
        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            hideProgressAndShowMessage(R.string.failed);

            if (exception instanceof InvalidInputException) {
                // Incorrect number provided
            } else if (exception instanceof ServiceErrorException) {
                // Verification initiation aborted due to early reject feature,
                // client callback denial, or some other Sinch service error.
                // Fallback to other verification method here.
                fallbackIfNecessary();
            } else {
                // Other system error, such as UnknownHostException in case of network error
            }
        }

        @Override
        public void onVerificationFallback() {
            fallbackIfNecessary();
        }

        private void fallbackIfNecessary() {
            if (mShouldFallback) {
                mIsSmsVerification = !mIsSmsVerification;
                if (mIsSmsVerification) {
                    Log.i(TAG, "Falling back to sms verification.");
                } else {
                    Log.i(TAG, "Falling back to flashcall verification.");
                }
                mShouldFallback = false;
                requestPermissions(); // Initiate verification with the alternative method.
            }
        }

        @Override
        public void onVerified() {
            mIsVerified = true;
            Log.d(TAG, "Verified!");
            hideProgressAndShowMessage(R.string.verified);
            showCompleted();
            proceed.setVisibility(View.VISIBLE);

        }

        @Override
        public void onVerificationFailed(Exception exception) {
            if(mIsVerified) {
                return;
            }

            Log.e(TAG, "Verification failed: " + exception.getMessage());
            if (exception instanceof CodeInterceptionException) {
                // Automatic code interception failed, probably due to missing permissions.
                // Let the user try and enter the code manually.
                hideProgress();
            } else {
                hideProgressAndShowMessage(R.string.failed);
            }
            enableInputField(true);
        }
    }

    public void generateUserId(){
        String url="http://api.arcmobile.vizag.co/1.php?mobile=#MOBILE#&country=India";
        if(mPhoneNumber!=null)
            url=url.replace("#MOBILE#",mPhoneNumber);
        else
            url=url.replace("#MOBILE#", MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
        StringRequest request=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public void processResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObj=jsonObject.getJSONObject("result");
            JSONObject jObj=jsonObj.getJSONObject("user_data");
            User user=new User();
            user.setUserId(jObj.getString("userid"));
            String mobile=jObj.getString("mobile");
            user.setUserMobile(mobile.substring(1,mobile.length()));
            user.setUserCountry("");
            user.setUserName("");
            Log.e("verifi",user.toString());
            MyApplication.getInstance().getPreferenceManager().storeUser(user);
            Intent i=new Intent(VerificationActivity.this,InvokeVoiceVideoCallListener.class);
            i.putExtra(INTENT_PHONENUMBER,mPhoneNumber);
            startActivity(i);
        }catch (JSONException je){

        }
    }

}
