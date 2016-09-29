package com.hcs.hitiger.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.opportunity.CreateOpportynityRequest;
import com.hcs.hitiger.api.model.opportunity.OpportunityRequest;
import com.hcs.hitiger.api.model.opportunity.OpportunityResponse;
import com.hcs.hitiger.fragments.AdditionalInformationFragment;
import com.hcs.hitiger.fragments.AddressFragment;
import com.hcs.hitiger.fragments.AmountFragment;
import com.hcs.hitiger.fragments.DateFragment;
import com.hcs.hitiger.fragments.SportsFragment;
import com.hcs.hitiger.fragments.TimeFragment;
import com.hcs.hitiger.model.Opportunity;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.view.customdialogs.AdditionalInformationCustomDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateOpportynityActivity extends AppCompatActivity implements GetOpportunityInterface, Progressable {
    public static final String OPPORTUNITY_DATA = "opportunity";

    @InjectView(R.id.create_opporotunity_title)
    TextView title;
    @InjectView(R.id.next_button)
    TextView nextButton;
    @InjectView(R.id.first_icon)
    ImageView firstIcon;
    @InjectView(R.id.second_icon)
    ImageView secondIcon;
    @InjectView(R.id.third_icon)
    ImageView thirdIcon;
    @InjectView(R.id.fourth_icon)
    ImageView fourthIcon;
    @InjectView(R.id.fifth_icon)
    ImageView fifthIcon;
    @InjectView(R.id.sixth_icon)
    ImageView sixthIcon;

    private Map<Integer, ImageView> mImageViewMap;
    private int position;
    private Fragment fragment;
    private boolean isNextEnabled;
    private Opportunity mOpportunity;
    private UserProfileDetail userProfileDetail;
    private ProgressDialog mProgressDialog;

    @OnClick(R.id.backpress_button)
    public void backPress() {
        onBackPressed();
    }

    @OnClick(R.id.next_button)
    public void clickNext() {
        if (position < 7) {
            addFragment();
        } else if (position == 7) {
            AdditionalInformationCustomDialog additionalInformationCustomDialog = new AdditionalInformationCustomDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createOpportunity();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, mOpportunity);
            additionalInformationCustomDialog.show();
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


    private void createOpportunity() {
        long price = 0;
        if (!mOpportunity.isFree()) {
            price = Long.parseLong(mOpportunity.getAmount());
        }
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID) + Calendar.getInstance().getTimeInMillis();
        mOpportunity.getDate().set(Calendar.HOUR_OF_DAY, 0);
        mOpportunity.getDate().set(Calendar.MINUTE, 0);
        long time = -1;
        if (!mOpportunity.isAllDay()) {
            time = mOpportunity.getTime().getTimeInMillis();
        }
        long venueId = -1;
        if (mOpportunity.getAddressData() != null) {
            venueId = mOpportunity.getAddressData().getId();
        }
        showProgress("", getResources().getString(R.string.creating_opportunities));
        getApiService().createOpportunity(new CreateOpportynityRequest(userProfileDetail.getFbId(), new OpportunityRequest(deviceId,
                mOpportunity.getSport().getId(), price, mOpportunity.getDate().getTimeInMillis(), time, venueId, userProfileDetail.getId(),
                mOpportunity.getListOfAdditionalInformation())), new Callback<OpportunityResponse>() {
            @Override
            public void success(OpportunityResponse opportunityResponse, Response response) {
                hideProgress();
                if (opportunityResponse.isSuccesfull()) {
                    Toast.makeText(CreateOpportynityActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(HitigerApplication.EVENT_LOAD_KEY, true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                HitigerApplication.getInstance().showNetworkErrorMessage();
                hideProgress();
            }
        });
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private void addFragment() {
        if (isNextEnabled) {
            isNextEnabled = false;
            changeIcons(position);
            changeFragment(position);
            position++;
        }
    }

    private void changeIcons(int position) {
        for (int i = 1; i < 7; i++) {
            if (i == position) {
                mImageViewMap.get(i).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.black_circle));
            } else if (i < position) {
                mImageViewMap.get(i).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.verified1));
            } else {
                mImageViewMap.get(i).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.grey_circle));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_opportynity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userProfileDetail = UserProfileDetail.getUserProfileDetail();

        ButterKnife.inject(this);
        setFonts();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(OPPORTUNITY_DATA)) {
            position = 2;
            mOpportunity = getIntent().getExtras().getParcelable(OPPORTUNITY_DATA);
        } else {
            position = 1;
            mOpportunity = new Opportunity();
        }
        isNextEnabled = true;

        initializeMapping();
        addFragment();
    }

    private void initializeMapping() {
        mImageViewMap = new HashMap<>();
        mImageViewMap.put(1, firstIcon);
        mImageViewMap.put(2, secondIcon);
        mImageViewMap.put(3, thirdIcon);
        mImageViewMap.put(4, fourthIcon);
        mImageViewMap.put(5, fifthIcon);
        mImageViewMap.put(6, sixthIcon);
    }

    private void setFonts() {
        title.setTypeface(HitigerApplication.BOLD);
        nextButton.setTypeface(HitigerApplication.BOLD);
    }

    public void setNextEnabled() {
        isNextEnabled = true;
        nextButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.enable_button_background));
    }

    public void setCreateButton() {
        isNextEnabled = true;
        nextButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.enable_button_background));
        nextButton.setText(getResources().getString(R.string.create));
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void setNextButton() {
        isNextEnabled = true;
        nextButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.enable_button_background));
        nextButton.setText(getResources().getString(R.string.next));
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.back_arrow), null);
    }

    public void setNextDisable() {
        nextButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.disable_button_background));
        isNextEnabled = false;
    }

    private void changeFragment(int position) {
        switch (position - 1) {
            case 0:
                fragment = SportsFragment.getInstance(userProfileDetail);
                break;
            case 1:
                fragment = DateFragment.getInstance();
                break;
            case 2:
                fragment = TimeFragment.getInstance();
                break;
            case 3:
                fragment = AmountFragment.getInstance();
                break;
            case 4:
                fragment = AddressFragment.getInstance();
                break;
            default:
                fragment = AdditionalInformationFragment.getInstance();
                break;
        }
        if (position - 1 != 0)
            setNextDisable();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.selected_frames, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            position--;
            if (position == 6) {
                setNextButton();
            } else
                setNextEnabled();
            changeIcons(position - 1);
            fm.popBackStack();
        } else {
            fm.popBackStack();
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("i ", "df");
    }

    @Override
    public Opportunity getOpportunity() {
        return mOpportunity;
    }
}
