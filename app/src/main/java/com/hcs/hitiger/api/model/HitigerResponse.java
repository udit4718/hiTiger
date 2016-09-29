package com.hcs.hitiger.api.model;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.otp.ResponseCode;

public class HitigerResponse {
    @SerializedName("responseCode")
    private ResponseCode responseCode;

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public boolean isSuccesfull() {
        if (responseCode != null && "200".equals(responseCode.getResponsecode())) {
            return true;
        }
        return false;
    }
}
