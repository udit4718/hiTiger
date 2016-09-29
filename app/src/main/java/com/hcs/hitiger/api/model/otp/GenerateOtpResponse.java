package com.hcs.hitiger.api.model.otp;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class GenerateOtpResponse extends HitigerResponse {
    @SerializedName("otp")
    private String otp;

    public String getOtp() {
        return otp;
    }
}
