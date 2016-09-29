package com.hcs.hitiger.view.customdialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailResponse;
import com.hcs.hitiger.api.model.opportunity.OpportunityApiResponse;
import com.hcs.hitiger.api.model.opportunity.SubmitRating;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anuj gupta on 4/27/16.
 */
public class EventRateCustomDialog extends Dialog implements OnClickListener {

    @InjectView(R.id.submit)
    TextView submitView;
    @InjectView(R.id.close_button)
    ImageView closeButton;
    @InjectView(R.id.request_send_to_message)
    TextView requestSendToMessageTextVeiw;
    @InjectView(R.id.select_date)
    TextView dateTextView;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.dialog_header_message)
    TextView dialogHeaderMessage;
    @InjectView(R.id.other_user_profile_image)
    ImageView userProfileImageView;
    @InjectView(R.id.ratingBar)
    RatingBar ratingBar;
    private ProgressDialog progressDialog;

    private OpportunityApiResponse opportunityApiResponse;
    private String fbId;
    private long userId;
    private Context context;

    public EventRateCustomDialog(Context context, GetOpportunityDetailResponse data, String fbId, long userId) {
        super(context);
        this.context = context;
        this.userId = userId;
        this.fbId = fbId;
        this.opportunityApiResponse = data.getOpportunity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_evant_rate_dialog);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.inject(this);

        setFontsForViews();
        setDataInViews();

        closeButton.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d("TAG", "onRatingChanged() returned: " + "");
                submitView.setOnClickListener(EventRateCustomDialog.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    submitView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.enable_button_background));
                } else {
                    submitView.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.enable_button_background));
                }
            }
        });
    }

    private void setDataInViews() {
        userName.setText(opportunityApiResponse.getVersus().getName());
        if (opportunityApiResponse.getVersus().getImageUrl() != null) {
            Picasso.with(getContext()).load(opportunityApiResponse.getVersus().getImageUrl()).placeholder(R.drawable.ic_whistle).into(userProfileImageView);
        }
        dialogHeaderMessage.setText("Welcome back ");
        setDetailsFromOpp();
    }

    private void setFontsForViews() {
        submitView.setTypeface(HitigerApplication.BOLD);
        requestSendToMessageTextVeiw.setTypeface(HitigerApplication.REGULAR);
        userName.setTypeface(HitigerApplication.BOLD);
        dialogHeaderMessage.setTypeface(HitigerApplication.REGULAR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                submitRating(fbId, userId, opportunityApiResponse.getId(), (int) ratingBar.getRating());
                break;
            default:
                break;
        }

    }


    void submitRating(String uniqueId, Long userId, Long oppId, int rating) {
        progressDialog = ProgressDialog.show(context, "", "Please Wait While We Submit Your Rating...");
        HitigerApplication.getInstance().getRestClient().getApiService().
                rateEvent(new SubmitRating(uniqueId, userId, oppId, rating), new Callback<HitigerResponse>() {

                    @Override
                    public void success(HitigerResponse hitigerResponse, Response response) {
                        hideDialog();
                        dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(context, "Some Network Error Occoured Try Again", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }

                    private void hideDialog() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.hide();
                        }
                    }
                });

    }


    private void setDetailsFromOpp() {
        requestSendToMessageTextVeiw.setText("Your " + opportunityApiResponse.getClub().getName() + " Game with ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM");
        String date = dateFormat.format(opportunityApiResponse.getDate());
        if (opportunityApiResponse.getTime() != -1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
            String timeInString = timeFormat.format(opportunityApiResponse.getTime()).replace("a.m.", "AM").replace("p.m.", "PM");
            date += " " + timeInString;
        }
        dateTextView.setText(date + " " + "");
    }
}
