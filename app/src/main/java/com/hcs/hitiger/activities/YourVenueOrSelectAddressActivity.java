package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.VenueAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.user.CreateUserVenueRequest;
import com.hcs.hitiger.api.model.user.UserUniqueIdRequest;
import com.hcs.hitiger.api.model.user.UserVenueResponse;
import com.hcs.hitiger.api.model.user.VenueRequest;
import com.hcs.hitiger.api.model.user.VenueResponse;
import com.hcs.hitiger.fragments.AddressFragment;
import com.hcs.hitiger.model.AddressData;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class YourVenueOrSelectAddressActivity extends AppCompatActivity implements Progressable {
    private static final String TAG = "TAG";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private VenueAdapter mClubAdapter;
    private List<AddressData> list;
    private UserProfileDetail mUserProfileDetail;
    private ProgressDialog mProgressDialog;
    private boolean isAddressSearch;
    private String address;


    @InjectView(R.id.vanue_list)
    ListView venueList;
    @InjectView(R.id.your_venue_title)
    TextView title;
    @InjectView(R.id.add_venue_button)
    TextView addVenueButton;
    @InjectView(R.id.empty_view)
    TextView emptyVenueTextView;
    @InjectView(R.id.search_address)
    EditText selectAddressEditText;

    @OnClick(R.id.backpress_button)
    public void backPress() {
        onBackPressed();
    }

    @OnClick(R.id.add_venue_button)
    public void addVenue() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @OnClick(R.id.search_address)
    public void searchAddress() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                createVenue(place);
                this.address = place.getAddress().toString();
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private void createVenue(Place place) {
        showProgress("", "Creating Venues...");
        getApiServices().createVenue(new CreateUserVenueRequest(mUserProfileDetail.getFbId(), new VenueRequest(place.getAddress().toString(),
                place.getLatLng().latitude, place.getLatLng().longitude, 1, mUserProfileDetail.getId())), new Callback<UserVenueResponse>() {
            @Override
            public void success(UserVenueResponse userVenueResponse, Response response) {
                hideProgress();
                if (userVenueResponse.isSuccesfull()) {
                    loadingVenues();
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showServerError();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_venue);

        isAddressSearch = getIntent().getBooleanExtra(AddressFragment.SELECT_FOR_ADDRESS, false);

        mUserProfileDetail = UserProfileDetail.getUserProfileDetail();
        mProgressDialog = new ProgressDialog(this);

        ButterKnife.inject(this);
        setFonts();

        if (isAddressSearch) {
            addVenueButton.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            selectAddressEditText.setVisibility(View.VISIBLE);
        }

        loadingVenues();
    }

    private void loadingVenues() {
        if (isAddressSearch) {
            showProgress("", "Loading Address...");
        } else {
            showProgress("", "Loading Venues...");
        }
        getApiServices().getUserVenues(new UserUniqueIdRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId()), new Callback<UserVenueResponse>() {
            @Override
            public void success(UserVenueResponse userVenueResponse, Response response) {
                hideProgress();
                if (userVenueResponse.isSuccesfull()) {
                    if (userVenueResponse.getVenues() != null && userVenueResponse.getVenues().size() > 0) {
                        if (emptyVenueTextView.getVisibility() != View.GONE)
                            emptyVenueTextView.setVisibility(View.GONE);
                        setAdaptersToList(userVenueResponse.getVenues());
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


    private ApiService getApiServices() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private void setFonts() {
        title.setTypeface(HitigerApplication.BOLD);
        addVenueButton.setTypeface(HitigerApplication.SEMI_BOLD);
    }

    private void setAdaptersToList(List<VenueResponse> userVenues) {
        list = new ArrayList<>();
        for (int i = 0; i < userVenues.size(); i++) {
            list.add(new AddressData(userVenues.get(i).getId(), userVenues.get(i).getAddress(), userVenues.get(i).getAccess(),
                    userVenues.get(i).getLat(), userVenues.get(i).getLng()));
            if (isAddressSearch && address != null && address.equals(userVenues.get(i).getAddress())) {
                Intent intent = new Intent();
                intent.putExtra(AddressFragment.ADDRESS, list.get(i));
                setResult(AddressFragment.REQUSET_CODE, intent);
                finish();
            }

        }
        if (mClubAdapter == null)
            mClubAdapter = new VenueAdapter(this, 0);
        venueList.setAdapter(mClubAdapter);
        mClubAdapter.clear();
        mClubAdapter.addData(list, isAddressSearch);
        mClubAdapter.notifyDataSetChanged();
    }
}