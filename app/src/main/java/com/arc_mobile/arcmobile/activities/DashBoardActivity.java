package com.arc_mobile.arcmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.contactUtils.Contact;
import com.arc_mobile.arcmobile.utils.MyApplication;

/**
 * Created by dgrandhi on 23/01/18.
 */

public class DashBoardActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MyApplication.getInstance().getPreferenceManager().getUser().getUserGcmId()!=null)
            Log.e("gcmid",MyApplication.getInstance().getPreferenceManager().getUser().getUserGcmId());
        else
            Log.e("gcmid","not_exists");
        setContentView(R.layout.activity_dashboard);
        ImageButton call=findViewById(R.id.btn_activity_dash_call);
        ImageButton pricing= findViewById(R.id.btn_pricing);
        ImageButton dialer=findViewById(R.id.btn_dialer_bottom);
        ImageButton wallet=findViewById(R.id.btn_wallet);
        ImageButton callHistory=findViewById(R.id.btn_call_history);
        ImageButton callHistoryBottom=findViewById(R.id.btn_history_bottom);
        ImageButton customerService=findViewById(R.id.btn_customer_service);
        ImageButton home=findViewById(R.id.btn_home_button);
        ImageButton contacts = findViewById(R.id.btn_phone_book_bottom);
        ImageButton messaging =findViewById(R.id.btn_messaging);
        Log.e("User", MyApplication.getInstance().getPreferenceManager().getUser().toString());
        pricing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashBoardActivity.this,PricingActivity.class);
                startActivity(i);
            }
        });

        dialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashBoardActivity.this,PlaceCallActivity.class);
                startActivity(i);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashBoardActivity.this,WalletActivity.class);
                startActivity(i);
            }
        });

        callHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(DashBoardActivity.this,CallHistory.class);
                startActivity(i);
            }
        });

        callHistoryBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(DashBoardActivity.this,CallHistory.class);
                startActivity(i);
            }
        });

        customerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+919494444162"));
                startActivity(intent);
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoardActivity.this, ContactsActivity.class);
                i.putExtra("INVOKINGACTIVITY","CONTACTS");
                startActivity(i);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(DashBoardActivity.this,PlaceCallActivity.class);
                startActivity(i);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashBoardActivity.this,DashBoardActivity.class);
               // startActivity(i);
            }
        });

        messaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DashBoardActivity.this,ContactsActivity.class);
                i.putExtra("INVOKINGACTIVITY","MESSAGING");
                startActivity(i);
            }
        });
    }
}
