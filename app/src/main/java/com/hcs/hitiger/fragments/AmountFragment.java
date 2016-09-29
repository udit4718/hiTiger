package com.hcs.hitiger.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AmountFragment extends android.app.Fragment {
    @InjectView(R.id.fragment_image)
    ImageView fragmentImage;
    @InjectView(R.id.select_fragment_message)
    TextView fragmentTextMessage;
    @InjectView(R.id.fragment_button)
    TextView fragmentButton;
    @InjectView(R.id.amount_edit_text)
    EditText amountEditText;
    @InjectView(R.id.or)
    TextView orButton;

    private View.OnClickListener mButtonClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fragmentButtonAfterCahnges();
        }
    };

    private TextWatcher mEditTextChangeLisner = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().isEmpty()) {
                setTextAfterchanges(s.toString());
            } else if (!getOpportunityInterface.getOpportunity().isFree()) {
                ((CreateOpportynityActivity) getActivity()).setNextDisable();
                getOpportunityInterface.getOpportunity().setFree(false);
                amountEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightGrey));
            }
        }
    };
    private GetOpportunityInterface getOpportunityInterface;

    private void setTextAfterchanges(String data) {
        getOpportunityInterface.getOpportunity().setFree(false);
        getOpportunityInterface.getOpportunity().setAmount(data);
        amountEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        fragmentButtonBeforeChanges();
    }

    private void fragmentButtonAfterCahnges() {
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        getOpportunityInterface.getOpportunity().setFree(true);
        getOpportunityInterface.getOpportunity().setAmount(null);
        amountEditText.setText("");
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.yellow_rounded_rectangular_background));
    }

    public static AmountFragment getInstance() {
        AmountFragment fragment = new AmountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_opportunity_common_fragment, container, false);

        ButterKnife.inject(this, view);

        setFonts();
        initializeData();

        fragmentButtonBeforeChanges();

        amountEditText.addTextChangedListener(mEditTextChangeLisner);
        fragmentButton.setOnClickListener(mButtonClickLisner);

        return view;
    }

    private void initializeData() {
        amountEditText.setVisibility(View.VISIBLE);
        fragmentImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.rupees));
        fragmentTextMessage.setText(getActivity().getString(R.string.any_change));

        fragmentButton.setText(getActivity().getString(R.string.free));
        fragmentButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private void setFonts() {
        orButton.setTypeface(HitigerApplication.EXTRA_BOLD);
        amountEditText.setTypeface(HitigerApplication.BOLD);
        fragmentButton.setTypeface(HitigerApplication.SEMI_BOLD);
        fragmentTextMessage.setTypeface(HitigerApplication.REGULAR);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunityInterface = (GetOpportunityInterface) getActivity();

        if (getOpportunityInterface.getOpportunity().getAmount() != null &&
                !(getOpportunityInterface.getOpportunity().getAmount().equals("0"))) {
            amountEditText.setText(getOpportunityInterface.getOpportunity().getAmount());
        } else if (getOpportunityInterface.getOpportunity().isFree()) {
            fragmentButtonAfterCahnges();
        }
    }

    private void fragmentButtonBeforeChanges() {
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_grey_border));
    }
}
