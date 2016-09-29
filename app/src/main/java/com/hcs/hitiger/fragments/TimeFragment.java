package com.hcs.hitiger.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimeFragment extends android.app.Fragment {

    @InjectView(R.id.fragment_image)
    ImageView fragmentImage;
    @InjectView(R.id.select_fragment_message)
    TextView fragmentTextMessage;
    @InjectView(R.id.fragment_button)
    TextView fragmentButton;
    @InjectView(R.id.time_or_amount)
    TextView time;
    @InjectView(R.id.or)
    TextView orButton;

    private Calendar mCalendar;

    private View.OnClickListener mTimeClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTimePickerDialog();
        }
    };

    private View.OnClickListener mButtonClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            afterchangesForButton();
        }
    };
    private GetOpportunityInterface getOpportunityInterface;

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                if (getOpportunityInterface.getOpportunity().isToday()) {
                    if (hourOfDay < calendar.get(Calendar.HOUR_OF_DAY) || (hourOfDay == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE))) {
                        Toast.makeText(getActivity(), "Oops too Late !!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                calendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                if ((mCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) || mCalendar.get(Calendar.HOUR_OF_DAY) != calendar.HOUR_OF_DAY) {
                    mCalendar = calendar;
                    afterchangesInTime();
                }

            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void afterchangesInTime() {
        beforeChangesForButton();
        getOpportunityInterface.getOpportunity().setAllDay(false);
        getOpportunityInterface.getOpportunity().setTime(mCalendar);
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm-aaa");
        String timeInString = timeFormat.format(mCalendar.getTime()).replace("a.m.", "AM").replace("p.m.", "PM");
        time.setText(timeInString);
        time.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
    }

    public static TimeFragment getInstance() {
        TimeFragment fragment = new TimeFragment();
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

        initialiseTime();
        setFontes();

        time.setOnClickListener(mTimeClickLisner);
        fragmentButton.setOnClickListener(mButtonClickLisner);
        return view;
    }

    private void setFontes() {
        fragmentButton.setTypeface(HitigerApplication.SEMI_BOLD);
        fragmentTextMessage.setTypeface(HitigerApplication.REGULAR);
        orButton.setTypeface(HitigerApplication.EXTRA_BOLD);
        time.setTypeface(HitigerApplication.BOLD);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunityInterface = (GetOpportunityInterface) getActivity();

        mCalendar = getOpportunityInterface.getOpportunity().getTime();
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        if (getOpportunityInterface.getOpportunity().isAllDay()) {
            afterchangesForButton();
        } else if (getOpportunityInterface.getOpportunity().getTime() != null && !getOpportunityInterface.getOpportunity().isAllDay()) {
            afterchangesInTime();
        }
    }

    private void initialiseTime() {
        fragmentImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.watch1));
        fragmentTextMessage.setText(getActivity().getString(R.string.enter_time));
        fragmentButton.setText(getActivity().getString(R.string.all_day));
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        fragmentButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_grey_border));
        time.setVisibility(View.VISIBLE);

        beforeChangesForButton();
        beforeChangesInTime();
    }

    private void beforeChangesInTime() {
        time.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        time.setText(getActivity().getResources().getString(R.string.select_time));
    }

    private void afterchangesForButton() {
        beforeChangesInTime();
        getOpportunityInterface.getOpportunity().setAllDay(true);
        getOpportunityInterface.getOpportunity().setTime(null);
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.yellow_rounded_rectangular_background));
    }

    private void beforeChangesForButton() {
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_grey_border));
    }
}
