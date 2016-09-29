package com.hcs.hitiger.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hcs.hitiger.activities.LoginActivity;
import com.hcs.hitiger.model.LoginInterface;
import com.hcs.hitiger.model.LoginSourceCode;

/**
 * Created by anuj gupta on 4/26/16.
 */
public class GoogleApiClientHandler implements GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 0;
    private final GoogleApiClient mGoogleApiClient;
    private Context context;

    private String fullName;
    private String email;
    private String image;
    private String personId;

    public GoogleApiClientHandler(Context context) {
        this.context = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((LoginActivity) context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((LoginActivity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount currentPerson = result.getSignInAccount();
            if (currentPerson != null) {
                fullName = currentPerson.getDisplayName();
                if (currentPerson.getPhotoUrl() != null) {
                    image = currentPerson.getPhotoUrl().toString();
                }

                personId = currentPerson.getId();
                email = currentPerson.getEmail();

                ((LoginInterface) context).doSocialLogin(fullName, email, image, LoginSourceCode.GOOGLE, personId);
            }
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("TAG", connectionResult.getErrorMessage());
    }
}

