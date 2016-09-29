package com.hcs.hitiger.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.user.UserFullProfile;
import com.hcs.hitiger.api.model.user.UserUniqueIdRequest;
import com.hcs.hitiger.model.UserProfileDetail;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sanyamtyagi on 31/05/16.
 */
public class UserProfileUpdateReceiver extends BroadcastReceiver {

    UserProfileDetail userProfileDetail;
    ApiService apiService;

    @Override
    public void onReceive(Context context, Intent intent) {
        updateProfile();
    }

    private void updateProfile() {
        userProfileDetail = UserProfileDetail.getUserProfileDetail();
        apiService = HitigerApplication.getInstance().getRestClient().getApiService();
        apiService.getUserProfile(new UserUniqueIdRequest(userProfileDetail.getFbId(), userProfileDetail.getId()),
                new Callback<UserFullProfile>() {
                    @Override
                    public void success(UserFullProfile userFullProfile, Response response) {
                        if (userFullProfile.isSuccesfull()) {
                            userProfileDetail = UserProfileDetail.createUser(userFullProfile.getUser());
                            UserProfileDetail.setUserProfileDetail(userProfileDetail);
                        } else
                            HitigerApplication.getInstance().showServerError();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }
}
