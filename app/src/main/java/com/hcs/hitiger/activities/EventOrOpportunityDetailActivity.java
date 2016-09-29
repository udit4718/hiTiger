package com.hcs.hitiger.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.NonScrollListView;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.RequestersAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.opportunity.CancelRequestToPlayRequest;
import com.hcs.hitiger.api.model.opportunity.DeclineEventRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailResponse;
import com.hcs.hitiger.api.model.opportunity.SendRequestToPlayRequest;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventOrOpportunityDetailActivity extends AppCompatActivity implements Progressable {
    public static final String OPPORTUNITY_ID = "OPPORTUNITY_ID";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1221;

    @InjectView(R.id.game_name)
    TextView sportsNameTextView;
    @InjectView(R.id.game_image)
    ImageView sportsImageView;
    @InjectView(R.id.game_timing)
    TextView gameTimingTextView;
    @InjectView(R.id.game_date)
    TextView gameDateTextView;
    @InjectView(R.id.location)
    TextView locationTextView;
    @InjectView(R.id.game_amount)
    TextView gameAmountTextView;
    @InjectView(R.id.list_of_instructions)
    LinearLayout listOfAdditionalString;
    @InjectView(R.id.hosted_by)
    TextView hostedByTextView;
    @InjectView(R.id.host_image)
    ImageView hostImageView;
    @InjectView(R.id.user_name)
    TextView userNameTextView;
    @InjectView(R.id.number_of_followers)
    TextView numberOfFollowersView;
    @InjectView(R.id.user_profile_image)
    ImageView userProfileImageView;
    @InjectView(R.id.number_of_game_played_by_user)
    TextView numberOfGamePlayedTextView;
    @InjectView(R.id.lets_play_button)
    TextView letsPlayButton;
    @InjectView(R.id.requesting_layout)
    RelativeLayout requestingLayout;
    @InjectView(R.id.cancel_request)
    TextView cancelRequest;
    @InjectView(R.id.game_instructions_layout)
    LinearLayout gamedataListLayout;
    @InjectView(R.id.message_image)
    ImageView messageImageView;
    @InjectView(R.id.decline_button)
    TextView declineButton;
    @InjectView(R.id.call_button)
    TextView callButton;
    @InjectView(R.id.call_layout)
    LinearLayout callLayout;
    @InjectView(R.id.requesters_list)
    NonScrollListView requesterListView;
    @InjectView(R.id.requesters_header)
    LinearLayout requestersHEaderLayout;
    @InjectView(R.id.hosted_by_layout)
    RelativeLayout hostedByLayout;
    @InjectView(R.id.hosted_by_header_layout)
    LinearLayout hostedByHeaderLayout;
    @InjectView(R.id.empty_view)
    TextView emptyView;
    @InjectView(R.id.next_button)
    ImageView nextButton;
    @InjectView(R.id.menu)
    ImageView menuButton;
    @InjectView(R.id.background_layout)
    LinearLayout backgroundLayout;
    @InjectView(R.id.toolbar_layout)
    RelativeLayout toolbar_layout;
    @InjectView(R.id.scrollViewContainer)
    ScrollView mScrollView;

    private RequestersAdapter mRequestersAdapter;

    @OnClick(R.id.hosted_by_layout)
    public void clickHostedUser() {
        Intent intent = new Intent(this, OthersProfileActivity.class);
        if (mOpportunity.getVersus() != null)
            intent.putExtra(FollowersOrPlayedWithActivity.SELECTED_USER, UserProfileDetail.createUser(mOpportunity.getVersus()));
        else
            intent.putExtra(FollowersOrPlayedWithActivity.SELECTED_USER, UserProfileDetail.createUser(mOpportunity.getUser()));
        startActivity(intent);
    }

    @OnClick(R.id.call_button)
    public void onCallClick() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + mOpportunity.getVersus().getMobile()));
            startActivity(callIntent);
        }
    }

    @OnClick(R.id.decline_button)
    public void onDeclineClick() {
        showProgress("", getResources().getString(R.string.cancelling_request));
        getApiService().declineEventRequest(new DeclineEventRequest(mUserProfileDetail.getFbId()
                , mUserProfileDetail.getId(), mOpportunity.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    Toast.makeText(EventOrOpportunityDetailActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    onResume();
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    @OnClick(R.id.message_image)
    public void openChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.OPPORTUNITY, mOpportunity);
        if (mOpportunity.getVersus() != null) {
            intent.putExtra(ChatActivity.RECEIPENT_USER_ID, mOpportunity.getVersus().getId());
            intent.putExtra(ChatActivity.RECEIPENT_USER_NAME, mOpportunity.getVersus().getName());
            intent.putExtra(ChatActivity.RECEIPENT_USER_IMAGEURL, mOpportunity.getVersus().getImageUrl());
        } else {
            intent.putExtra(ChatActivity.RECEIPENT_USER_ID, mOpportunity.getUser().getId());
            intent.putExtra(ChatActivity.RECEIPENT_USER_NAME, mOpportunity.getUser().getName());
            intent.putExtra(ChatActivity.RECEIPENT_USER_IMAGEURL, mOpportunity.getUser().getImageUrl());
        }
        startActivity(intent);
    }

    private OpportunityUiData mOpportunity;
    private UserProfileDetail mUserProfileDetail;
    private ProgressDialog mProgressDialog;
    private long opportunityId;

    @OnClick(R.id.lets_play_button)
    public void sendRequest() {
        showProgress("", getResources().getString(R.string.sending_request));
        getApiService().sendRequest(new SendRequestToPlayRequest(mUserProfileDetail.getFbId(), mOpportunity.getId(),
                mUserProfileDetail.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    showConfermationDialog();
                    enablingCancelRequest();
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

    private void enablingCancelRequest() {
        requestingLayout.setVisibility(View.VISIBLE);
        letsPlayButton.setVisibility(View.GONE);
    }

    private void showConfermationDialog() {
        ConfirmOpportunityCustomDialog confirmOpportunityCustomDialog = new ConfirmOpportunityCustomDialog(
                this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelRequest();
                dialog.dismiss();
            }
        }, mOpportunity, null);
        confirmOpportunityCustomDialog.show();
    }

    @OnClick(R.id.cancel_request)
    public void cancelRequest() {
        showProgress("", getResources().getString(R.string.cancelling_request));
        getApiService().cancelRequest(new CancelRequestToPlayRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId(), mOpportunity.getId(),
                mUserProfileDetail.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    enablingLetsPlay();
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

    private void enablingLetsPlay() {
        requestingLayout.setVisibility(View.GONE);
        letsPlayButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.back_button)
    public void backPress() {
        onBackPressed();
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
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserProfileDetail = UserProfileDetail.getUserProfileDetail();
        opportunityId = getIntent().getLongExtra(OPPORTUNITY_ID, 1);
        ButterKnife.inject(this);
        setFonts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOppDetails();
    }

    private void getOppDetails() {
        if (opportunityId == mUserProfileDetail.getId())
            showProgress("", getResources().getString(R.string.loading_event_detail));
        else
            showProgress("", getResources().getString(R.string.loading_opportunities_detail));
        getApiService().getOppDetails(new GetOpportunityDetailRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId(),
                opportunityId), new Callback<GetOpportunityDetailResponse>() {
            @Override
            public void success(GetOpportunityDetailResponse opportunityApiResponse, Response response) {
                hideProgress();
                if (opportunityApiResponse.isSuccesfull()) {
                    mOpportunity = OpportunityUiData.getOpportunityOrEventData(opportunityApiResponse.getOpportunity());
                    setOpportunityData();
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_UP);
                        }
                    });
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

    private void setOpportunityData() {
        if (mRequestersAdapter != null) {
            mRequestersAdapter.clear();
            mRequestersAdapter.notifyDataSetChanged();
        }
        hideViews();
        if (mOpportunity.getCreatorId() == mUserProfileDetail.getId() || mOpportunity.getVersus() != null) {
            menuButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }

        if (mOpportunity.getUser() != null) {
            userNameTextView.setText(mOpportunity.getUser().getName());

            Spannable gamePlayedSpan = new SpannableString(mOpportunity.getUser().getGamesPlayed() + " Game Played");
            gamePlayedSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                    0, gamePlayedSpan.length() - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            numberOfGamePlayedTextView.setText(gamePlayedSpan);

            if (mOpportunity.getUser().getImageUrl() != null) {
                Picasso.with(this).load(mOpportunity.getUser().getImageUrl())
                        .into(userProfileImageView);
            } else {
                userProfileImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile_default_image));
            }
        }

        if (mOpportunity.getPrice() != 0) {
            gameAmountTextView.setText(mOpportunity.getPrice() + " / Player");
        } else {
            gameAmountTextView.setText(getResources().getString(R.string.free));
        }

        if (mOpportunity.getClub() != null) {
            sportsNameTextView.setText(mOpportunity.getClub().getName());
            Picasso.with(this).load(mOpportunity.getClub().getImageUrl()).into(sportsImageView);
            toolbar_layout.setBackgroundColor(Color.parseColor(mOpportunity.getClub().getColor()));
            backgroundLayout.setBackgroundColor(Color.parseColor(mOpportunity.getClub().getColor()));
        }

        if (mOpportunity.getVenue() != null) {
            locationTextView.setText(mOpportunity.getVenue().getAddress());
        }

        if (mOpportunity.getInstructions().size() > 0) {
            listOfAdditionalString.removeAllViews();
            for (int i = 0; i < mOpportunity.getInstructions().size(); i++) {
                TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.white_text_view, listOfAdditionalString, false);
                view.setTypeface(HitigerApplication.LIGHT);
                view.setText(mOpportunity.getInstructions().get(i) + "this is " + i);
                listOfAdditionalString.addView(view);
            }
        } else {
            gamedataListLayout.setVisibility(View.GONE);
        }
        if (mOpportunity.getTime() != -1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
            String timeInString = timeFormat.format(mOpportunity.getTime()).replace("a.m.", "AM").replace("p.m.", "PM");
            gameTimingTextView.setText(timeInString);
        } else {
            gameTimingTextView.setText(getResources().getString(R.string.all_day));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM");
        String date = dateFormat.format(mOpportunity.getDate());
        gameDateTextView.setText(date);

        if (mOpportunity.getCreatorId() == mUserProfileDetail.getId()) {
            requestersHEaderLayout.setVisibility(View.GONE);
            requesterListView.setVisibility(View.GONE);

            if (mOpportunity.getRequesters() != null && mOpportunity.getRequesters().size() > 0) {
                emptyView.setVisibility(View.GONE);
                requestersHEaderLayout.setVisibility(View.VISIBLE);
                requesterListView.setVisibility(View.VISIBLE);
//                mRequestersAdapter = new RequestersAdapter(this, 0, mOpportunity.getRequesters(), mOpportunity);
                requesterListView.setAdapter(mRequestersAdapter);
            } else if (mOpportunity.getVersus() == null && mOpportunity.getRequesters() == null) {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            setHostedByChanges();
            if (mOpportunity.isReqPending()) {
                requestingLayout.setVisibility(View.VISIBLE);
                letsPlayButton.setVisibility(View.GONE);
            } else {
                letsPlayButton.setVisibility(View.VISIBLE);
                requestingLayout.setVisibility(View.GONE);
            }
        }

        messageImageView.setVisibility(View.VISIBLE);

        if (mOpportunity.getVersus() != null) {
            callLayout.setVisibility(View.VISIBLE);
            messageImageView.setVisibility(View.VISIBLE);

            requestingLayout.setVisibility(View.GONE);
            letsPlayButton.setVisibility(View.GONE);

            matchFixWithChanges();

            userNameTextView.setText(mOpportunity.getVersus().getName());
            Spannable followersSpan = new SpannableString(mOpportunity.getVersus().getFollowersCount() + " Followers");
            followersSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                    0, followersSpan.length() - 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            numberOfFollowersView.setText(followersSpan);
            numberOfFollowersView.setVisibility(View.VISIBLE);
            Spannable gamePlayedSpan = new SpannableString(mOpportunity.getVersus().getGamesPlayed() + " Game Played");
            gamePlayedSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                    0, gamePlayedSpan.length() - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            numberOfGamePlayedTextView.setText(gamePlayedSpan);

            if (mOpportunity.getVersus().getImageUrl() != null) {
                Picasso.with(this).load(mOpportunity.getVersus().getImageUrl())
                        .into(userProfileImageView);
            } else {
                userProfileImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile_default_image));
            }
        } else {
            callLayout.setVisibility(View.GONE);
            messageImageView.setVisibility(View.GONE);
            hostedByLayout.setVisibility(View.GONE);
            hostedByHeaderLayout.setVisibility(View.GONE);
//            requestingLayout.setVisibility(View.VISIBLE);
//            letsPlayButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:0" + mOpportunity.getVersus().getMobile()));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                            ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        return;
                    }
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_call_permission), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void setHostedByChanges() {
        hostedByLayout.setVisibility(View.VISIBLE);
        hostedByHeaderLayout.setVisibility(View.VISIBLE);
        hostedByTextView.setText(getResources().getString(R.string.hosted_by));
        hostImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hosted_by));
    }

    private void matchFixWithChanges() {
        hostedByLayout.setVisibility(View.VISIBLE);
        hostedByHeaderLayout.setVisibility(View.VISIBLE);
        hostedByTextView.setText(getResources().getString(R.string.match_fixed_with));
        hostImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.match_fix_with));
    }


    private void setFonts() {
        sportsNameTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        gameTimingTextView.setTypeface(HitigerApplication.REGULAR);
        gameDateTextView.setTypeface(HitigerApplication.REGULAR);
        locationTextView.setTypeface(HitigerApplication.REGULAR);
        gameAmountTextView.setTypeface(HitigerApplication.REGULAR);
        hostedByTextView.setTypeface(HitigerApplication.REGULAR);
        userNameTextView.setTypeface(HitigerApplication.REGULAR);
        numberOfGamePlayedTextView.setTypeface(HitigerApplication.REGULAR);
        letsPlayButton.setTypeface(HitigerApplication.SEMI_BOLD);
        cancelRequest.setTypeface(HitigerApplication.REGULAR);
        declineButton.setTypeface(HitigerApplication.REGULAR);
        callButton.setTypeface(HitigerApplication.REGULAR);
    }

    private void hideViews() {
        requestingLayout.setVisibility(View.GONE);
        requestersHEaderLayout.setVisibility(View.GONE);
        requesterListView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        letsPlayButton.setVisibility(View.GONE);
    }
}
