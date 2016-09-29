package com.hcs.hitiger.view.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.model.Opportunity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 4/27/16.
 */
public class AdditionalInformationCustomDialog extends Dialog implements OnClickListener {

    @InjectView(R.id.confirm_post)
    TextView confirm;
    @InjectView(R.id.edit_dialog)
    TextView edit;
    @InjectView(R.id.game_instructions_layout)
    LinearLayout gamedataLayout;
    @InjectView(R.id.game_address)
    TextView gameAddressTextView;
    @InjectView(R.id.game_date)
    TextView gameDateTextView;
    @InjectView(R.id.game_timing)
    TextView gameTimingTextView;
    @InjectView(R.id.game_price)
    TextView gamePriceTextView;
    @InjectView(R.id.review_opportunity_message)
    TextView reviewOpportunityMessageTextView;
    @InjectView(R.id.dialog_header_message)
    TextView reviewOpportunityTextView;
    @InjectView(R.id.close_button)
    ImageView closeButton;
    @InjectView(R.id.list_of_instructions)
    LinearLayout additionalInfoLinearLayout;
    @InjectView(R.id.address_layout)
    LinearLayout addressLinearLayout;
    @InjectView(R.id.game_name)
    TextView gameName;
    @InjectView(R.id.game_image)
    ImageView gameImage;


    private final OnClickListener mPositiveCallback;
    private final OnClickListener mNegativeCalback;
    private final Opportunity mOpportunity;

    public AdditionalInformationCustomDialog(Context context, OnClickListener positiveCallback, OnClickListener negativeCalback, Opportunity opportunity) {
        super(context);
        mPositiveCallback = positiveCallback;
        mNegativeCalback = negativeCalback;
        mOpportunity = opportunity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_additional_information_dialog);
        ButterKnife.inject(this);
        setFontsForViews();
        gamePriceTextView.setText("Free");
        confirm.setOnClickListener(this);
        edit.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        setOpportunityData();
    }

    private void setOpportunityData() {
        gameName.setText(mOpportunity.getSport().getName());
        Picasso.with(getContext()).load(mOpportunity.getSport().getImageUrl()).placeholder(R.drawable.ic_whistle).into(gameImage);

        int defaultColor = ContextCompat.getColor(getContext(), R.color.blue);
        gameName.setTextColor(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{android.R.attr.state_pressed}, new int[]{}},
                new int[]{defaultColor, defaultColor, Color.parseColor(mOpportunity.getSport().getColor())}));

        if (mOpportunity.getListOfAdditionalInformation().size() > 0) {
            gamedataLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < mOpportunity.getListOfAdditionalInformation().size(); i++) {
                TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.text_view, additionalInfoLinearLayout, false);
                view.setTypeface(HitigerApplication.REGULAR);
                view.setText(mOpportunity.getListOfAdditionalInformation().get(i));
                additionalInfoLinearLayout.addView(view);
            }
        } else {
            gamedataLayout.setVisibility(View.GONE);
        }
        if (!mOpportunity.isFree()) {
            gamePriceTextView.setText(mOpportunity.getAmount());
            gamePriceTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.rupee), null, null, null);
        }
        if (!mOpportunity.isAllDay()) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
            String timeInString = timeFormat.format(mOpportunity.getTime().getTimeInMillis()).replace("a.m.", "AM").replace("p.m.", "PM");
            gameTimingTextView.setText(timeInString);
        } else {
            gameTimingTextView.setText(getContext().getString(R.string.all_day));
        }
        if (mOpportunity.getAddressData() != null) {
            gameAddressTextView.setText(mOpportunity.getAddressData().getAddress());
        } else {
            addressLinearLayout.setVisibility(View.GONE);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM");
        String date = dateFormat.format(mOpportunity.getDate().getTimeInMillis());
        gameDateTextView.setText(date);
    }

    private void setFontsForViews() {
        gameName.setTypeface(HitigerApplication.BOLD);
        confirm.setTypeface(HitigerApplication.BOLD);
        edit.setTypeface(HitigerApplication.SEMI_BOLD);
        gameAddressTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        gameDateTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        gameTimingTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        gamePriceTextView.setTypeface(HitigerApplication.REGULAR);
        reviewOpportunityMessageTextView.setTypeface(HitigerApplication.REGULAR);
        reviewOpportunityTextView.setTypeface(HitigerApplication.SEMI_BOLD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_post:
                mPositiveCallback.onClick(this, 0);
                break;
            case R.id.edit_dialog:
                mNegativeCalback.onClick(this, 0);
                break;
            case R.id.close_button:
                dismiss();
            default:
                break;
        }
        dismiss();
    }
}
