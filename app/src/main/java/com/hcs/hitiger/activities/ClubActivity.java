package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.ClubOrFollowersOrPlayedWithAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.user.ClubRequest;
import com.hcs.hitiger.api.model.user.ClubResponse;
import com.hcs.hitiger.model.ClubUiData;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClubActivity extends AppCompatActivity implements Progressable {
    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    public static final String CLUB_DATA = "CLUB_DATA";

    @InjectView(R.id.toolbar_title)
    TextView toolbarTitle;
    @InjectView(R.id.category_title)
    TextView categoryTitle;
    @InjectView(R.id.add_category)
    TextView addCategory;
    @InjectView(R.id.followers_list)
    ListView followersListView;
    @InjectView(R.id.opportunities_text)
    TextView opportunitiesTextView;
    @InjectView(R.id.number_of_members)
    TextView numberOfMembersTextView;
    @InjectView(R.id.club_image)
    ImageView clubImageView;
    @InjectView(R.id.empty_view)
    TextView emptyView;

    private boolean isAdded;
    private ClubOrFollowersOrPlayedWithAdapter mClubAdapter;
    private UserProfileDetail mCurrentUserProfileDetail;
    private ProgressDialog mProgressDialog;
    private ClubUiData clubUiData;
    private List<UserProfileDetail> mUserProfileDetailList;

    private AdapterView.OnItemClickListener userListItemClickLisner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ClubActivity.this, OthersProfileActivity.class);
            intent.putExtra(FollowersOrPlayedWithActivity.SELECTED_USER, mUserProfileDetailList.get(position));
            startActivity(intent);
        }
    };

    @OnClick(R.id.backpress_button)
    public void backPress() {
        onBackPressed();
    }

    @OnClick(R.id.add_category)
    public void changeAddButtonState() {
        if (isAdded) {
            removeUserClub();
        } else
            addUserClub();
    }

    private void addUserClub() {
        showProgress("", getResources().getString(R.string.adding_to_this_club));
        getApiService().addUsersClub(new ClubRequest(mCurrentUserProfileDetail.getId(), clubUiData.getId(), mCurrentUserProfileDetail.getFbId()), new Callback<ClubResponse>() {
            @Override
            public void success(ClubResponse clubResponse, Response response) {
                hideProgress();
                if (clubResponse.isSuccesfull()) {
                    Toast.makeText(ClubActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    loadClubDetails();
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

    private void removeUserClub() {
        showProgress("", getResources().getString(R.string.removing_from_this_club));
        getApiService().removeUsersClub(new ClubRequest(mCurrentUserProfileDetail.getId(), clubUiData.getId(), mCurrentUserProfileDetail.getFbId()), new Callback<ClubResponse>() {
            @Override
            public void success(ClubResponse clubResponse, Response response) {
                hideProgress();
                if (clubResponse.isSuccesfull()) {
                    Toast.makeText(ClubActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    loadClubDetails();
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

    private void addButtonChanges() {
        if (isAdded) {
            addCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            addCategory.setBackgroundDrawable(
                    ContextCompat.getDrawable(this, R.drawable.green_background_with_border));
            addCategory.setText(getResources().getString(R.string.added));
        } else {
            addCategory.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.add), null, null, null);
            addCategory.setCompoundDrawablePadding(10);
            addCategory.setText(getResources().getString(R.string.add));
            addCategory.setBackgroundDrawable(
                    ContextCompat.getDrawable(this, R.drawable.black_background_with_border));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        mCurrentUserProfileDetail = UserProfileDetail.getUserProfileDetail();
        clubUiData = getIntent().getParcelableExtra(CLUB_DATA);
        String categoryName = getIntent().getStringExtra(CATEGORY_NAME);

        ButterKnife.inject(this);
        toolbarTitle.setText("CLUB");
        setStyle();
        setClubImage(categoryName);

        categoryTitle.setText(clubUiData.getName());
        loadClubDetails();
    }

    private void setClubImage(String categoryName) {
        if (categoryName.toLowerCase().equals("city")) {
            clubImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_location));
        } else {
            clubImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_whistle));
        }
    }

    private void loadClubDetails() {
        showProgress("", getResources().getString(R.string.loading_clubs));
        getApiService().getClubDetails(new ClubRequest(mCurrentUserProfileDetail.getId(), clubUiData.getId(), mCurrentUserProfileDetail.getFbId()), new Callback<ClubResponse>() {
            @Override
            public void success(ClubResponse clubResponse, Response response) {
                hideProgress();
                if (clubResponse.isSuccesfull()) {
                    isAdded = clubResponse.isAdded();
                    declareAndSetDataInAdapters(clubResponse);
                    setSpannableAndDrawableData(clubResponse);
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

    private void setSpannableAndDrawableData(ClubResponse clubResponse) {
        addButtonChanges();
        Spannable wordtoSpan = new SpannableString(clubResponse.getOpportunities() + " Opportunities");
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                0, wordtoSpan.length() - 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        opportunitiesTextView.setText(wordtoSpan);
        int membersCount = isAdded ? clubResponse.getUsers().size() + 1 : clubResponse.getUsers().size();
        Spannable wordtoSpan1 = membersCount == 1 ? new SpannableString(membersCount + " Member") : new SpannableString(membersCount + " Members");
        wordtoSpan1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                0, wordtoSpan1.length() - 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numberOfMembersTextView.setText(wordtoSpan1);
    }

    private void declareAndSetDataInAdapters(ClubResponse clubResponse) {
        mUserProfileDetailList = UserProfileDetail.createUserList(clubResponse.getUsers());

        if (mUserProfileDetailList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            if (isAdded) {
                emptyView.setText("You are the only member of this club.");
            } else {
                emptyView.setText(R.string.currently_no_user);
            }
        } else {
            if (mClubAdapter == null)
                mClubAdapter = new ClubOrFollowersOrPlayedWithAdapter(this, 0);
            followersListView.setAdapter(mClubAdapter);
            mClubAdapter.clear();
            mClubAdapter.addingList(mUserProfileDetailList, mCurrentUserProfileDetail);
            mClubAdapter.notifyDataSetChanged();
            followersListView.setOnItemClickListener(userListItemClickLisner);
        }
    }

    private void setStyle() {
        toolbarTitle.setTypeface(HitigerApplication.BOLD);
        categoryTitle.setTypeface(HitigerApplication.SEMI_BOLD);
        addCategory.setTypeface(HitigerApplication.REGULAR);
        opportunitiesTextView.setTypeface(HitigerApplication.REGULAR);
        numberOfMembersTextView.setTypeface(HitigerApplication.REGULAR);
    }
}
