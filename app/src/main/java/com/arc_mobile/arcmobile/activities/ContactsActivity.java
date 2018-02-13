package com.arc_mobile.arcmobile.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.contactUtils.Contact;
import com.arc_mobile.arcmobile.contactUtils.ContactFetcher;
import com.arc_mobile.arcmobile.contactUtils.ContactsAdapter;

import java.util.ArrayList;

/**
 * Created by dgrandhi on 29/01/18.
 */

public class ContactsActivity extends Activity {
    ArrayList<Contact> listContacts,phoneContacts;
    ListView lvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        listContacts = new ContactFetcher(this).fetchAll();
        phoneContacts =new ArrayList<Contact>();
        for(Contact contact:listContacts){
            if(contact.numbers.size()>0)
                phoneContacts.add(contact);
        }
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        final Intent gI=getIntent();
        final ContactsAdapter adapterContacts = new ContactsAdapter(this, phoneContacts);
        lvContacts.setAdapter(adapterContacts);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i ;
                if(gI.getExtras().getString("INVOKINGACTIVITY").equals("MESSAGING"))
                    i=new Intent(ContactsActivity.this,MessagingActivity.class);
                else
                    i=new Intent(ContactsActivity.this,CallNMessageOptionsActivity.class);
                Contact contact=phoneContacts.get(position);
                i.putExtra("NAME",contact.name);
                if(contact.numbers.size()>0) {
                    String number = contact.numbers.get(0).number;
                    if(number.startsWith("+")){
                        number=number.substring(1,number.length());
                    }
                    else if(number.startsWith("0"))
                        number="91"+number.substring(1,number.length());
                    i.putExtra("NUMBER", PhoneNumberUtils.stripSeparators(number) );
                }
                startActivity(i);

            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
}
