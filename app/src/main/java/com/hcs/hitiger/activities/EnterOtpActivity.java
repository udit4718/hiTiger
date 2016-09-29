package com.hcs.hitiger.activities;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.otp.GenerateOtpResponse;
import com.hcs.hitiger.api.model.otp.OtpRequest;
import com.hcs.hitiger.api.model.sports.GetAllSportsResponse;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.api.model.user.User;
import com.hcs.hitiger.api.model.user.UserRequest;
import com.hcs.hitiger.api.model.user.UserResponseWithCode;
import com.hcs.hitiger.api.model.user.UserUpdateRequest;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserInitialDetail;
import com.hcs.hitiger.model.UserProfileDetail;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EnterOtpActivity extends AppCompatActivity implements Progressable {
    @InjectView(R.id.verifying_layout)
    RelativeLayout verifyingRelativeLayout;
    @InjectView(R.id.first_edit_text_otp)
    EditText firstOtpNumberTextView;
    @InjectView(R.id.second_edit_text_otp)
    EditText secondOtpNumberTextView;
    @InjectView(R.id.third_edit_text_otp)
    EditText thirdOtpNumberTextView;
    @InjectView(R.id.fourth_edit_text_otp)
    EditText fourthOtpNumberTextView;
    @InjectView(R.id.otp_message)
    TextView otpMessageTextView;
    @InjectView(R.id.resend_otp)
    TextView resendOtpTextView;
    @InjectView(R.id.otp_title)
    TextView title;

    ImageView rotater;
    private final int COUNTER_START_TIME = 30; //wait for otp, time in seconds
    private final int COUNTER_END_TIME = 1;
    static ProgressDialog mProgressDialog;
    private UserInitialDetail mUserInitialDetail;
    boolean resendEnabled = false;

    @OnClick(R.id.resend_otp)
    public void resendOtp() {
        if (resendEnabled) {
            getApiService().generateOtp(new OtpRequest(mUserInitialDetail.getPhoneNumber()), new Callback<HitigerResponse>() {
                @Override
                public void success(HitigerResponse hitigerResponse, Response response) {
                    if (hitigerResponse.isSuccesfull())
                        startCounter();
                    else
                        HitigerApplication.getInstance().showServerError();
                }

                @Override
                public void failure(RetrofitError error) {
                    HitigerApplication.getInstance().showNetworkErrorMessage();
                    resendEnabled = true;
                }
            });
        }
    }

    private TextWatcher mFourthDigitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!firstOtpNumberTextView.getText().toString().isEmpty() && !secondOtpNumberTextView.getText().toString().isEmpty() &&
                    !thirdOtpNumberTextView.getText().toString().isEmpty() && !fourthOtpNumberTextView.getText().toString().isEmpty()) {
                String otp;
                otp = firstOtpNumberTextView.getText().toString() + secondOtpNumberTextView.getText().toString() +
                        thirdOtpNumberTextView.getText().toString() + fourthOtpNumberTextView.getText().toString();
                verifyingRelativeLayout.setVisibility(View.VISIBLE);
                checkOtp(otp);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private boolean isEditableMode;
    private UserProfileDetail mUserDetail;

    private void checkOtp(String otp) {
        String mobile;
        if (isEditableMode) {
            mobile = mUserDetail.getMobile();
        } else {
            mobile = mUserInitialDetail.getPhoneNumber();
        }
        getApiService().verifyOtp(new OtpRequest(mobile, otp), new Callback<GenerateOtpResponse>() {
            @Override
            public void success(GenerateOtpResponse generateOtpResponse, Response response) {
                if (generateOtpResponse.isSuccesfull()) {
                    if (isEditableMode)
                        updateUserProfile();
                    else
                        createUserProfile();
                } else {
                    Toast.makeText(EnterOtpActivity.this, "Wrong Otp", Toast.LENGTH_LONG).show();
                    reEnterOTP();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HitigerApplication.getInstance().showNetworkErrorMessage();
                onBackPressed();
            }
        });
    }

    private void updateUserProfile() {
        getApiService().updateUserProfile(new UserUpdateRequest(mUserDetail.getFbId(), new UserRequest(mUserDetail.getId(),
                mUserDetail.getName(), mUserDetail.getEmail(), mUserDetail.getMobile(), mUserDetail.getFbId(),
                mUserDetail.getImageUrl())), new Callback<UserResponseWithCode>() {
            @Override
            public void success(UserResponseWithCode userResponce, Response response) {
                if (userResponce.isSuccesfull()) {
                    UserProfileDetail.setUserProfileDetail(UserProfileDetail.createUser(userResponce.getUser()));
                    HitigerApplication.getInstance().loadPubnubHistory();
                    HitigerApplication.getInstance().enableGcmFromServers();
                    getSportList();
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
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

    private void getSportList() {
        UserProfileDetail userProfileDetail = UserProfileDetail.getUserProfileDetail();
        showProgress("", getResources().getString(R.string.loading_sports));
        HitigerApplication.getInstance().getRestClient().getApiService().getAllSports(new HitigerRequest(userProfileDetail.getFbId(),
                userProfileDetail.getId()), new Callback<GetAllSportsResponse>() {
            @Override
            public void success(GetAllSportsResponse getAllSportsResponse, Response response) {
                hideProgress();
                if (getAllSportsResponse.isSuccesfull())
                    Sport.setSportList(getAllSportsResponse.getSportList());
                else
                    HitigerApplication.getInstance().showServerError();
                startNextActivity();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
                startNextActivity();
            }
        });
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void reEnterOTP() {
        verifyingRelativeLayout.setVisibility(View.GONE);
        firstOtpNumberTextView.setText("");
        secondOtpNumberTextView.setText("");
        thirdOtpNumberTextView.setText("");
        fourthOtpNumberTextView.setText("");
        firstOtpNumberTextView.requestFocus();
    }

    private void createUserProfile() {
        getApiService().createUser(new User(new UserRequest(mUserInitialDetail.getFullName(),
                mUserInitialDetail.getEmailId(), mUserInitialDetail.getPhoneNumber(), mUserInitialDetail.getFbId(),
                mUserInitialDetail.getImageUrl(), 1, 1, "address", mUserInitialDetail.getLatitude(), mUserInitialDetail.getLongitude())), new Callback<UserResponseWithCode>() {
            @Override
            public void success(UserResponseWithCode userResponseWithCode, Response response) {
                if (userResponseWithCode.isSuccesfull()) {
                    UserProfileDetail.setUserProfileDetail(UserProfileDetail.createUser(userResponseWithCode.getUser()));
                    getSportList();
                } else {
                    HitigerApplication.getInstance().showServerError();
                    reEnterOTP();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HitigerApplication.getInstance().showNetworkErrorMessage();
                reEnterOTP();
            }
        });
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        //sms reciever registration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                registerReciever();
            } else {
                Toast.makeText(EnterOtpActivity.this, "Please enter 4-digit OTP you received", Toast.LENGTH_SHORT).show();
            }
        } else {
            registerReciever();
        }
        rotater = (ImageView) findViewById(R.id.ivRotate);
        startCounter();
        //sms reciever registration
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);


        isEditableMode = getIntent().getBooleanExtra(HomePageActivity.IS_EDITABLE_MODE, false);

        if (isEditableMode) {
            mUserDetail = getIntent().getParcelableExtra(LoginActivity.UESR_DETAIL);
        } else
            mUserInitialDetail = getIntent().getParcelableExtra(LoginActivity.UESR_DETAIL);
        ButterKnife.inject(this);

        setStyles();
        Spannable wordtoSpan;
        if (isEditableMode) {
            wordtoSpan = new SpannableString("Please enter 4 digit OTP you have recieved on +91" + mUserDetail.getMobile() + " via SMS.");
        } else {
            wordtoSpan = new SpannableString("Please enter 4 digit OTP you have recieved on +91" + mUserInitialDetail.getPhoneNumber() + " via SMS.");
        }
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                wordtoSpan.length() - 23, wordtoSpan.length() - 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        otpMessageTextView.setText(wordtoSpan);


        firstOtpNumberTextView.addTextChangedListener(requestToNext(secondOtpNumberTextView));
        secondOtpNumberTextView.addTextChangedListener(requestToNext(thirdOtpNumberTextView));
        thirdOtpNumberTextView.addTextChangedListener(requestToNext(fourthOtpNumberTextView));
        fourthOtpNumberTextView.addTextChangedListener(mFourthDigitTextWatcher);

        secondOtpNumberTextView.setOnKeyListener(requestToPrevious(firstOtpNumberTextView, secondOtpNumberTextView));
        thirdOtpNumberTextView.setOnKeyListener(requestToPrevious(secondOtpNumberTextView, thirdOtpNumberTextView));
        fourthOtpNumberTextView.setOnKeyListener(requestToPrevious(thirdOtpNumberTextView, fourthOtpNumberTextView));
    }

    private View.OnKeyListener requestToPrevious(final EditText previousEditText, final EditText currentEditText) {
        View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            previousEditText.requestFocus();
                            previousEditText.setText("");
                        }
                    }, 10);
                    return true;
                }
                return false;
            }
        };
        return onKeyListener;
    }

    private void setStyles() {
        firstOtpNumberTextView.setTypeface(HitigerApplication.REGULAR);
        secondOtpNumberTextView.setTypeface(HitigerApplication.REGULAR);
        thirdOtpNumberTextView.setTypeface(HitigerApplication.REGULAR);
        fourthOtpNumberTextView.setTypeface(HitigerApplication.REGULAR);
        otpMessageTextView.setTypeface(HitigerApplication.REGULAR);
        title.setTypeface(HitigerApplication.BOLD);
        resendOtpTextView.setTypeface(HitigerApplication.BOLD);
    }

    private TextWatcher requestToNext(final EditText nextEditText) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        return textWatcher;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (verifyingRelativeLayout.getVisibility() == View.VISIBLE) {
            verifyingRelativeLayout.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        @TargetApi(23)
        public void onReceive(Context context, Intent intent) {
            mProgressDialog = new ProgressDialog(EnterOtpActivity.this);
            mProgressDialog.setMessage("Verifying OTP");
            mProgressDialog.show();
            Bundle bundle = intent.getExtras();
            Object[] smsArray = (Object[]) bundle.get("pdus");
            SmsMessage message;
            for (Object singleMessage : smsArray) {
                //TODO
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    message = SmsMessage.createFromPdu((byte[]) singleMessage, bundle.getString("format"));
                } else {
                    message = SmsMessage.createFromPdu((byte[]) singleMessage);
                }

                String sms = message.getDisplayMessageBody();
                String[] smsWords = sms.split(" ");
                for (String otp : smsWords) {
                    if (otp.length() == 4 && isInteger(otp)) {
                        checkOtp(otp);
                        mProgressDialog.hide();
                        unregisterReceiver(this);
                        break;
                    }
                }
            }

        }

        //test for integer
        public boolean isInteger(String s) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        }
    };

    private void startCounter() {
        resendEnabled = false;
        rotater.setVisibility(View.VISIBLE);
        final RotateAnimation rotate = new RotateAnimation(0, 358, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(800);
        new CountDownTimer(COUNTER_START_TIME * 1000, COUNTER_END_TIME * 1000) {
            public void onTick(long millisUntilFinished) {
                resendOtpTextView.setText(millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                rotater.setVisibility(View.GONE);
                resendOtpTextView.setText("Resend OTP");
                resendEnabled = true;
            }
        }.start();
    }

    private void registerReciever() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }
}
