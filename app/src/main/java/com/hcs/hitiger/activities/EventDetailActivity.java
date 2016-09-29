package com.hcs.hitiger.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.hcs.hitiger.api.model.opportunity.CloseEvent;
import com.hcs.hitiger.api.model.opportunity.DeclineEventRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailResponse;
import com.hcs.hitiger.api.model.opportunity.SendRequestToPlayRequest;
import com.hcs.hitiger.model.AddressData;
import com.hcs.hitiger.model.Opportunity;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.NotificationHelper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import com.hcs.hitiger.view.customdialogs;

/**
 * Created by ritikrishu on 03/06/16.
 */
public class EventDetailActivity extends AppCompatActivity implements Progressable {
    public static final String OPPORTUNITY_ID = "OPPORTUNITY_ID";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1221;

    @InjectView(R.id.game_name)
    TextView sportsNameTextView;
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
    @InjectView(R.id.game_image)
    ImageView sportsImageView;
    @InjectView(R.id.background_layout)
    LinearLayout backgroundLayout;
    @InjectView(R.id.toolbar_layout)
    RelativeLayout toolbar_layout;
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
    @InjectView(R.id.scrollViewContainer)
    ScrollView mScrollView;
    @InjectView(R.id.tvEventLifeCycle)
    TextView mEventLifeCycle;
    @InjectView(R.id.main_layout)
    LinearLayout layout;


    private OpportunityUiData mOpportunity;
    private UserProfileDetail mUserProfileDetail;
    private ProgressDialog mProgressDialog;
    private long opportunityId;
    private RequestersAdapter mRequestersAdapter;
    private Menu menu;
    private boolean isActivityInForeground;
    private boolean shouldReload = false;


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
        getOppDetails();
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
                    if (mRequestersAdapter != null) {
                        mRequestersAdapter.setOpportunity(mOpportunity);
                    }
                    setUI();
//                    mScrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mScrollView.fullScroll(View.FOCUS_UP);
//                        }
//                    });
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

    private void setUI() {
        hideViews();
        setGameDataUI();
        setBottomUI();
        showMenuToCreator();
//        setMatchFixedView();
//        setPlayersUI();
//        decideHostOrPlayerView();

    }

    private void setBottomUI() {

        if (mOpportunity.getCreatorId() == mUserProfileDetail.getId()) {
            selfWatching();
        } else {
            otherUserWatching();
        }

    }


    //2.1 Opportunity Creator is Watching
    private void selfWatching() {
        if (mOpportunity.isDeleted()) {
            closeEventLayout();
        } else {
            mEventLifeCycle.setText("Event");
            if (mOpportunity.getVersus() != null) {
                matchFixed();
            } else {
                matchNotFixed();
            }
        }
    }

    //2.1.1 Match Fixed with someone
    private void matchFixed() {
        callLayout.setVisibility(View.VISIBLE);
        messageImageView.setVisibility(View.VISIBLE);
        mEventLifeCycle.setText("Event Fixed");
//        requestingLayout.setVisibility(View.GONE);
//        letsPlayButton.setVisibility(View.GONE);

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


        if (mOpportunity.getRequesters() != null && mOpportunity.getRequesters().size() > 0) {
            listOfRequesters();
        } else {
            noMoreRequesters();
        }
    }

    //2.1.2 Match not fixed yet
    private void matchNotFixed() {
        callLayout.setVisibility(View.GONE);
        messageImageView.setVisibility(View.GONE);
        hostedByLayout.setVisibility(View.GONE);
        hostedByHeaderLayout.setVisibility(View.GONE);
//            requestingLayout.setVisibility(View.VISIBLE);
//            letsPlayButton.setVisibility(View.VISIBLE);


        if (mOpportunity.getRequesters() != null && mOpportunity.getRequesters().size() > 0) {
            listOfRequesters();
        } else {
            noMoreRequesters();
        }
    }


    //if more requests are available
    private void listOfRequesters() {
        requestersHEaderLayout.setVisibility(View.VISIBLE);
        requesterListView.setVisibility(View.VISIBLE);
        if (mRequestersAdapter == null) {
            mRequestersAdapter = new RequestersAdapter(this, 0, mOpportunity);
            requesterListView.setAdapter(mRequestersAdapter);
        }
        mRequestersAdapter.setUsers(mOpportunity.getRequesters());
    }


    //if no more requests are available
    private void noMoreRequesters() {
        requestersHEaderLayout.setVisibility(View.GONE);
        requesterListView.setVisibility(View.GONE);
    }

