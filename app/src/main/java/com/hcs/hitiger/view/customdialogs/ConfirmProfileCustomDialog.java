package com.hcs.hitiger.view.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 4/27/16.
 */
public class ConfirmProfileCustomDialog extends Dialog implements OnClickListener {

    @InjectView(R.id.confirm_post)
    TextView confirm;
    @InjectView(R.id.edit_dialog)
    TextView edit;
    @InjectView(R.id.confirmation_message)
    TextView confirmationMessage;
    @InjectView(R.id.notification_message)
    TextView notificationMEssage;
    @InjectView(R.id.contact_number)
    TextView contactNumber;

    private final DialogInterface.OnClickListener mPositiveCallback;
    private final DialogInterface.OnClickListener mNegativeCalback;
    private final String mPhoneNumber;

    public ConfirmProfileCustomDialog(Context context, DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCalback, String phoneNumber) {
        super(context);
        mPositiveCallback = positiveCallback;
        mNegativeCalback = negativeCalback;
        mPhoneNumber = phoneNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        ButterKnife.inject(this);

        setFontsForViews();
        contactNumber.setText("+91 " + mPhoneNumber);
        confirm.setText(getContext().getResources().getString(R.string.update));
        confirm.setOnClickListener(this);
        edit.setOnClickListener(this);
    }

    private void setFontsForViews() {
        confirm.setTypeface(HitigerApplication.BOLD);
        edit.setTypeface(HitigerApplication.BOLD);
        confirmationMessage.setTypeface(HitigerApplication.REGULAR);
        notificationMEssage.setTypeface(HitigerApplication.REGULAR);
        contactNumber.setTypeface(HitigerApplication.BOLD);
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
            default:
                break;
        }
        dismiss();
    }
}
