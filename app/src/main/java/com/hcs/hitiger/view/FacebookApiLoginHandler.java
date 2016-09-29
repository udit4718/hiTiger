package com.hcs.hitiger.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hcs.hitiger.model.LoginInterface;
import com.hcs.hitiger.model.LoginSourceCode;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by anuj gupta on 4/26/16.
 */
public class FacebookApiLoginHandler {

    private CallbackManager callbackManager;
    private Context context;

//    https://developers.facebook.com/quickstarts/573777689470178/?platform=android

    public FacebookApiLoginHandler(Context context) {
        this.context = context;
        FacebookSdk.sdkInitialize(context.getApplicationContext());
    }

    public void login() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                if (object != null) {
                                    String firstName = object.optString("first_name");
                                    String lastName = object.optString("last_name");
//                                    String gender = object.optString("gender").equals("male") ? "Male" : "Female";
                                    String email = object.optString("email");
                                    String image = object.optJSONObject("picture").optJSONObject("data").optString("url");
                                    String id = object.optString("id");
//                                    LoginManager.getInstance().logOut();
                                    ((LoginInterface) context).doSocialLogin(firstName + " " + lastName, email, image, LoginSourceCode.FACEBOOK, id);
                                } else {
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,gender,last_name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();

                Log.d("tag", "successful");

            }

            @Override
            public void onCancel() {
                Log.d("TAG", "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("TAG", e.getMessage());

            }

        });
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, permissions);
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
