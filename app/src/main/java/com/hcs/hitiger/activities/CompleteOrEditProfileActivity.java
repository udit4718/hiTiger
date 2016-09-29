package com.hcs.hitiger.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hcs.hitiger.Constants;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.otp.OtpRequest;
import com.hcs.hitiger.api.model.user.UserRequest;
import com.hcs.hitiger.api.model.user.UserResponseWithCode;
import com.hcs.hitiger.api.model.user.UserUpdateRequest;
import com.hcs.hitiger.model.UserInitialDetail;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.AWSUtility;
import com.hcs.hitiger.view.customdialogs.ConfirmProfileCustomDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CompleteOrEditProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int LOCATION_EXPIRATION_TIME = 30 * 1000;
    public static final int IMAGE_REQUEST_CODE = 101;
    private static final int STORAGE_PERMISSIONS_REQUEST = 111;
    private static final String TAG = CompleteOrEditProfileActivity.class.getSimpleName();
    private boolean isEditableMode;

    @InjectView(R.id.name_edit_text)
    EditText profileName;
    @InjectView(R.id.email_edit_text)
    EditText profileEmail;
    @InjectView(R.id.profile_image)
    ImageView profileImage;
    @InjectView(R.id.mobile_number_edit_text)
    EditText profilePhoneNumber;
    @InjectView(R.id.update_profile_button)
    TextView updateProfileButton;
    @InjectView(R.id.country_code_below_view)
    View view;
    @InjectView(R.id.number_hint_text)
    TextView numberHint;
    @InjectView(R.id.cancel_edit_profile)
    ImageView cancelEditting;
    @InjectView(R.id.edit_or_complete_profile_title)
    TextView title;
    @InjectView(R.id.country_code)
    TextView countryCode;
    @InjectView(R.id.verified)
    TextView verifiedTextView;
    @InjectView(R.id.location_progress_bar)
    ProgressBar locationProgressBar;
    private Bitmap mProfileImageBitmap;
    private ProgressDialog mProgressDialog;

    @OnClick(R.id.profile_image)
    void changeImage() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSIONS_REQUEST);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private Location lastLocation;

    private UserInitialDetail mUserInitialDetail;
    private UserProfileDetail mUserProfileDetail;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int REQUEST_LOCATION_SETTING = 10;
    protected static final int READ_LOCATION_PERMISSIONS_REQUEST = 1;
    protected static final int READ_MESSAGE_PERMISSIONS_REQUEST = 2;
    private GoogleApiClient mGoogleApiClient;

    private View.OnFocusChangeListener mOnFocusChangeLisner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (hasFocus) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                numberHint.setTextColor(getResources().getColor(R.color.colorAccent));
                params.height = 4;
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.textColorGrey));
                numberHint.setTextColor(getResources().getColor(R.color.textColorGrey));
                params.height = 2;
            }
            view.setLayoutParams(params);
        }
    };

    private View.OnClickListener mUpdateProfileButtonClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateProfileCLick();
        }
    };

    private void updateProfileCLick() {
        if (mProfileImageBitmap != null) {
            if (HitigerApplication.getInstance().isNetworkAvailable()) {
                beginAwsUpload(getFileForBitmap(mProfileImageBitmap, mUserProfileDetail.getName().replace(" ","_") + "_" + mUserProfileDetail.getId()));
            } else {
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        } else {
            updateNumberIfChanged();
        }
    }

    private void updateNumberIfChanged() {
        String newNumber = profilePhoneNumber.getText().toString();
        if (newNumber.length() == 10) {
            if (isEditableMode) {
                if (newNumber.equals(mUserProfileDetail.getMobile())) {
                    onBackPressed();
                    return;
                }
                mUserProfileDetail.setMobile(newNumber);
                showConfirmationDialog(null);
            } else {
                mUserInitialDetail = new UserInitialDetail(mUserInitialDetail.getFbId(), mUserInitialDetail.getFullName(),
                        profileEmail.getText().toString(), mUserInitialDetail.getImageUrl(),
                        newNumber);
                if (lastLocation != null && System.currentTimeMillis() - lastLocation.getTime() < LOCATION_EXPIRATION_TIME) {
                    showConfirmationDialog(lastLocation);
                } else {
                    wouldFetchLocationIfLocationPermissionEnabled();
                }
            }
        } else {
            Toast.makeText(CompleteOrEditProfileActivity.this, "Enter ten digit number", Toast.LENGTH_LONG).show();
        }
    }

    private void callForOtpREquest() {
        final ProgressDialog mProgressDialog = ProgressDialog.show(this, "", "Generating otp", true, false);
        String mobileNumber;
        if (isEditableMode) {
            mobileNumber = mUserProfileDetail.getMobile();
        } else {
            mobileNumber = mUserInitialDetail.getPhoneNumber();
        }
        getApiService().generateOtp(new OtpRequest(mobileNumber), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse generateOtpResponse, Response response) {
                mProgressDialog.dismiss();
                if (generateOtpResponse.getResponseCode().getResponsecode().equals("200")) {
                    Intent intent = new Intent(CompleteOrEditProfileActivity.this, EnterOtpActivity.class);
                    if (isEditableMode) {
                        intent.putExtra(HomePageActivity.IS_EDITABLE_MODE, true);
                        intent.putExtra(LoginActivity.UESR_DETAIL, mUserProfileDetail);
                    } else {
                        intent.putExtra(LoginActivity.UESR_DETAIL, mUserInitialDetail);
                        startActivity(intent);
                    }
                    startActivity(intent);
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressDialog.dismiss();
                Toast.makeText(CompleteOrEditProfileActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private View.OnClickListener mCancelProfileEditting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private TextWatcher mProfilePhoneNumberChangeLisner = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String number = s.toString();
            if (isEditableMode) {
                changesForVerifiedText(number);
            } else {
                enableOrDisableButton(number);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void enableOrDisableButton(String number) {
        if (number.length() == 10) {
            updateProfileButton.setOnClickListener(mUpdateProfileButtonClickLisner);
            updateProfileButton.setBackgroundResource(R.drawable.enable_button_background);
        } else {
            updateProfileButton.setOnClickListener(null);
            updateProfileButton.setBackgroundResource(R.drawable.disable_button_background);
        }
    }

    private void changesForVerifiedText(String newNumber) {
        if (mUserProfileDetail.getMobile().equals(newNumber)) {
            verifiedTextView.setVisibility(View.VISIBLE);
            if (mProfileImageBitmap == null) {
                updateProfileButton.setOnClickListener(null);
                updateProfileButton.setBackgroundResource(R.drawable.disable_button_background);
            } else {
                enableOrDisableButton(newNumber);
            }
        } else {
            verifiedTextView.setVisibility(View.GONE);
            enableOrDisableButton(newNumber);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Activity Result", "on create" + lastLocation);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_your_profile);

        isEditableMode = getIntent().getBooleanExtra(HomePageActivity.IS_EDITABLE_MODE, false);
        ButterKnife.inject(this);

        setFontsForView();

        if (isEditableMode) {
            mUserProfileDetail = UserProfileDetail.getUserProfileDetail();
            title.setText("Edit Profile");
            verifiedTextView.setVisibility(View.VISIBLE);
            cancelEditting.setVisibility(View.VISIBLE);
            cancelEditting.setOnClickListener(mCancelProfileEditting);
            profilePhoneNumber.setText(mUserProfileDetail.getMobile());

            profileName.setText(mUserProfileDetail.getName());
            profileEmail.setText(mUserProfileDetail.getEmail());
            if (mUserProfileDetail.getImageUrl() != null)
                Picasso.with(this).load(mUserProfileDetail.getImageUrl()).into(profileImage);
        } else {
            locationProgressBar.setVisibility(View.VISIBLE);
            updateProfileButton.setText("Getting Location");
            mUserInitialDetail = getIntent().getParcelableExtra(LoginActivity.UESR_DETAIL);
            profileName.setText(mUserInitialDetail.getFullName());
            profileEmail.setText(mUserInitialDetail.getEmailId());
            if (mUserInitialDetail.getImageUrl() != null)
                Picasso.with(this).load(mUserInitialDetail.getImageUrl()).into(profileImage);
        }

        profilePhoneNumber.setOnFocusChangeListener(mOnFocusChangeLisner);
        profilePhoneNumber.addTextChangedListener(mProfilePhoneNumberChangeLisner);
    }

    private void showConfirmationDialog(final Location lastLocation) {
        String mobileNumber;
        if (isEditableMode) {
            mobileNumber = mUserProfileDetail.getMobile();
        } else {
            mUserInitialDetail.setLatitude(lastLocation.getLatitude());
            mUserInitialDetail.setLongitude(lastLocation.getLongitude());
            mobileNumber = mUserInitialDetail.getPhoneNumber();
        }
        ConfirmProfileCustomDialog customDialog = new ConfirmProfileCustomDialog(CompleteOrEditProfileActivity.this, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                            READ_MESSAGE_PERMISSIONS_REQUEST);
                } else {
                    callForOtpREquest();
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, mobileNumber);
        customDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void wouldFetchLocationIfLocationPermissionEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        READ_LOCATION_PERMISSIONS_REQUEST);

            } else {
                buildAndConnectGoogleClient();
            }
        } else {

            buildAndConnectGoogleClient();
        }

    }

    private void buildAndConnectGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setFontsForView() {
        title.setTypeface(HitigerApplication.BOLD);
        profileName.setTypeface(HitigerApplication.BOLD);
        profileEmail.setTypeface(HitigerApplication.BOLD);
        updateProfileButton.setTypeface(HitigerApplication.BOLD);
        profilePhoneNumber.setTypeface(HitigerApplication.BOLD);
        countryCode.setTypeface(HitigerApplication.BOLD);
        verifiedTextView.setTypeface(HitigerApplication.REGULAR);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null && isLocationValid(lastLocation)) {
            updateProfileButton.setText("Update Profile");
            locationProgressBar.setVisibility(View.GONE);
            if (!profilePhoneNumber.getText().toString().isEmpty())
                showConfirmationDialog(lastLocation);
        } else {
            checkForGpsEnabled();
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_EXPIRATION_TIME);
        locationRequest.setFastestInterval(5 * 1000);
        return locationRequest;
    }

    private void checkForGpsEnabled() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest());

        builder.setAlwaysShow(true); //this is the key ingredient
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                getLocationRequest(), CompleteOrEditProfileActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    CompleteOrEditProfileActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                wouldFetchLocationIfLocationPermissionEnabled();
                                return;
                            }
                        } else {
                            wouldFetchLocationIfLocationPermissionEnabled();
                            return;
                        }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                getLocationRequest(), CompleteOrEditProfileActivity.this);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case IMAGE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    mProfileImageBitmap = (Bitmap) extras.get("data");
                    profileImage.setImageBitmap(mProfileImageBitmap);
                    updateProfileCLick();
                }
            }
        }
    }

    private String getFileForBitmap(Bitmap imageBitmap, String fileName) {
        String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, fileName + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, fileName + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + fileName);
        }
        try {
            outStream = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file.getAbsolutePath();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("check", "checking");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && mGoogleApiClient != null) {
            if (isLocationValid(location)) {
                lastLocation = location;
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }
                updateProfileButton.setText("Update Profile");
                locationProgressBar.setVisibility(View.GONE);
                updateProfileCLick();
            }
        }
    }

    private boolean isLocationValid(Location location) {
        if (Calendar.getInstance().getTimeInMillis() - location.getTime() < LOCATION_EXPIRATION_TIME) {
            return true;
        }
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("connection failed", connectionResult.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_LOCATION_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildAndConnectGoogleClient();
                    Log.i("Location", "in if");
                } else {
                    Log.i("Location", "in else");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Location is necessary to use this applicaton.Go to Permissions and grant permissions");
                    alertDialogBuilder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }
            }
            break;
            case READ_MESSAGE_PERMISSIONS_REQUEST:
                callForOtpREquest();
                break;
            case STORAGE_PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Storage access is necessary to take photo.Go to Permissions and grant permissions");
                    alertDialogBuilder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isEditableMode)
            wouldFetchLocationIfLocationPermissionEnabled();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
        }
    }

    /*
  * Begins to upload the file specified by the file path.
  */
    private void beginAwsUpload(String filePath) {
        showProgress("", "updating profile image...");
        final File file = new File(filePath);
        TransferObserver observer = AWSUtility.getTransferUtility(this).upload(Constants.BUCKET_NAME, file.getName(),
                file, CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int i, TransferState transferState) {
                if (transferState.equals(TransferState.COMPLETED)) {
                    file.delete();
                    hideProgress();
                    if(isEditableMode){
                        mUserProfileDetail.setImageUrl(Constants.AWS_BUCKET_URL + file.getName());
                        updateUserProfile();
                    }else{
                        mUserInitialDetail.setImageUrl(Constants.AWS_BUCKET_URL + file.getName());
                        updateNumberIfChanged();
                    }
                    Log.d(TAG, "onStateChanged() returned: ");
                }
            }

            @Override
            public void onProgressChanged(int i, long l, long l1) {
                Log.d(TAG, "onProgressChanged() returned: ");

            }

            @Override
            public void onError(int i, Exception e) {
                file.delete();
                Log.d(TAG, "onError() returned: ");
                hideProgress();
            }
        });
    }

    public void showProgress(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(this, title, message, true);
    }

    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void updateUserProfile() {
        showProgress("","updating profile image");
        getApiService().updateUserProfile(new UserUpdateRequest(mUserProfileDetail.getFbId(), new UserRequest(mUserProfileDetail.getId(),
                mUserProfileDetail.getName(), mUserProfileDetail.getEmail(), mUserProfileDetail.getMobile(), mUserProfileDetail.getFbId(),
                mUserProfileDetail.getImageUrl())), new Callback<UserResponseWithCode>() {
            @Override
            public void success(UserResponseWithCode userResponce, Response response) {
                hideProgress();
                if (userResponce.isSuccesfull()) {
                    UserProfileDetail.setUserProfileDetail(UserProfileDetail.createUser(userResponce.getUser()));
                    mProfileImageBitmap = null;
                    updateNumberIfChanged();
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
