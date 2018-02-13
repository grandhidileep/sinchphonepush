package com.arc_mobile.arcmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.contactUtils.ContactFetcher;
import com.arc_mobile.arcmobile.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgrandhi on 02/02/18.
 */

public class Conversations extends Activity {
    List<String> numbers;
    ListView recyclerView;
    ArrayAdapter adapter;
    ContactFetcher contactFetcher=new ContactFetcher(Conversations.this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url="http://api.arcmobile.vizag.co/get_conversations.php?number=#mynum#";
        numbers=new ArrayList<>();
        recyclerView=findViewById(R.id.rcv_conversations);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,numbers);
        recyclerView.setAdapter(adapter);
        url=url.replace("#mynum#", MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
        StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Conversations.this,MessagingActivity.class);
                intent.putExtra("NUMBER", PhoneNumberUtils.stripSeparators(numbers.get(position)) );
                intent.putExtra("NAME", contactFetcher.getContactName(numbers.get(position)));
                startActivity(intent);
            }
        });
    }

    public void processResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("result");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jObj=jsonArray.getJSONObject(i);
                String num=jObj.getString("conversations");
                String name=contactFetcher.getContactName(num);
                if(name != null)
                    numbers.add(name);
                else
                    numbers.add(num);
            }
            adapter.notifyDataSetChanged();
        }catch (JSONException je){
            Log.e("jsonexcep",je.toString());
        }
    }
}
