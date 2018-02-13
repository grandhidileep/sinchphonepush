package com.arc_mobile.arcmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.adapters.MessageAdapter;
import com.arc_mobile.arcmobile.utils.CustomMessage;
import com.arc_mobile.arcmobile.utils.MyApplication;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends BaseActivity implements MessageClientListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();

    private MessageAdapter mMessageAdapter;
    private TextView mTxtRecipient;
    private EditText mTxtTextBody;
    private Button mBtnSend;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);
        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getExtras();
        String name = bundle.getString("NAME");
        number = bundle.getString("NUMBER");

        mTxtRecipient = findViewById(R.id.txtRecipient);
        mTxtRecipient.setText(name);
        mTxtRecipient.setEnabled(false);
        mTxtTextBody = (EditText) findViewById(R.id.txtTextBody);
        fetchExstingMessages();
        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.lstMessages);
        messagesList.setAdapter(mMessageAdapter);

        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void fetchExstingMessages() {
        String url = "http://api.arcmobile.vizag.co/get_im.php?fromnumber=#FRMNUMBER#&tonumber=#TONUMBER#";
        url = url.replace("#FRMNUMBER#", MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
        url = url.replace("#TONUMBER#", number);
        Log.e("url", url);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public void processResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String myId = MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile();
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                String recipientId = jObj.getString("tonumber");
                Log.e("recibool", "" + recipientId.trim().equals("") + " " + i);
                if (recipientId != null && !recipientId.isEmpty()) ;
                {
                    Log.e("reci", recipientId + " " + i);
                    String msg = jObj.getString("msg");
                    CustomMessage message = new CustomMessage();
                    if (!jObj.getString("fromnumber").isEmpty()) {
                        List<String> recipientIds=new ArrayList<>();
                        recipientIds.add(recipientId);
                        message.setRecipientIds(recipientIds);
                        message.setTextBody(msg);
                        message.setSenderId(jObj.getString("fromnumber"));
                        String dateString=jObj.getJSONObject("dt_created").getString("date");
                        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        try {
                            message.setTimestamp(dateFormat.parse(dateString));
                        }catch (ParseException e){
                            Log.e("parseException",e.toString());
                        }
                       // message.setTimestamp();
                        if (myId.equals(jObj.getString("fromnumber"))) {
                            mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                        } else {
                            mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                        }
                    }
                }

            }
        } catch (JSONException je) {

        }
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().removeMessageClientListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onServiceConnected() {
        getSinchServiceInterface().addMessageClientListener(this);
        setButtonEnabled(true);
    }

    @Override
    public void onServiceDisconnected() {
        setButtonEnabled(false);
    }

    private void sendMessage() {
        String recipient = mTxtRecipient.getText().toString();
        String textBody = mTxtTextBody.getText().toString();
        if (recipient.isEmpty()) {
            Toast.makeText(this, "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textBody.isEmpty()) {
            Toast.makeText(this, "No text message", Toast.LENGTH_SHORT).show();
            return;
        }

        getSinchServiceInterface().sendMessage(number, textBody);
        hitEndPoint(mTxtTextBody.getText().toString());
        mTxtTextBody.setText("");
    }

    public void hitEndPoint(String textMessage) {
        String url = "http://api.arcmobile.vizag.co/send_im.php?fromnumber=#FRMNUMBER#&tonumber=#TONUMBER#&msg=#MSG#";
        url = url.replace("#FRMNUMBER#", MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
        url = url.replace("#TONUMBER#", number);
        url = url.replace("#MSG#", textMessage);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    private void setButtonEnabled(boolean enabled) {
        mBtnSend.setEnabled(enabled);
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d(TAG, "onDelivered");
    }
}
