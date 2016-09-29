package com.hcs.hitiger.api.model.pubnub;

import com.hcs.hitiger.HitigerApplication;

/**
 * Created by hot cocoa on 19/05/16.
 */
public class    PubnubEnvelope {

    private String data;
    private int contentType;

    public PubnubEnvelope(String data, int contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    public enum ContentType {
        MESSAGE(1);

        private int value;

        ContentType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return HitigerApplication.getInstance().getGson().toJson(this);
    }

    public String getData() {
        return data;
    }

    public int getContentType() {
        return contentType;
    }
}
