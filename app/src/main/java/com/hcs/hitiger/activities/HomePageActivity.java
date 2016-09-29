package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.HomeFragmentPagerAdapter;
import com.hcs.hitiger.adapters.HomePageDrawerListAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailResponse;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.api.model.user.UpdateUserAddress;
import com.hcs.hitiger.api.model.user.UserFullProfile;
import com.hcs.hitiger.api.model.user.UserRequest;
import com.hcs.hitiger.api.model.user.UserUniqueIdRequest;
import com.hcs.hitiger.background.UserProfileUpdateReceiver;
import com.hcs.hitiger.fragments.OpportunityFragment;
import com.hcs.hitiger.model.BackPressed;
import com.hcs.hitiger.model.ListModel;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.NotificationHelper;
import com.hcs.hitiger.util.SportsViewHelper;
import com.hcs.hitiger.view.customdialogs.EventRateCustomDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomePageActivity extends AppCompatActivity implements Progressable {
    public static final String IS_EDITABLE_MODE = "IS_EDITABLE_MODE";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 123;
    private static final String TAG = "TAG";
    private static final int REQUEST_FOR_SPORT_CHANGES = 15;
    public static final String SHARING_OPP_ID = "sharingOppId";

    private final UserProfileUpdateReceiver updateReceiver = new UserProfileUpdateReceiver();

    @InjectView(R.id.left_drawer_list_view)
    ListView mDrawerListView;
    @InjectView(R.id.profile_image)
    ImageView profileImageView;
    @InjectView(R.id.user_name)
    TextView userNameView;
    @InjectView(R.id.user_email)
    TextView userEmailView;
    @InjectView(R.id.user_contact_number)
    TextView userContactNumberView;
    @InjectView(R.id.edit_person)
    TextView editPersonView;
    @InjectView(R.id.view_pager_detail)
    public ViewPager viewPager;
    @InjectView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.home_page_title)
    TextView title;
    @InjectView(R.id.verified)
    TextView verifiedTextView;
    @InjectView(R.id.played_with_count)
    TextView playedWithCount;
    @InjectView(R.id.followers_count_text)
    TextView followersCountTextView;
    @InjectView(R.id.following_count_text)
    TextView followingCountTextView;
    @InjectView(R.id.invite_friend_text)
    TextView inviteFriendTextView;
    @InjectView(R.id.get_in_touch_text)
    TextView getInTouchTextView;
    @InjectView(R.id.about_yellow_sox_text)
    TextView aboutYellowSoxTextView;
    private boolean isActivityInForeground;
    private boolean shouldReload;
    private List<Sport> userSelectedSportsList;

    @OnClick(R.id.following_text)
    public void showFollowing() {
        Intent intent = new Intent(this, FollowersOrPlayedWithActivity.class);
        intent.putExtra(FollowersOrPlayedWithActivity.USER_PROFILE, userProfileDetail);
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
        intent.putExtra(FollowersOrPlayedWithActivity.USER_PROFILE, userProfileDetail);
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
        intent.putExtra(FollowersOrPlayedWithActivity.USER_PROFILE, userProfileDetail);
        startActivity(intent);
    }

    @OnClick(R.id.followers_count_text)
    public void followersActivity() {
        showFollowers();
    }

    private UserProfileDetail userProfileDetail;
    private ProgressDialog mProgressDialog;
    private View headerView;
    private HomeFragmentPagerAdapter fragmentPagerAdapter;

    @OnClick(R.id.notification)
    public void notifaction() {
        startActivity(new Intent(this, NotificationActivity.class));
    }

    @OnClick(R.id.home_page_title_layout)
    public void clickTitle() {
        Intent intent = null;
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(HomePageActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.invite_friend_layout)
    public void onInviteFriend() {
        startActivity(new Intent(this, InviteFriendsActivity.class));
    }

    @OnClick(R.id.left_drawable_image)
    public void openDrawable() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.RIGHT);
        else
            drawerLayout.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.edit_person_layout)
    public void onEditClick() {
        editPerson();
    }


    @OnClick(R.id.profile_image)
    public void onProfileImageClick() {
        editPerson();
    }

    public void editPerson() {
        Intent intent = new Intent(HomePageActivity.this, CompleteOrEditProfileActivity.class);
        intent.putExtra(IS_EDITABLE_MODE, true);
        startActivity(intent);
    }

    private final AdapterView.OnItemClickListener mDrawerListClickLisner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 1) {
                startActivity(new Intent(HomePageActivity.this, AboutYouActivity.class));
            } else if (position == 2) {
                Intent intent = new Intent(HomePageActivity.this, YourVenueOrSelectAddressActivity.class);
                startActivity(intent);
            }
        }
    };

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
        setContentView(R.layout.activity_home_page);
        userProfileDetail = UserProfileDetail.getUserProfileDetail();
        ButterKnife.inject(this);

        setFonts();
        settingTitle();

        fragmentPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        loadUserProfile();
        initializeData();
        setListInLeftDrawer();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean(HitigerApplication.EVENT_LOAD_KEY)) {
                viewPager.setCurrentItem(1);
            }
        }
        checkForRating();
    }

    private void initializeData() {
        if (userProfileDetail.getImageUrl() != null && !userProfileDetail.getImageUrl().isEmpty()) {
            Picasso.with(this).load(userProfileDetail.getImageUrl()).into(profileImageView);
        }
        userNameView.setText(userProfileDetail.getName());
        userEmailView.setText(userProfileDetail.getEmail());
        userContactNumberView.setText(userProfileDetail.getMobile());

        editPersonView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_edit_preson), null, null, null);
        editPersonView.setCompoundDrawablePadding(4);
        verifiedTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.verified_green), null);
        verifiedTextView.setCompoundDrawablePadding(4);

    }

    private void settingTitle() {
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(this, Locale.getDefault());

        //            addresses = geocoder.getFromLocation(userProfileDetail.getLat(), userProfileDetail.getLng(), 1);
//            final String address = addresses.get(0).getSubLocality() + ", " + addresses.get(0).getAdminArea();
        String address = userProfileDetail.getAddress();
        if (address != null) {
            title.setText(address);
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.point_down), null);
            title.setCompoundDrawablePadding(8);
        }
    }

    private void loadUserProfile() {
        userProfileDetail = UserProfileDetail.getUserProfileDetail();
        showProgress("", "Loading Your Profile..");
        getApiService().getUserProfile(new UserUniqueIdRequest(userProfileDetail.getFbId(), userProfileDetail.getId()),
                new Callback<UserFullProfile>() {
                    @Override
                    public void success(UserFullProfile userFullProfile, Response response) {
                        hideProgress();
                        if (userFullProfile.isSuccesfull()) {
                            userProfileDetail = UserProfileDetail.createUser(userFullProfile.getUser());
                            UserProfileDetail.setUserProfileDetail(userProfileDetail);
                            Sport.setUserSelectedSportList(userFullProfile.getSports());
                        } else
                            HitigerApplication.getInstance().showServerError();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        HitigerApplication.getInstance().showNetworkErrorMessage();
                    }
                }
        );
        setDataToLeftDrawer();
        addHeaderToDrawer();
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private void setFonts() {
        title.setTypeface(HitigerApplication.BOLD);
        userNameView.setTypeface(HitigerApplication.SEMI_BOLD);
        userEmailView.setTypeface(HitigerApplication.SEMI_BOLD);
        userContactNumberView.setTypeface(HitigerApplication.SEMI_BOLD);
        verifiedTextView.setTypeface(HitigerApplication.REGULAR);
        playedWithCount.setTypeface(HitigerApplication.SEMI_BOLD);
        followersCountTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        followingCountTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        editPersonView.setTypeface(HitigerApplication.SEMI_BOLD);
        inviteFriendTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        getInTouchTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        aboutYellowSoxTextView.setTypeface(HitigerApplication.SEMI_BOLD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_SPORT_CHANGES) {
            if (data != null) {
                boolean result = data.getBooleanExtra(EditSportsActivity.IS_CHANGES, false);
                if (result)
                    addHeaderToDrawer();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                Fragment fm = getSupportFragmentManager().getFragments().get(1);
                if (fm instanceof OpportunityFragment) {
                    ((OpportunityFragment) fm).getOpportunities(place.getLatLng().latitude, place.getLatLng().longitude);
                }

                String[] splittedAddress = place.getAddress().toString().split(",");

                final String address = splittedAddress.length > 3 ? splittedAddress[splittedAddress.length - 4] + ", " + splittedAddress[splittedAddress.length - 3]
                        : splittedAddress.length > 0 ? splittedAddress[0] : "";
                updateUserAddressOnServer(address, place.getLatLng().latitude, place.getLatLng().longitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the peration.
            }
        } else if (data != null) {
            this.userProfileDetail = data.getParcelableExtra(LoginActivity.UESR_DETAIL);
            setDataToLeftDrawer();
        }
    }

    private void updateUserAddressOnServer(final String address, final double latitude, final double longitude) {

        getApiService().updateUserAddress(new UpdateUserAddress(userProfileDetail.getFbId(), new UserRequest(userProfileDetail.getId()
                , address, latitude, longitude, userProfileDetail.getFbId())), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                if (hitigerResponse.isSuccesfull()) {
                    updateUserAddressInLocal(latitude, longitude, address);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void updateUserAddressInLocal(double latitude, double longitude, String address) {
        UserProfileDetail user = UserProfileDetail.getUserProfileDetail();
        user.setLat(latitude);
        user.setLng(longitude);
        user.setAddress(address);
        UserProfileDetail.setUserProfileDetail(user);
        userProfileDetail = UserProfileDetail.getUserProfileDetail();
        title.setText(address);
    }

    private void setDataToLeftDrawer() {
        if (userProfileDetail != null) {
            playedWithCount.setText(userProfileDetail.getGamesPlayed() + "");
            followingCountTextView.setText(userProfileDetail.getFollowingCount() + "");
            followersCountTextView.setText(userProfileDetail.getFollowersCount() + "");
        }
    }

    private void setListInLeftDrawer() {
        List<ListModel> listModelList = new ArrayList<>();
        listModelList.add(new ListModel(getResources().getString(R.string.more_about_you), R.drawable.more_about_you));
        listModelList.add(new ListModel(getResources().getString(R.string.venue), R.drawable.venue));

        HomePageDrawerListAdapter homePageDrawerListAdapter = new HomePageDrawerListAdapter(this, 0);
        mDrawerListView.setAdapter(homePageDrawerListAdapter);
        mDrawerListView.setHeaderDividersEnabled(true);
        mDrawerListView.setOnItemClickListener(mDrawerListClickLisner);

        homePageDrawerListAdapter.setListModelList(listModelList);
        homePageDrawerListAdapter.notifyDataSetChanged();
    }

    private void addHeaderToDrawer() {
        userSelectedSportsList=Sport.getUserSelectedSportList();
        if (mDrawerListView.getHeaderViewsCount() != 0) {
            mDrawerListView.removeHeaderView(headerView);
        }
        if (userSelectedSportsList == null || userSelectedSportsList.size() == 0) {
            headerView = getLayoutInflater().inflate(R.layout.drawer_list_item, null);
            ImageView imageView = (ImageView) headerView.findViewById(R.id.left_image);
            TextView textView = (TextView) headerView.findViewById(R.id.center_text);
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_whistle));
            textView.setText("Please select sports");
            textView.setTypeface(HitigerApplication.SEMI_BOLD);
        } else {
            headerView = getLayoutInflater().inflate(R.layout.header_view, null);
            LinearLayout sportsConatiner = (LinearLayout) headerView.findViewById(R.id.sports_container);
            float widthAvailable = 272 * getResources().getDisplayMetrics().density;
            float currentWidth = 0;
            for (Sport sport : userSelectedSportsList) {
                TextView textView = new TextView(this);
                textView.setText(sport.getName());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                SportsViewHelper.setStyleToSporstTetView(textView, params, sport);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) widthAvailable, View.MeasureSpec.AT_MOST);
                textView.measure(widthMeasureSpec, widthMeasureSpec);
                currentWidth += (8 * getResources().getDisplayMetrics().density + textView.getMeasuredWidth());
                if (currentWidth <= widthAvailable) {
                    sportsConatiner.addView(textView);
                } else {
                    break;
                }
            }
        }
        mDrawerListView.addHeaderView(headerView);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomePageActivity.this, EditSportsActivity.class), REQUEST_FOR_SPORT_CHANGES);
            }
        });
    }

    void checkForRating() {
        getApiService().checkForRating(new HitigerRequest(userProfileDetail.getFbId(), userProfileDetail.getId()), new Callback<GetOpportunityDetailResponse>() {

            @Override
            public void success(GetOpportunityDetailResponse getOpportunityDetailResponse, Response response) {
                if (getOpportunityDetailResponse.getOpportunity() != null) {
                    if (HitigerApplication.getInstance().getSharedPreferences().getLong(SHARING_OPP_ID, (long) 0) != getOpportunityDetailResponse.getOpportunity().getId()) {
                        HitigerApplication.getInstance().getSharedPreferences().edit().putLong(SHARING_OPP_ID, getOpportunityDetailResponse.getOpportunity().getId()).apply();
                        showDialogForRating(getOpportunityDetailResponse);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void showDialogForRating(GetOpportunityDetailResponse data) {
        EventRateCustomDialog eventRateCustomDialog = new EventRateCustomDialog(this, data, userProfileDetail.getFbId(), userProfileDetail.getId());
        eventRateCustomDialog.show();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();

        } else {
            BackPressed backPressed = (BackPressed) fragmentPagerAdapter.getItem(viewPager.getCurrentItem());
            if (backPressed.canBackPress()) {
                backPressed.backPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityInForeground = true;
        if (shouldReload) {
            reloadFragments();
            shouldReload = false;
        }
        userProfileDetail = UserProfileDetail.getUserProfileDetail();
        setDataToLeftDrawer();
    }

    private void updateProfileReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(HitigerApplication.UPDATE_PROFILE);
        registerReceiver(updateReceiver, filter);
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
        registerReceiver(mEventUpdateReceiver, filter);
        updateProfileReceiver();
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        isActivityInForeground = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
        unregisterReceiver(mEventUpdateReceiver);
    }

    private final BroadcastReceiver mEventUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isActivityInForeground) {
                reloadFragments();
            } else {
                shouldReload = true;
            }
        }
    };

    private void reloadFragments() {
        ((ReloadEvents) fragmentPagerAdapter.getItem(0)).requestToRefresh();
        ((ReloadEvents) fragmentPagerAdapter.getItem(1)).requestToRefresh();
        Toast.makeText(this, "New update recieved, refreshing list...", Toast.LENGTH_SHORT).show();
    }

    public interface ReloadEvents {
        void requestToRefresh();
    }
}
