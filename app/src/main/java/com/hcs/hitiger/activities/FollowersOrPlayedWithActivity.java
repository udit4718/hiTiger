package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.ClubOrFollowersOrPlayedWithAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.follow.FollowersResponse;
import com.hcs.hitiger.api.model.follow.FollowingList;
import com.hcs.hitiger.api.model.follow.FollowingResponse;
import com.hcs.hitiger.api.model.user.PlayedWithResponse;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FollowersOrPlayedWithActivity extends AppCompatActivity implements Progressable {
    public static final String SELECTED_USER = "SELECTED_USER";
    public static final String IS_PLAYED_WITH = "IS_PLAYED_WITH";
    public static final String IS_FOLLOWING = "IS_FOLLOWING";
    public static final String USER_PROFILE = "USER_PROFILE";

    @InjectView(R.id.followers_list)
    ListView followersOrPlayedWithListView;
    @InjectView(R.id.toolbar_title)
    TextView title;
    @InjectView(R.id.empty_view)
    TextView emptyView;

    private AdapterView.OnItemClickListener mFollowersListItemClickLisner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(FollowersOrPlayedWithActivity.this, OthersProfileActivity.class);
            intent.putExtra(SELECTED_USER, mUserProfileDetailList.get(position));
            startActivity(intent);
        }
    };

    @OnClick(R.id.backpress_button)
    public void backPress() {
        onBackPressed();
    }

    private UserProfileDetail mCurrentUserProfileDetail;
    private ClubOrFollowersOrPlayedWithAdapter mFollowersOrPlayedAdapter;
    private List<UserProfileDetail> mUserProfileDetailList;
    private ProgressDialog mProgressDialog;
    private boolean isPlayedWith;
    private boolean isFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_or_played_with);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrentUserProfileDetail = getIntent().getParcelableExtra(USER_PROFILE);

        isPlayedWith = getIntent().getBooleanExtra(IS_PLAYED_WITH, false);
        isFollowing = getIntent().getBooleanExtra(IS_FOLLOWING, false);

        ButterKnife.inject(this);
        setFontes();

        if (isPlayedWith) {
            title.setText(getResources().getString(R.string.played_with));
            loadPlayedWith();
        } else if (isFollowing) {
            title.setText(getResources().getString(R.string.following));
            loadFollowing();
        } else {
            title.setText(getResources().getString(R.string.followers));
            loadFollowers();
        }
    }

    private void loadFollowing() {
        showProgress("", getResources().getString(R.string.loading_following));
        getApiService().getFollowingList(new FollowingList(mCurrentUserProfileDetail.getFbId(),
                mCurrentUserProfileDetail.getId(), HitigerApplication.getInstance().getUserDetail().getId()), new Callback<FollowingResponse>() {
            @Override
            public void success(FollowingResponse followingResponse, Response response) {
                hideProgress();
                if (followingResponse.isSuccesfull()) {
                    setDataInAdapters(followingResponse.getUsers());
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

    private void loadPlayedWith() {
        showProgress("", getResources().getString(R.string.loading_played_with));
        getApiService().getPlayedWith(new FollowingList(mCurrentUserProfileDetail.getFbId(), mCurrentUserProfileDetail.getId()
                , HitigerApplication.getInstance().getUserDetail().getId()), new Callback<PlayedWithResponse>() {
            @Override
            public void success(PlayedWithResponse followersResponse, Response response) {
                hideProgress();
                if (followersResponse.isSuccesfull()) {
                    setDataInAdapters(followersResponse.getUsers());
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

    private void setFontes() {
        title.setTypeface(HitigerApplication.BOLD);
        emptyView.setTypeface(HitigerApplication.SEMI_BOLD);
    }

    private void loadFollowers() {
        showProgress("", getResources().getString(R.string.loading_followers));
        getApiService().getUserFollowersList(new FollowingList(mCurrentUserProfileDetail.getFbId(),
                mCurrentUserProfileDetail.getId(), HitigerApplication.getInstance().getUserDetail().getId()), new Callback<FollowersResponse>() {
            @Override
            public void success(FollowersResponse followersResponse, Response response) {
                hideProgress();
                if (followersResponse.isSuccesfull()) {
                    setDataInAdapters(followersResponse.getFollowers());
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

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    public void setDataInAdapters(List<UserApiResponse> dataInAdapters) {
        if (dataInAdapters != null && dataInAdapters.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            if (isPlayedWith) {
                emptyView.setText(getResources().getString(R.string.no_played_with));
            }
        }
        mUserProfileDetailList = UserProfileDetail.createUserList(dataInAdapters);

        if (mFollowersOrPlayedAdapter == null)
            mFollowersOrPlayedAdapter = new ClubOrFollowersOrPlayedWithAdapter(this, 0);
        followersOrPlayedWithListView.setAdapter(mFollowersOrPlayedAdapter);
        mFollowersOrPlayedAdapter.addingList(mUserProfileDetailList, HitigerApplication.getInstance().getUserDetail());
        mFollowersOrPlayedAdapter.notifyDataSetChanged();
        followersOrPlayedWithListView.setOnItemClickListener(mFollowersListItemClickLisner);


    }
}
