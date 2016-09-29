package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.HomePagerAdapter;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.sports.GetAllSportsResponse;
import com.hcs.hitiger.api.model.user.LoginRequest;
import com.hcs.hitiger.api.model.user.LoginResponse;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.model.LoginInterface;
import com.hcs.hitiger.model.LoginSourceCode;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserInitialDetail;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.view.FacebookApiLoginHandler;
import com.hcs.hitiger.view.GoogleApiClientHandler;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity implements LoginInterface, Progressable {

    public static final String UESR_DETAIL = "UESR_DETAIL";

    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.facebook_login_button)
    TextView facebookLogin;
    @InjectView(R.id.google_login_button)
    TextView googleLogin;

    private ArrayList<ImageView> dots;
    private final int NUM_PAGES = 3;
    private FacebookApiLoginHandler facebookApiLoginHandler;
    private GoogleApiClientHandler googleApiClientHandler;
    private ProgressDialog mProgressDialog;

    @OnClick(R.id.google_login_button)
    public void onGoogleClick() {
        if (googleApiClientHandler == null)
            googleApiClientHandler = new GoogleApiClientHandler(this);
        googleApiClientHandler.signIn();
    }

    @OnClick(R.id.facebook_login_button)
    public void onFacebookClick() {
        facebookApiLoginHandler = new FacebookApiLoginHandler(LoginActivity.this);
        facebookApiLoginHandler.login();
    }

    private ViewPager.OnPageChangeListener mViewPagerChangeLisner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            selectDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        viewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager(), NUM_PAGES));

        addDots();

        googleLogin.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_google), null, null, null);
        facebookLogin.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_facebook), null, null, null);

        viewPager.setOnPageChangeListener(mViewPagerChangeLisner);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleApiClientHandler.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleApiClientHandler.handleSignInResult(result);
        } else if (facebookApiLoginHandler != null) {
            facebookApiLoginHandler.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void doSocialLogin(String fullName, String email, String image, LoginSourceCode loginSourceCode, String id) {
        UserInitialDetail userInitialDetail = new UserInitialDetail(id, fullName, email, image);
        checkLoginUser(userInitialDetail);
    }

    private void checkLoginUser(final UserInitialDetail userInitialDetail) {
        showProgress("", getResources().getString(R.string.checking_credential));
        HitigerApplication.getInstance().getRestClient().getApiService().loginUser(new LoginRequest(userInitialDetail.getFbId()), new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                hideProgress();
                if (loginResponse.getResponseCode().getResponsecode().equals("200")) {
                    if (loginResponse.getUser() != null) {
                        UserProfileDetail.setUserProfileDetail(UserProfileDetail.createUser(loginResponse.getUser()));
                        HitigerApplication.getInstance().loadPubnubHistory();
                        HitigerApplication.getInstance().enableGcmFromServers();
                        getSportList();
                    } else
                    {
                        Intent intent = new Intent(LoginActivity.this, CompleteOrEditProfileActivity.class);
                        intent.putExtra(UESR_DETAIL, userInitialDetail);
                        startActivity(intent);
                    }
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

    private void getSportList() {
        UserProfileDetail userProfileDetail = UserProfileDetail.getUserProfileDetail();
        showProgress("", getResources().getString(R.string.loading_sports));
        HitigerApplication.getInstance().getRestClient().getApiService().getAllSports(new HitigerRequest(userProfileDetail.getFbId(),
                userProfileDetail.getId()), new Callback<GetAllSportsResponse>() {
            @Override
            public void success(GetAllSportsResponse getAllSportsResponse, Response response) {
                hideProgress();
                if (getAllSportsResponse.isSuccesfull()) {
                    Sport.setSportList(getAllSportsResponse.getSportList());
                    Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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

    public void addDots()
    {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.bottom_images);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);
            if (i == 0)
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fill_circle_24dp));
            else
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cicle_grey));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            dotsLayout.addView(dot, params);
            dots.add(dot);
        }
    }

    public void selectDot(int idx)
    {
        Resources res = getResources();
        for (int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i == idx) ? (R.drawable.fill_circle_24dp) : (R.drawable.cicle_grey);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }
}
