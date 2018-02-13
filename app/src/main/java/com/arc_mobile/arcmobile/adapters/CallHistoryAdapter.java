package com.arc_mobile.arcmobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.arc_mobile.arcmobile.R;
import com.arc_mobile.arcmobile.utils.CallHistoryItem;

import java.util.List;
import java.util.Map;

/**
 * Created by dgrandhi on 31/01/18.
 */

public class CallHistoryAdapter  extends RecyclerView.Adapter<CallHistoryAdapter.CardViewHolder>  {
    private List<CallHistoryItem> historyItems;

    public CallHistoryAdapter(List<CallHistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder contactViewHolder, int i) {
        CallHistoryItem historyItem = historyItems.get(i);
        contactViewHolder.tvPhoneNumber.setText(""+historyItem.getPhone());
        contactViewHolder.tvTime.setText(""+historyItem.getDate());
        contactViewHolder.tvDuration.setText(historyItem.getDuration());
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvPhoneNumber;
        protected TextView tvTime;
        protected TextView tvDuration;

        public CardViewHolder(View v) {
            super(v);
            tvPhoneNumber =  v.findViewById(R.id.tv_card_view_phone_number);
            tvTime=  v.findViewById(R.id.v_card_view_item_time);
            tvDuration = v.findViewById(R.id.tv_card_view_duration);
        }

    }

}
