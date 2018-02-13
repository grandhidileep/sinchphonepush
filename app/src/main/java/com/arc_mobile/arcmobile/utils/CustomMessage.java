package com.arc_mobile.arcmobile.utils;

import com.sinch.android.rtc.messaging.Message;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dgrandhi on 31/01/18.
 */

public class CustomMessage implements Message {
    String senderId,textBody;
    List<String> recipientIds;
    Date timestamp;
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public String getMessageId() {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public String getTextBody() {
        return textBody;
    }

    @Override
    public List<String> getRecipientIds() {
        return recipientIds;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public void setRecipientIds(List<String> recipientIds) {
        this.recipientIds = recipientIds;
    }
}