    //2.2 other user is watching
    private void otherUserWatching() {
        setHostedByChanges();
        setPlayersUI();
        //if user have requested and request has not been accepted yet
        if (mOpportunity.isReqPending() && mOpportunity.getAcceptorId() == 0) {
            mEventLifeCycle.setText("Event");
            opportunityNotAccepted();
            Log.d("TAG", "otherUserWatching() returned: " + "user have requested and request has not been accepted yet");
        }
        //if user has requested and request have been accepted to some other user
        else if ((mOpportunity.isReqPending() && mOpportunity.getAcceptorId() != mUserProfileDetail.getId()) || mOpportunity.isDeleted()) {
            closeEventLayout();
        } else {
            //if user has requested and request have been accepted
            if (mOpportunity.getAcceptorId() == mUserProfileDetail.getId()) {
                mEventLifeCycle.setText("Event Fixed");
                opportunityAccepted();
                Log.d("TAG", "otherUserWatching() returned: " + "user have requested and request have been accepted");
            }
            //if user have not requested at all
            else {
                mEventLifeCycle.setText("Event");
                letsPlayButton.setVisibility(View.VISIBLE);
                requestingLayout.setVisibility(View.GONE);
                Log.d("TAG", "otherUserWatching() returned: " + "user have not requested at all");
            }
        }
    }

    private void opportunityAccepted() {
        callLayout.setVisibility(View.VISIBLE);
        messageImageView.setVisibility(View.VISIBLE);
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
    }

