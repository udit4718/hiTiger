package com.hcs.hitiger.model;

/**
 * Created by anuj gupta on 4/27/16.
 */
public enum LoginSourceCode {
    GOOGLE("GOOGLE"), FACEBOOK("FB");
    private String mode;

    LoginSourceCode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
