package com.arc_mobile.arcmobile.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.adapters.CallHistoryAdapter;
import com.arc_mobile.arcmobile.utils.CallHistoryItem;
import com.arc_mobile.arcmobile.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dgrandhi on 30/01/18.
 */

public class CallHistory extends Activity {
    List<CallHistoryItem> itemList;
    CallHistoryAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        recyclerView = findViewById(R.id.rcv_call_history);
        itemList = new ArrayList<CallHistoryItem>();
        adapter = new CallHistoryAdapter(itemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CallHistory.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        String url = "http://api.arcmobile.vizag.co/4.php?userid=#UID#";
        url = url.replace("#UID#", MyApplication.getInstance().getPreferenceManager().getUser().getUserMobile());
        Log.e("url", url);

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public void processResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                CallHistoryItem item = new CallHistoryItem();
                String tonumber = jObj.getString("tonumber");
                if (tonumber != null && !tonumber.trim().equals("")) {
                    Log.e("tonum",tonumber);
                    item.setPhone(tonumber);
                    Date date = new Date(new Long(jObj.getString("starttime")));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
                    item.setDate(simpleDateFormat.format(date));
                    item.setDuration(jObj.getString("duration"));
                    itemList.add(item);
                }
            }
            Log.e("adapter", "" + adapter.getItemCount());
            adapter.notifyDataSetChanged();
        } catch (JSONException je) {
            Log.e("err", je.toString());
        }

    }
}
