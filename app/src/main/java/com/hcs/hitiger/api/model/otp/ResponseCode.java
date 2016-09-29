package com.hcs.hitiger.api.model.otp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class ResponseCode {
    @SerializedName("responsecode")
    private String responsecode;
    @SerializedName("message")
    private String message;

    public String getResponsecode() {
        return responsecode;
    }

    public String getMessage() {
        return message;
    }
}