    private void opportunityNotAccepted() {
        callLayout.setVisibility(View.GONE);
        messageImageView.setVisibility(View.VISIBLE);
        hostedByLayout.setVisibility(View.VISIBLE);
        hostedByHeaderLayout.setVisibility(View.VISIBLE);
        requestingLayout.setVisibility(View.VISIBLE);
        letsPlayButton.setVisibility(View.GONE);
//            requestingLayout.setVisibility(View.VISIBLE);
//            letsPlayButton.setVisibility(View.VISIBLE);
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

    private void setGameDataUI() {
        nextButton.setVisibility(View.VISIBLE);

        //set up sports name
        if (mOpportunity.getClub() != null) {
            sportsNameTextView.setText(mOpportunity.getClub().getName());
            Picasso.with(this).load(mOpportunity.getClub().getImageUrl()).into(sportsImageView);
            toolbar_layout.setBackgroundColor(Color.parseColor(mOpportunity.getClub().getColor()));
            backgroundLayout.setBackgroundColor(Color.parseColor(mOpportunity.getClub().getColor()));
        }

        //set up game date & time
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

        //set up match venue
        if (mOpportunity.getVenue() != null) {
            locationTextView.setText(mOpportunity.getVenue().getAddress());
        }

        //set up opportunity price
        if (mOpportunity.getPrice() != 0) {
            gameAmountTextView.setText(mOpportunity.getPrice() + " / Player");
        } else {
            gameAmountTextView.setText(getResources().getString(R.string.free));
        }

        //set up additional instructions, if any
        setAdditionalInstructions();

    }
//---------------------------------------------------------------------------------------------------------------------------------------

    private void setPlayersUI() {
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
    }


    private void setAdditionalInstructions() {
        if (mOpportunity.getInstructions().size() > 0) {
            gamedataListLayout.setVisibility(View.VISIBLE);
            listOfAdditionalString.removeAllViews();
            for (int i = 0; i < mOpportunity.getInstructions().size(); i++) {
                TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.white_text_view, listOfAdditionalString, false);
                view.setTypeface(HitigerApplication.LIGHT);
                view.setText(mOpportunity.getInstructions().get(i));
                listOfAdditionalString.addView(view);
            }
        }
    }


    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
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
        mEventLifeCycle.setTypeface(HitigerApplication.SEMI_BOLD);
    }

    private void hideViews() {
        requestingLayout.setVisibility(View.GONE);
        requestersHEaderLayout.setVisibility(View.GONE);
        requesterListView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        letsPlayButton.setVisibility(View.GONE);
        messageImageView.setVisibility(View.GONE);
        //menuButton.setVisibility(View.GONE);
        gamedataListLayout.setVisibility(View.GONE);
        callLayout.setVisibility(View.GONE);
        hostedByLayout.setVisibility(View.GONE);
        hostedByHeaderLayout.setVisibility(View.GONE);
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

    private void enablingLetsPlay() {
        requestingLayout.setVisibility(View.GONE);
        messageImageView.setVisibility(View.GONE);
        letsPlayButton.setVisibility(View.VISIBLE);
    }

    private void showConfirmationDailog() {
        com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog confirmOpportunityCustomDialog = new com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog(
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

    private void enablingCancelRequest() {
        requestingLayout.setVisibility(View.VISIBLE);
        messageImageView.setVisibility(View.VISIBLE);
        letsPlayButton.setVisibility(View.GONE);
    }

    //click handling functions --

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
                    Toast.makeText(EventDetailActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                    broadcastIntent.putExtra(NotificationHelper.OPPORTUNITY_ID, mOpportunity.getId());
                    getApplicationContext().sendBroadcast(broadcastIntent);
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


    @OnClick(R.id.lets_play_button)
    public void sendRequest() {
        showProgress("", getResources().getString(R.string.sending_request));
        getApiService().sendRequest(new SendRequestToPlayRequest(mUserProfileDetail.getFbId(), mOpportunity.getId(),
                mUserProfileDetail.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    showConfirmationDailog();
                    enablingCancelRequest();
                    Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                    getApplicationContext().sendBroadcast(broadcastIntent);
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
                    Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                    getApplicationContext().sendBroadcast(broadcastIntent);
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


    @OnClick(R.id.back_button)
    public void backPress() {
        onBackPressed();
    }


    private void showMenuToCreator() {
        if (mOpportunity.getCreatorId() == mUserProfileDetail.getId()) {
            menuButton.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nextButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            nextButton.setLayoutParams(params);
        }
    }

    @OnClick(R.id.menu)
    public void menuClick() {
        PopupMenu popup = new PopupMenu(EventDetailActivity.this, menuButton);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        this.menu = popup.getMenu();
        setMenuTitles();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Recreate")) {
                    recreateOpportunity();
                } else {
                    closeOpportunity();
                }
                return true;
            }
        });
        popup.show();
    }


    private void setMenuTitles() {
        MenuItem item = menu.findItem(R.id.menu_item);
        if (mOpportunity.getTime() == -1) {
            if (mOpportunity.getDate() <= getCurrentDate() || mOpportunity.isDeleted()) {
                item.setTitle("Recreate");
            } else {
                item.setTitle("Close");
            }
        } else {
            if (mOpportunity.getTime() < Calendar.getInstance().getTimeInMillis() || mOpportunity.isDeleted()) {
                item.setTitle("Recreate");
            } else {
                item.setTitle("Close");
            }
        }
    }

    long getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    private void closeOpportunity() {
        HitigerApplication.getInstance().getRestClient().getApiService().closeEvent(new CloseEvent(mUserProfileDetail.getFbId(),
                mUserProfileDetail.getId(), mOpportunity.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                if (hitigerResponse != null && hitigerResponse.isSuccesfull()) {
                    closeEventLayout();
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HitigerApplication.getInstance().showNetworkErrorMessage();

            }
        });
    }

    private void closeEventLayout() {
        toolbar_layout.setBackgroundColor(Color.parseColor("#808080"));
        backgroundLayout.setBackgroundColor(Color.parseColor("#808080"));
        mEventLifeCycle.setText("Event Closed");
        requestingLayout.setVisibility(View.GONE);
    }

    private void recreateOpportunity() {
        Opportunity opportunity = new Opportunity();
        opportunity.setSport(mOpportunity.getClub());
        opportunity.setListOfAdditionalInformation(mOpportunity.getInstructions());
        opportunity.setAmount(String.valueOf(mOpportunity.getPrice()));
        if (mOpportunity.getVenue() != null) {
            opportunity.setAddress(new AddressData(mOpportunity.getVenue().getId(), mOpportunity.getVenue().getAddress(),
                    mOpportunity.getVenue().getAccess(), mOpportunity.getVenue().getLat(), mOpportunity.getVenue().getLng()));
        }
        Intent intent = new Intent(EventDetailActivity.this, CreateOpportynityActivity.class);
        intent.putExtra(CreateOpportynityActivity.OPPORTUNITY_DATA, opportunity);
        startActivity(intent);
    }

    private final BroadcastReceiver reloadOnReceivingNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getLongExtra(NotificationHelper.OPPORTUNITY_ID, (long) -1) == mOpportunity.getId()) {
                if (isActivityInForeground) {
                    getOppDetails();
                } else {
                    shouldReload = true;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isActivityInForeground = true;
        if (shouldReload) {
            getOppDetails();
            shouldReload = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
        registerReceiver(reloadOnReceivingNotification, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reloadOnReceivingNotification);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityInForeground = false;
    }
}
