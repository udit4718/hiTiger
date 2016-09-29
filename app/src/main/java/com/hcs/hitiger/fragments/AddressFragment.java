package com.hcs.hitiger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;
import com.hcs.hitiger.activities.YourVenueOrSelectAddressActivity;
import com.hcs.hitiger.model.AddressData;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddressFragment extends android.app.Fragment {
    public static final int REQUSET_CODE = 12345;
    public static final String SELECT_FOR_ADDRESS = "SELECT_FOR_ADDRESS";
    public static final String ADDRESS = "ADDRESS";

    @InjectView(R.id.fragment_option_layout)
    RelativeLayout optionLayout;
    @InjectView(R.id.center_text)
    TextView addressTextView;
    @InjectView(R.id.place_text_message)
    TextView placeMessage;
    @InjectView(R.id.place_no)
    TextView noButton;
    @InjectView(R.id.place_yes)
    TextView yesButton;
    @InjectView(R.id.message_frame)
    FrameLayout messageFrameLayout;
    @InjectView(R.id.left_image)
    ImageView verifiedImage;
    @InjectView(R.id.right_image)
    ImageView editAddress;

    @OnClick(R.id.right_image)
    public void getAddress() {
        Intent intent = new Intent(getActivity(), YourVenueOrSelectAddressActivity.class);
        intent.putExtra(SELECT_FOR_ADDRESS, true);
        startActivityForResult(intent, REQUSET_CODE);
    }

    private GetOpportunityInterface getOpportunityInterface;

    private View.OnClickListener mYesClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getAddress();
        }
    };

    private View.OnClickListener mNoClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((CreateOpportynityActivity) getActivity()).setNextEnabled();
            ((CreateOpportynityActivity) getActivity()).clickNext();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == REQUSET_CODE && data != null) {
            AddressData addressData = data.getParcelableExtra(ADDRESS);
            getOpportunityInterface.getOpportunity().setAddress(addressData);
            setResultedData();
        }
    }

    private void setResultedData() {
        optionLayout.setVisibility(View.GONE);
        verifiedImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.verified_black));
        editAddress.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_edit_string_24dp));
        addressTextView.setText(getOpportunityInterface.getOpportunity().getAddressData().getAddress());
        messageFrameLayout.setVisibility(View.VISIBLE);
        placeMessage.setText(getActivity().getResources().getString(R.string.address_for_opportunity));
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
    }

    public static AddressFragment getInstance() {
        AddressFragment fragment = new AddressFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunityInterface = (GetOpportunityInterface) getActivity();

        ((CreateOpportynityActivity) getActivity()).setNextDisable();
        if (getOpportunityInterface.getOpportunity().getAddressData() != null) {
            setResultedData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        ButterKnife.inject(this, view);

        setFonts();

        placeMessage.setText(getActivity().getResources().getString(R.string.do_you_have_opportunity));

        yesButton.setOnClickListener(mYesClickLisner);
        noButton.setOnClickListener(mNoClickLisner);

        return view;
    }

    private void setFonts() {
        placeMessage.setTypeface(HitigerApplication.SEMI_BOLD);
        noButton.setTypeface(HitigerApplication.SEMI_BOLD);
        yesButton.setTypeface(HitigerApplication.SEMI_BOLD);
        addressTextView.setTypeface(HitigerApplication.REGULAR);
    }
}
