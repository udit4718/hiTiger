package com.hcs.hitiger.view.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.model.OpportunityUiData;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 4/27/16.
 */
public class ConfirmOpportunityCustomDialog extends Dialog implements OnClickListener {

    @InjectView(R.id.confirm_post)
    TextView confirm;
    @InjectView(R.id.edit_dialog)
    TextView edit;
    @InjectView(R.id.close_button)
    ImageView closeButton;
    @InjectView(R.id.request_send_to_message)
    TextView requestSendToMessageTextVeiw;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.dialog_header_message)
    TextView dialogHeaderMessage;
    @InjectView(R.id.other_user_profile_image)
    ImageView userProfileImageView;

    private final OnClickListener mPositiveCallback;
    private final OnClickListener mNegativeCalback;
    private final OpportunityUiData mOpportunityUiData;
    private final UserApiResponse otherUser;

    public ConfirmOpportunityCustomDialog(Context context, OnClickListener positiveCallback, OnClickListener negativeCalback,
                                          OpportunityUiData opportunityUiData, UserApiResponse otherUser) {
        super(context);
        mPositiveCallback = positiveCallback;
        mNegativeCalback = negativeCalback;
        mOpportunityUiData = opportunityUiData;
        this.otherUser = otherUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_opportunity_dialog);
        ButterKnife.inject(this);

        setFontsForViews();
        setDataInViews();

        confirm.setOnClickListener(this);
        edit.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    private void setDataInViews() {
        if (otherUser == null) {
            userName.setText(mOpportunityUiData.getUser().getName());
            requestSendToMessageTextVeiw.setText("Your " + mOpportunityUiData.getClub().getName() + " Match Request sent to");
            if (mOpportunityUiData.getUser().getImageUrl() != null)
                Picasso.with(getContext()).load(mOpportunityUiData.getUser().getImageUrl()).into(userProfileImageView);

            confirm.setText(getContext().getResources().getString(R.string.okey));
            edit.setText(getContext().getResources().getString(R.string.cancel_request));
        } else {
            edit.setVisibility(View.GONE);

            userName.setText(otherUser.getName());
            if (otherUser.getImageUrl() != null)
                Picasso.with(getContext()).load(otherUser.getImageUrl()).into(userProfileImageView);

            confirm.setText(getContext().getResources().getString(R.string.share_with_friends));
            confirm.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.share), null, null, null);
            confirm.setMarqueeRepeatLimit(20);
            confirm.setPadding(24, 8, 24, 8);
            requestSendToMessageTextVeiw.setText("Your " + mOpportunityUiData.getClub().getName() + " Match fix with");
        }
        dialogHeaderMessage.setText(getContext().getResources().getString(R.string.congratulation));
    }

    private void setFontsForViews() {
        confirm.setTypeface(HitigerApplication.BOLD);
        edit.setTypeface(HitigerApplication.REGULAR);
        requestSendToMessageTextVeiw.setTypeface(HitigerApplication.REGULAR);
        userName.setTypeface(HitigerApplication.BOLD);
        dialogHeaderMessage.setTypeface(HitigerApplication.REGULAR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_post:
                mPositiveCallback.onClick(this, 0);
                break;
            case R.id.edit_dialog:
                if (otherUser == null)
                    mNegativeCalback.onClick(this, 0);
                break;
            case R.id.close_button:
                if (otherUser != null) {
                    mNegativeCalback.onClick(this, 0);
                } else
                    dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
