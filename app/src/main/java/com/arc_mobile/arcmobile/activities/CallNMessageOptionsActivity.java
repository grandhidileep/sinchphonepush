package com.arc_mobile.arcmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.services.SinchServicePush;
import com.sinch.android.rtc.calling.Call;

/**
 * Created by dgrandhi on 31/01/18.
 */

public class CallNMessageOptionsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_n_message_options);
        TextView tvName=findViewById(R.id.tv_callnmsg_name);
        TextView tvPhone=findViewById(R.id.tv_callnmsg_phone);
        Intent gi=getIntent();
        final String name=gi.getExtras().getString("NAME");
        final String number=gi.getExtras().getString("NUMBER");
        tvName.setText(name);
        tvPhone.setText(number);
        ImageButton call,videocall,message;
        call=findViewById(R.id.img_button_voice_call);
        videocall=findViewById(R.id.img_button_video_call);
        message=findViewById(R.id.img_button_message);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call call = getSinchServiceInterface().callUser(number);
                Intent callScreen = new Intent(CallNMessageOptionsActivity.this, CallScreenActivity.class);
                startActivity(callScreen);
                String callId = call.getCallId();
                callScreen.putExtra(SinchServicePush.CALL_ID, callId);
                startActivity(callScreen);
            }
        });
        videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call call = getSinchServiceInterface().callUserVideo(number);
                Intent callScreen = new Intent(CallNMessageOptionsActivity.this, VideoCallScreenActivity.class);
                startActivity(callScreen);
                String callId = call.getCallId();
                callScreen.putExtra(SinchServicePush.CALL_ID, callId);
                startActivity(callScreen);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(CallNMessageOptionsActivity.this,MessagingActivity.class);
                i.putExtra("NAME",name);
                i.putExtra("NUMBER",number);
                startActivity(i);
            }
        });
    }
}
