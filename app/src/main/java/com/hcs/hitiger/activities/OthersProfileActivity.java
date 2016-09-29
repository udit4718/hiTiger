package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.NonScrollListView;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.ListOfCommonAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.follow.FollowRequest;
import com.hcs.hitiger.api.model.follow.UnFollowRequest;
import com.hcs.hitiger.api.model.user.AnotherUserProfileRequest;
import com.hcs.hitiger.api.model.user.AnotherUserProfileResponse;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.model.ClubUiData;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.SportsViewHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OthersProfileActivity extends AppCompatActivity implements Progressable {
    public static final String SELECTED_USER_ID = "SELECTED_USER_ID";
    public static final String SELECTED_USER_NAME = "SELECTED_USER_NAME";

    @InjectView(R.id.user_name)
    TextView userNameTextView;
    @InjectView(R.id.user_email)
    TextView userEmailTextView;
    @InjectView(R.id.user_contact_detail_layout)
    RelativeLayout contactDetailLayout;
    @InjectView(R.id.profile_image)
    ImageView profileImageView;
    @InjectView(R.id.follow_or_following)
    TextView followOrFollowingTextView;
    @InjectView(R.id.edit_person)
    TextView editPerson;
    @InjectView(R.id.played_with_count)
    TextView playedWithCount;
    @InjectView(R.id.followers_count_text)
    TextView followersCountTextView;
    @InjectView(R.id.following_count_text)
    TextView followingCountTextView;
    @InjectView(R.id.you_and_player_text)
    TextView youAndPlayerTextView;
    @InjectView(R.id.common_clubs)
    NonScrollListView commonClubsListView;
    @InjectView(R.id.more_clubs)
    NonScrollListView moreClubsListView;
    @InjectView(R.id.more_layout)
    RelativeLayout moreClubLayout;
    @InjectView(R.id.common_layout)
    RelativeLayout commonClubLayout;
    @InjectView(R.id.all_selected_sports_container)
    FlexboxLayout allSelectedSportsContainer;

    private UserProfileDetail anotherUserProfile;
    private UserProfileDetail mCurrentUserProfileDetail;
    private ProgressDialog mProgressDialog;
    private boolean isFollowing;

    @OnClick(R.id.follow_or_following)
    public void setFollowOrFollowingTextView() {
        callForFollowOrFollowingChanges();
    }

    @OnClick(R.id.following_text)
    public void showFollowing() {
        Intent intent = new Intent(this, FollowersOrPlayedWithActivity.class);
        intent.putExtra(FollowersOrPlayedWithActivity.USER_PROFILE, anotherUserProfile);
        intent.putExtra(FollowersOrPlayedWithActivity.IS_FOLLOWING, true);
        startActivity(intent);
    }

    @OnClick(R.id.following_count_text)
    public void following() {
        showFollowing();
    }

    @OnClick(R.id.played_with_text)
    public void showPlayedWith() {
        Intent intent = new Intent(this, FollowersOrPlayedWithActivity.class);
        intent.putExtra(FollowersOrPlayedWithActivity.USER_PROFILE, anotherUserProfile);
        intent.putExtra(FollowersOrPlayedWithActivity.IS_PLAYED_WITH, true);
        startActivity(intent);
    }

    @OnClick(R.id.played_with_count)
    public void playWith() {
        showPlayedWith();
    }

    @OnClick(R.id.followers_text)
    public void showFollowers() {
        Intent intent = new Intent(this, FollowersOrPlayedWithActivity.class);
        intent.putExtra(FollowersOrPlayedWithActivity.USER_PROFILE, anotherUserProfile);
        startActivity(intent);
    }

    @OnClick(R.id.followers_count_text)
    public void followersActivity() {
        showFollowers();
    }


    private void callForFollowOrFollowingChanges() {
        if (isFollowing) {
            showProgress("", "Unfollowing...");
            getApiService().unfollowUser(new UnFollowRequest(mCurrentUserProfileDetail.getFbId(), mCurrentUserProfileDetail.getId(),
                    anotherUserProfile.getId()), new Callback<HitigerResponse>() {
                @Override
                public void success(HitigerResponse hitigerResponse, Response response) {
                    hideProgress();
                    if (hitigerResponse.isSuccesfull()) {
                        getAnotherUserProfile(anotherUserProfile.getName(), anotherUserProfile.getId());
                    } else
                        HitigerApplication.getInstance().showServerError();
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    HitigerApplication.getInstance().showNetworkErrorMessage();
                }
            });
        } else {
            showProgress("", "Following...");
            getApiService().follow(new FollowRequest(mCurrentUserProfileDetail.getFbId(), mCurrentUserProfileDetail.getId(),
                    anotherUserProfile.getId()), new Callback<HitigerResponse>() {
                @Override
                public void success(HitigerResponse hitigerResponse, Response response) {
                    hideProgress();
                    if (hitigerResponse.isSuccesfull()) {
                        getAnotherUserProfile(anotherUserProfile.getName(), anotherUserProfile.getId());
                    } else
                        HitigerApplication.getInstance().showServerError();
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    HitigerApplication.getInstance().showNetworkErrorMessage();
                }
            });
        }
    }

    private void setChangesForFollowButton() {
        if (mCurrentUserProfileDetail.getId() == anotherUserProfile.getId()) {
            followOrFollowingTextView.setVisibility(View.GONE);
        } else {
            followOrFollowingTextView.setVisibility(View.VISIBLE);
            if (isFollowing) {
                followOrFollowingTextView.setTypeface(HitigerApplication.SEMI_BOLD);
                followOrFollowingTextView.setBackgroundDrawable(
                        ContextCompat.getDrawable(this, R.drawable.yellow_rounded_rectangular_background));
                followOrFollowingTextView.setText(getResources().getString(R.string.following));
                followOrFollowingTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else {
                followOrFollowingTextView.setBackgroundDrawable(
                        ContextCompat.getDrawable(this, R.drawable.white_background_with_rounded_rectangular_border));
                followOrFollowingTextView.setTypeface(HitigerApplication.REGULAR);
                followOrFollowingTextView.setText(getResources().getString(R.string.follow));
                followOrFollowingTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this, R.drawable.create_add), null, null, null);
                followOrFollowingTextView.setCompoundDrawablePadding(25);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String otherUserName;
        long otherUserId;
        mCurrentUserProfileDetail = UserProfileDetail.getUserProfileDetail();
        anotherUserProfile = getIntent().getParcelableExtra(FollowersOrPlayedWithActivity.SELECTED_USER);

        ButterKnife.inject(this);
        if (anotherUserProfile == null) {
            otherUserName = getIntent().getStringExtra(SELECTED_USER_NAME);
            otherUserId = Long.parseLong(getIntent().getStringExtra(SELECTED_USER_ID));
        } else {
            otherUserName = anotherUserProfile.getName();
            otherUserId = anotherUserProfile.getId();
            initializeData();
        }

        setFontsAndVisibility();
        getAnotherUserProfile(otherUserName, otherUserId);
    }

    private void initializeData() {
        userEmailTextView.setText(anotherUserProfile.getEmail());
        userNameTextView.setText(anotherUserProfile.getName());
        playedWithCount.setText(anotherUserProfile.getGamesPlayed() + "");
        followersCountTextView.setText(anotherUserProfile.getFollowersCount() + "");
        followingCountTextView.setText(anotherUserProfile.getFollowingCount() + "");
        youAndPlayerTextView.setText("You & " + anotherUserProfile.getName().split(" ")[0]);

        if (anotherUserProfile.getImageUrl() != null)
            Picasso.with(this).load(anotherUserProfile.getImageUrl()).into(profileImageView);
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

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private void getAnotherUserProfile(String otherUserName, long otherUserId) {
        showProgress("", "Loading " + otherUserName + "'s Profile...");
        getApiService().getAnotherUserProfile(new AnotherUserProfileRequest(mCurrentUserProfileDetail.getFbId(),
                mCurrentUserProfileDetail.getId(), otherUserId), new Callback<AnotherUserProfileResponse>() {
            @Override
            public void success(AnotherUserProfileResponse userFullProfile, Response response) {
                hideProgress();
                if (userFullProfile.isSuccesfull())
                    setDataInList(userFullProfile);
                else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void setDataInList(AnotherUserProfileResponse userFullProfile) {
        anotherUserProfile = UserProfileDetail.createUser(userFullProfile.getUser());
        isFollowing = userFullProfile.isFollowing();
        setChangesForFollowButton();
        initializeData();
        if (userFullProfile.getMore().size() == 0) {
            moreClubLayout.setVisibility(View.GONE);
        } else {
            List<ClubUiData> moreDataList = ClubUiData.getList(userFullProfile.getMore());
            ListOfCommonAdapter listOfCategoryAdapter = new ListOfCommonAdapter(this, 0, moreDataList);
            moreClubsListView.setAdapter(listOfCategoryAdapter);
        }

        if (userFullProfile.getCommonClubs().size() == 0) {
            commonClubLayout.setVisibility(View.GONE);
        } else {
            List<ClubUiData> commonClubs = ClubUiData.getList(userFullProfile.getCommonClubs());
            ListOfCommonAdapter listOfCategoryAdapter = new ListOfCommonAdapter(this, 0, commonClubs);
            commonClubsListView.setAdapter(listOfCategoryAdapter);
        }

        allSelectedSportsContainer.removeAllViews();
        for (final Sport sport : userFullProfile.getSports()) {
            TextView textView = new TextView(this);
            textView.setText(sport.getName());
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            SportsViewHelper.setStyleToSporstTetView(textView, params, sport);
            textView.setTag(sport);
            allSelectedSportsContainer.addView(textView);
        }
    }

    private void setFontsAndVisibility() {
        userNameTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        userEmailTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        playedWithCount.setTypeface(HitigerApplication.SEMI_BOLD);
        followersCountTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        followingCountTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        youAndPlayerTextView.setTypeface(HitigerApplication.REGULAR);

        contactDetailLayout.setVisibility(View.GONE);
        editPerson.setVisibility(View.GONE);
    }
}
