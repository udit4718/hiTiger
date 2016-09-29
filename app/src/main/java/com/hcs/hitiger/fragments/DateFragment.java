package com.hcs.hitiger.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DateFragment extends android.app.Fragment {
    @InjectView(R.id.fragment_image)
    ImageView fragmentImage;
    @InjectView(R.id.select_fragment_message)
    TextView fragmentTextMessage;
    @InjectView(R.id.tommorow_date)
    TextView tommorowDateTextView;
    @InjectView(R.id.fragment_button)
    TextView fragmentButton;
    @InjectView(R.id.date_layout)
    LinearLayout dateLayout;
    @InjectView(R.id.today_date)
    TextView todayDateTextView;
    @InjectView(R.id.or)
    TextView orButton;
    private Calendar mCalendar;
    private GetOpportunityInterface getOpportunityInterface;

    private View.OnClickListener mButtonClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDateDialog();
        }
    };

    private View.OnClickListener mTodayClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beforeChangesForButton();
            getOpportunityInterface.getOpportunity().setDate(null);
            getOpportunityInterface.getOpportunity().setTommorow(false);
            getOpportunityInterface.getOpportunity().setToday(true);
            afterChengesForTodayButton();
        }
    };
    private View.OnClickListener mTommorowClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beforeChangesForButton();
            getOpportunityInterface.getOpportunity().setDate(null);
            getOpportunityInterface.getOpportunity().setTommorow(true);
            getOpportunityInterface.getOpportunity().setToday(false);
            afterChengesForTommorowButton();
        }
    };

    public static DateFragment getInstance() {
        return new DateFragment();
    }

    private void afterChengesForTodayButton() {
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        mCalendar = Calendar.getInstance();
        getOpportunityInterface.getOpportunity().setDate(mCalendar);
        todayDateTextView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        todayDateTextView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.yellow_rounded_rectangular_background));
    }

    private void afterChengesForTommorowButton() {
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        mCalendar = cal;
        getOpportunityInterface.getOpportunity().setDate(mCalendar);
        tommorowDateTextView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        tommorowDateTextView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.yellow_rounded_rectangular_background));
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

        initializeData();
        setFonts();

        fragmentButton.setOnClickListener(mButtonClickLisner);
        todayDateTextView.setOnClickListener(mTodayClickLisner);
        tommorowDateTextView.setOnClickListener(mTommorowClickLisner);

        return view;
    }

    private void initializeData() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();

        dateLayout.setVisibility(View.VISIBLE);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM dd");
        String today_month = month_date.format(Calendar.getInstance().getTimeInMillis());
        String tommorowMonth = month_date.format(tomorrow);

        todayDateTextView.setText("Today | " + today_month);
        tommorowDateTextView.setText("Tommorow | " + tommorowMonth);

        fragmentImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.trophy));
        fragmentTextMessage.setText(getActivity().getString(R.string.select_date));
    }

    private void setFonts() {
        fragmentButton.setTypeface(HitigerApplication.SEMI_BOLD);
        fragmentTextMessage.setTypeface(HitigerApplication.REGULAR);
        orButton.setTypeface(HitigerApplication.EXTRA_BOLD);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunityInterface = (GetOpportunityInterface) getActivity();
        mCalendar = getOpportunityInterface.getOpportunity().getDate();
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        if (getOpportunityInterface.getOpportunity().isToday()) {
            afterChengesForTodayButton();
        } else if (getOpportunityInterface.getOpportunity().isTommorow()) {
            afterChengesForTommorowButton();
        } else if (getOpportunityInterface.getOpportunity().getDate() != null) {
            setDataAfterDateChanges();
        } else {
            beforeChangesForButton();
        }
    }

    private void beforeChangesForButton() {
        getOpportunityInterface.getOpportunity().setDate(null);
        getOpportunityInterface.getOpportunity().setTommorow(false);
        getOpportunityInterface.getOpportunity().setToday(false);

        tommorowDateTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        tommorowDateTextView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_grey_border));

        todayDateTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        todayDateTextView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_grey_border));
        tommorowDateTextView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_border));

        todayDateTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        todayDateTextView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_border));

        fragmentButton.setText(getActivity().getResources().getString(R.string.select_date));
        fragmentButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.calendar), null, null, null);
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorGrey));
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_background_with_rounded_rectangular_grey_border));
    }

    private void showDateDialog() {

        Calendar mCal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                mCalendar = calendar;
                setDataAfterDateChanges();
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        mCal.set(Calendar.DATE, mCal.get(Calendar.DATE) + 2);
        datePickerDialog.getDatePicker().setMinDate(mCal.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setDataAfterDateChanges() {
        beforeChangesForButton();

        SimpleDateFormat month_date = new SimpleDateFormat("MMM ");
        String month_name = month_date.format(mCalendar.getTime());
        ((CreateOpportynityActivity) getActivity()).setNextEnabled();
        getOpportunityInterface.getOpportunity().setDate(mCalendar);
        fragmentButton.setText(month_name + mCalendar.get(Calendar.DAY_OF_MONTH));
        fragmentButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.soi_calendar2), null, null, null);
        fragmentButton.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        fragmentButton.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.yellow_rounded_rectangular_background));
    }


}
