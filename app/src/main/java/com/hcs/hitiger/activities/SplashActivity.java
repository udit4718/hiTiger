package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.sports.GetAllSportsResponse;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashActivity extends AppCompatActivity implements Progressable {
    private UserProfileDetail userProfileDetail;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userProfileDetail = UserProfileDetail.getUserProfileDetail();

        if (userProfileDetail == null) {
            new CountDownTimer(0, 0) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    nextActivity();
                }
            }.start();
        } else {
            getSportList();
        }
    }

    @Override
    public void showProgress(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(this, title, message, true);
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void getSportList() {
        showProgress("", getResources().getString(R.string.loading_sports));
        HitigerApplication.getInstance().getRestClient().getApiService().getAllSports(new HitigerRequest(userProfileDetail.getFbId(),
                userProfileDetail.getId()), new Callback<GetAllSportsResponse>() {
            @Override
            public void success(GetAllSportsResponse getAllSportsResponse, Response response) {
                hideProgress();
                if (getAllSportsResponse.isSuccesfull())
                    Sport.setSportList(getAllSportsResponse.getSportList());
                else
                    HitigerApplication.getInstance().showServerError();
                nextActivity();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
                nextActivity();
            }
        });
    }

    private void nextActivity() {
        if (userProfileDetail != null) {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
