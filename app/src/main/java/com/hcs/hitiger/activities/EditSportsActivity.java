package com.hcs.hitiger.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.sports.GetAllSportsResponse;
import com.hcs.hitiger.api.model.sports.UpdateUserSportsRequest;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.DialogUtil;
import com.hcs.hitiger.util.SportsViewHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditSportsActivity extends BaseActivity {
    public static final String IS_CHANGES = "IS_CHANGES";
    private Set<Long> userPreviousSportIds = new HashSet<>();

    @InjectView(R.id.all_sports_container)
    FlexboxLayout allSportsViewContainer;

    @InjectView(R.id.toolbar_title)
    TextView title;
    @InjectView(R.id.select_more_games_label)
    TextView selectMoreGamesLabel;
    private ArrayList<Sport> userSelectedSports;

    @OnClick(R.id.backpress_button)
    public void onBackPress() {
        onBackPressed();
    }

    @OnClick(R.id.done_editing)
    public void onCompleteEditing() {
        if (userSelectedSports.size() != userPreviousSportIds.size() || isNewSportsIdAdded()) {
            showProgressDialog("Updating...");
            List<Long> userSelectedSportsIDs = new ArrayList<>();
            for (Sport sport : userSelectedSports) {
                userSelectedSportsIDs.add(sport.getId());
            }
            UserProfileDetail userDetail = UserProfileDetail.getUserProfileDetail();
            HitigerApplication.getInstance().getRestClient().getApiService().updateUserSportsClub(new UpdateUserSportsRequest(userDetail.getFbId(), userDetail.getId(), userSelectedSportsIDs), new Callback<HitigerResponse>() {
                @Override
                public void success(HitigerResponse hitigerResponse, Response response) {
                    dismissProgressDialog();
                    if (hitigerResponse.isSuccesfull()) {
                        Sport.setUserSelectedSportList(userSelectedSports);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(IS_CHANGES, true);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else
                        HitigerApplication.getInstance().showServerError();
                }

                @Override
                public void failure(RetrofitError error) {
                    HitigerApplication.getInstance().showNetworkErrorMessage();
                    dismissProgressDialog();
                }
            });
        } else {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra(IS_CHANGES, false);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

    }

    private boolean isNewSportsIdAdded() {
        if (userSelectedSports != null)
            for (Sport sport : userSelectedSports) {
                if (!userPreviousSportIds.contains(sport.getId())) {
                    return true;
                }
            }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sports);
        ButterKnife.inject(this);
        setTextViewTypeFaces();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title.setText("SPORTS");
        UserProfileDetail userProfileDetail = UserProfileDetail.getUserProfileDetail();
        showProgressDialog("Loading Sports...");
        HitigerApplication.getInstance().getRestClient().getApiService().getAllSports(new HitigerRequest(userProfileDetail.getFbId(),
                userProfileDetail.getId()), new Callback<GetAllSportsResponse>() {
            @Override
            public void success(GetAllSportsResponse getAllSportsResponse, Response response) {
                if (getAllSportsResponse.isSuccesfull()) {
                    createUiForSportsSelection(getAllSportsResponse.getSportList());
                } else
                    HitigerApplication.getInstance().showServerError();
                dismissProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                dismissProgressDialog();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void createUiForSportsSelection(List<Sport> allSportList) {

        userSelectedSports = new ArrayList<>();
        for (Sport sport : allSportList) {
            if (sport.isAdded()) {
                userSelectedSports.add(sport);
                userPreviousSportIds.add(sport.getId());
            }
        }
        addSportsViewToViewGroup(allSportList, allSportsViewContainer);
    }

    @Override
    public void onBackPressed() {
        if (userSelectedSports != null && userSelectedSports.size() != userPreviousSportIds.size() || isNewSportsIdAdded()) {
            DialogUtil.showConfirmationDialog(this, "", "Are you sure to discard changes?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditSportsActivity.super.onBackPressed();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    private void addSportsViewToViewGroup(List<Sport> sportsList, FlexboxLayout sportsViewContainer) {
        sportsViewContainer.removeAllViews();
        if (sportsList.size() == 0) {
            sportsViewContainer.setVisibility(View.GONE);
            return;
        } else {
            sportsViewContainer.setVisibility(View.VISIBLE);
        }
        for (final Sport sport : sportsList) {
            TextView textView = new TextView(this);
            textView.setText(sport.getName());
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            SportsViewHelper.setStyleToSporstTetView(textView, params, sport);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sport selectedSport = (Sport) v.getTag();
                    if (v.isSelected()) {
                        v.setSelected(false);
                        selectedSport.setAdded(false);
                        userSelectedSports.remove(selectedSport);
                    } else {
                        v.setSelected(true);
                        selectedSport.setAdded(true);
                        userSelectedSports.add(selectedSport);
                    }
                }
            });
            textView.setTag(sport);
            sportsViewContainer.addView(textView);
        }
    }

    @Override
    protected void setTextViewTypeFaces() {
        title.setTypeface(HitigerApplication.BOLD);
        selectMoreGamesLabel.setTypeface(HitigerApplication.REGULAR);
    }
}
