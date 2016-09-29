package com.hcs.hitiger.api.model.otp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class OtpRequest {
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("otp")
    private String otp;

    public OtpRequest(String mobile) {
        this.mobile = mobile;
    }

    public OtpRequest(String mobile, String otp) {
        this.mobile = mobile;
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }
}