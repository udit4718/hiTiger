package com.hcs.hitiger.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;
import com.hcs.hitiger.activities.HomePageActivity;
import com.hcs.hitiger.adapters.HomeFragmentPagerAdapter;
import com.hcs.hitiger.adapters.OpportunitiesListAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.opportunity.CancelRequestToPlayRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunity;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityRequest;
import com.hcs.hitiger.api.model.opportunity.SendRequestToPlayRequest;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.model.BackPressed;
import com.hcs.hitiger.model.Opportunity;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.NotificationHelper;
import com.hcs.hitiger.util.SportsViewHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anuj gupta on 4/28/16.
 */
public class OpportunityFragment extends Fragment implements BackPressed, HomePageActivity.ReloadEvents {
    @InjectView(R.id.opportunities_list)
    ListView mListView;
    @InjectView(R.id.want_to_play)
    TextView wantToPlay;
    @InjectView(R.id.bottom_layout)
    LinearLayout bottom_layout;
    @InjectView(R.id.bottom_bar_create_opportunity_frame_layout)
    RelativeLayout bottomBarCreateOpportunityConatiner;
    @InjectView(R.id.opportunity_progressbar)
    ProgressBar mProgressBar;
    @InjectView(R.id.empty_opportunity_view)
    TextView emptyView;
    @InjectView(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;

    private Stack<View> bottmBarCreateOppportunityViewStack = new Stack<>();

    private List<OpportunityUiData> mOpportunityUiDataList;
    private ProgressDialog mProgressDialog;
    private View footerView;
    private OpportunitiesListAdapter popsDetailAdapter;
    private Opportunity mOpportunity;
    private UserProfileDetail mUserProfileDetail;

    private List<Sport> mSportList;
    private Sport mSelectedSport;
    private List<Sport> sportsList;
    private boolean isExpanded;


    @OnClick(R.id.want_to_play_layout)
    public void wantToPlay() {
        if (mOpportunity == null) {
            bottmBarCreateOppportunityViewStack.clear();
            bottomBarCreateOpportunityConatiner.setVisibility(View.VISIBLE);
            mOpportunity = new Opportunity();
            if (mSportList == null) {
                mSportList = Sport.getSportList();
            }
            showBottomSportSelectionView(mSportList);
            isExpanded = true;
        } else {
            if (bottomBarCreateOpportunityConatiner.getVisibility() == View.VISIBLE) {
                bottomBarCreateOpportunityConatiner.setVisibility(View.GONE);
                isExpanded = false;
            } else {
                Log.d("TAG", "wantToPlay() returned: " + "in true");
                bottomBarCreateOpportunityConatiner.setVisibility(View.VISIBLE);
                isExpanded = true;
            }
        }

    }
    @OnClick(R.id.floating_action_button)
    void floatingButtonClicked(){
        startActivity(new Intent(getContext(), CreateOpportynityActivity.class));
    }



    private View showBottomSportSelectionView(List<Sport> sportsList) {
        this.sportsList = sportsList;
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_bar_create_opportunity_select_sport, null);
//        final float scale = getResources().getDisplayMetrics().density;
//        int padding_20dp = (int) (20 * scale + 0.5f);
//        view.setPadding(padding_20dp,0,0,0);
        //CustomWrapViewGroup viewGroup = (CustomWrapViewGroup) view.findViewById(R.id.all_sports_container);

        FlexboxLayout viewGroup = (FlexboxLayout) view.findViewById(R.id.all_sports_container);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            View previousSelected = null;

            @Override
            public void onClick(View v) {
                if (previousSelected != null) {
                    ((Sport) previousSelected.getTag()).setLocallySelected(false);
                    previousSelected.setSelected(false);
                }
                previousSelected = v;
                mSelectedSport = (Sport) v.getTag();
                v.setSelected(true);
                mSelectedSport.setLocallySelected(true);
                mOpportunity.setSport(mSelectedSport);
                getOpportunitiesForSportFilter();
                showBottomBarDateSelectionView();
            }
        };
        for (final Sport sport : sportsList) {
            TextView textView = new TextView(getContext());
            textView.setText(sport.getName());
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            SportsViewHelper.setStyleToSporstTetView(textView, params, sport);
            textView.setTextColor(Color.parseColor(sport.getColor()));
            textView.setBackgroundDrawable(SportsViewHelper.getRoundedStrokeColoredDrawable(Color.parseColor(sport.getColor())));
            textView.setOnClickListener(onClickListener);
            textView.setTag(sport);
            textView.setSelected(false);
            viewGroup.addView(textView);
        }
        if (sportsList.size() > 12) {
            setHeightForSportsSelectionOnBottomBar(200);
        }
        bottomBarCreateOpportunityConatiner.removeAllViews();
        bottomBarCreateOpportunityConatiner.addView(view);
        return view;
    }

    private void getOpportunitiesForSportFilter() {
        mProgressBar.setVisibility(View.VISIBLE);
        getApiService().getOpportunities(new GetOpportunityRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId(),
                mUserProfileDetail.getLat(), mUserProfileDetail.getLng(), mOpportunity.getSport().getId()), new Callback<GetOpportunity>() {
            @Override
            public void success(GetOpportunity getOpportunity, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (getOpportunity.isSuccesfull()) {
                    mOpportunityUiDataList = OpportunityUiData.getOpportunityOrEventFullListData(getOpportunity.getOpportunity());
                    updateUserOpportunitiesList();
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void getOpportunitiesForSportAndDateFilter() {
        mProgressBar.setVisibility(View.VISIBLE);
        getApiService().getOpportunities(new GetOpportunityRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId(),
                mUserProfileDetail.getLat(), mUserProfileDetail.getLng(), mOpportunity.getSport().getId(),
                mOpportunity.getDate().getTimeInMillis()), new Callback<GetOpportunity>() {
            @Override
            public void success(GetOpportunity getOpportunity, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (getOpportunity.isSuccesfull()) {
                    mOpportunityUiDataList = OpportunityUiData.getOpportunityOrEventFullListData(getOpportunity.getOpportunity());
                    updateUserOpportunitiesList();
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private View showBottomBarDateSelectionView() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_bar_create_opputunity_select_date, null);
        TextView selectedSportTextView = (TextView) view.findViewById(R.id.sports_name);
        TextView todayDateTextView = (TextView) view.findViewById(R.id.today_date);
        TextView tomorrowDateTextView = (TextView) view.findViewById(R.id.tommorow_date);
        View selectDateView = view.findViewById(R.id.select_date);

        selectedSportTextView.setText(mSelectedSport.getName());
        selectedSportTextView.setBackgroundDrawable(SportsViewHelper.getRoundedColoredDrawable(Color.parseColor(mSelectedSport.getColor())));

        Date now = new Date();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();
        SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM dd");
        String todayMonth = monthDateFormat.format(Calendar.getInstance().getTimeInMillis());
        String tommorowMonth = monthDateFormat.format(tomorrow);
        todayDateTextView.setText("Today | " + todayMonth);
        tomorrowDateTextView.setText("Tommorow | " + tommorowMonth);


        selectedSportTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        todayDateTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        tomorrowDateTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        selectedSportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarCreateOpportunityConatiner.removeAllViews();
                if (sportsList.size() > 12) {
                    setHeightForSportsSelectionOnBottomBar(200);
                }
                bottomBarCreateOpportunityConatiner.addView(bottmBarCreateOppportunityViewStack.pop());
                getOpportunities(mUserProfileDetail.getLat(), mUserProfileDetail.getLng());
            }
        });

        todayDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpportunity.setToday(true);
                getOpportunitiesForSportAndDateFilter();
                showBottomBarCreateOpportunityView();
            }
        });

        tomorrowDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpportunity.setTommorow(true);
                getOpportunitiesForSportAndDateFilter();
                showBottomBarCreateOpportunityView();
            }
        });

        selectDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        mOpportunity.setDate(calendar);
                        mOpportunity.setToday(false);
                        mOpportunity.setTommorow(false);
                        getOpportunitiesForSportAndDateFilter();
                        showBottomBarCreateOpportunityView();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                mCalendar.set(Calendar.DATE, mCalendar.get(Calendar.DATE) + 2);
                datePickerDialog.getDatePicker().setMinDate(mCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

//        if (sportsList.size() > 12) {
//            setHeightForSportsSelectionOnBottomBar();
//        }
        setHeightForSportsSelectionOnBottomBar(50);
        bottmBarCreateOppportunityViewStack.add(bottomBarCreateOpportunityConatiner.getChildAt(0));
        bottomBarCreateOpportunityConatiner.removeAllViews();
        bottomBarCreateOpportunityConatiner.addView(view);
        return view;
    }

    private void showBottomBarCreateOpportunityView() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_bar_create_opportunity, null);
        TextView selectedSportTextView = (TextView) view.findViewById(R.id.sports_name);
        TextView selectedDateTextView = (TextView) view.findViewById(R.id.selected_date);
        // TextView createOpportunityTextView = (TextView) view.findViewById(R.id.create_opportunity_button);

        selectedSportTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        selectedDateTextView.setTypeface(HitigerApplication.SEMI_BOLD);

        selectedSportTextView.setText(mSelectedSport.getName());
        selectedSportTextView.setBackgroundDrawable(SportsViewHelper.getRoundedColoredDrawable(Color.parseColor(mSelectedSport.getColor())));

        final Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMM dd");
        if (mOpportunity.isToday()) {
            selectedDateTextView.setText("Today | " + monthDateFormat.format(cal.getTime()));
        } else if (mOpportunity.isTommorow()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            selectedDateTextView.setText("Tomorrow | " + monthDateFormat.format(cal.getTime()));
        } else {
            selectedDateTextView.setText(monthDateFormat.format(mOpportunity.getDate().getTime()));
        }

        selectedSportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarCreateOpportunityConatiner.removeAllViews();
                bottmBarCreateOppportunityViewStack.pop();
                if (sportsList.size() > 12) {
                    setHeightForSportsSelectionOnBottomBar(200);
                }
                bottomBarCreateOpportunityConatiner.addView(bottmBarCreateOppportunityViewStack.pop());
                getOpportunities(mUserProfileDetail.getLat(), mUserProfileDetail.getLng());
            }
        });

        selectedDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeightForSportsSelectionOnBottomBar(50);
                getOpportunitiesForSportFilter();
                bottomBarCreateOpportunityConatiner.removeAllViews();
                bottomBarCreateOpportunityConatiner.addView(bottmBarCreateOppportunityViewStack.pop());
            }
        });

//        createOpportunityTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), CreateOpportynityActivity.class);
//                intent.putExtra(CreateOpportynityActivity.OPPORTUNITY_DATA, mOpportunity);
//                startActivity(intent);
//                bottomBarCreateOpportunityConatiner.setVisibility(View.GONE);
//                mOpportunity = null;
//            }
//        });

        setHeightForSportsSelectionOnBottomBar(50);
        bottmBarCreateOppportunityViewStack.add(bottomBarCreateOpportunityConatiner.getChildAt(0));
        bottomBarCreateOpportunityConatiner.removeAllViews();
        bottomBarCreateOpportunityConatiner.addView(view);
    }

    private void setHeightForSportsSelectionOnBottomBar(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        bottomBarCreateOpportunityConatiner.getLayoutParams().height = (int) (dp * scale + 0.5f);
    }


    private View.OnClickListener mCLickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), CreateOpportynityActivity.class));
        }
    };

    public static Fragment getInstance() {
        return new OpportunityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserProfileDetail = UserProfileDetail.getUserProfileDetail();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunities(mUserProfileDetail.getLat(), mUserProfileDetail.getLng());
    }

    public void getOpportunities(double latitude, double longitude) {
        mProgressBar.setVisibility(View.VISIBLE);
        getApiService().getOpportunities(new GetOpportunityRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId(),
                latitude, longitude), new Callback<GetOpportunity>() {
            @Override
            public void success(GetOpportunity getOpportunity, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (getOpportunity.isSuccesfull()) {
                    mOpportunityUiDataList = OpportunityUiData.getOpportunityOrEventFullListData(getOpportunity.getOpportunity());
                    updateUserOpportunitiesList();
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void showCustomDialog(final int position) {
       com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog confirmOpportunityCustomDialog = new com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog(
                getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendBroadcastToReload();
                HomePageActivity activity = (HomePageActivity) getActivity();
                activity.viewPager.setCurrentItem(1);
                HomeFragmentPagerAdapter adapter = (HomeFragmentPagerAdapter) activity.viewPager.getAdapter();
                HomePageActivity.ReloadEvents reloadEvents = (HomePageActivity.ReloadEvents) adapter.getItem(1);
                reloadEvents.requestToRefresh();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelRequest(position);
                dialog.dismiss();
            }
        }, mOpportunityUiDataList.get(position), null);
        confirmOpportunityCustomDialog.show();
    }

    public void cancelRequest(final int position) {
        mProgressBar.setVisibility(View.VISIBLE);
        getApiService().cancelRequest(new CancelRequestToPlayRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId(), mOpportunityUiDataList.get(position).getId(),
                mUserProfileDetail.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (hitigerResponse.isSuccesfull()) {
                    popsDetailAdapter.cancelRequestChanges(position);
                    sendBroadcastToReload();
                    Toast.makeText(getContext(), "Cancel Request", Toast.LENGTH_LONG).show();
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    public void sendRequest(final int position) {
        mProgressBar.setVisibility(View.VISIBLE);
        getApiService().sendRequest(new SendRequestToPlayRequest(mUserProfileDetail.getFbId(), mOpportunityUiDataList.get(position).getId(),
                mUserProfileDetail.getId()), new Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (hitigerResponse.isSuccesfull()) {
                    showCustomDialog(position);
                    popsDetailAdapter.sendRequestChanges(position);
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void updateUserOpportunitiesList() {
        if (mOpportunityUiDataList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        if (popsDetailAdapter == null) {
            popsDetailAdapter = new OpportunitiesListAdapter(getActivity(), 0, this);
            mListView.setAdapter(popsDetailAdapter);
        }
        int index = mListView.getFirstVisiblePosition();
        View v = mListView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - mListView.getPaddingTop());
        popsDetailAdapter.setData(mOpportunityUiDataList);
        popsDetailAdapter.notifyDataSetChanged();
        mListView.setSelectionFromTop(index, top);
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.opportunities_layout, container, false);
        ButterKnife.inject(this, view);

        addFooter();

        wantToPlay.setTypeface(HitigerApplication.BOLD);
        emptyView.setTypeface(HitigerApplication.BOLD);
        return view;
    }

    private void addFooter() {
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.opportunity_footer_view, null);
        TextView createNewOpportunity = (TextView) footerView.findViewById(R.id.create_opportunity_button);
        TextView createOpportunityMessage = (TextView) footerView.findViewById(R.id.create_opporotunity_message);

        createNewOpportunity.setOnClickListener(mCLickLisner);
        createNewOpportunity.setTypeface(HitigerApplication.BOLD);
        createOpportunityMessage.setTypeface(HitigerApplication.SEMI_BOLD);
        mListView.addFooterView(footerView, null, false);
    }

    protected void showProgressDialog(String message) {
        dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(getContext(), "", message, true, false);
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && !getActivity().isFinishing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public boolean canBackPress() {
        return isExpanded;
    }

    @Override
    public void backPressed() {
        wantToPlay();
    }

    @Override
    public void requestToRefresh() {
        getOpportunities(mUserProfileDetail.getLat(), mUserProfileDetail.getLng());
    }

    private final void sendBroadcastToReload(){
        Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
        getContext().sendBroadcast(broadcastIntent);
    }
}
