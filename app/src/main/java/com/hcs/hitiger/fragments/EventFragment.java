package com.hcs.hitiger.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.HomePageActivity;
import com.hcs.hitiger.adapters.EventListAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.opportunity.EventResponse;
import com.hcs.hitiger.api.model.opportunity.OpportunityApiResponse;
import com.hcs.hitiger.model.BackPressed;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.UserProfileDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anuj gupta on 5/12/16.
 */

public class EventFragment extends Fragment implements BackPressed, HomePageActivity.ReloadEvents {
    @InjectView(R.id.event_list)
    ListView eventListView;
    @InjectView(R.id.event_progressbar)
    ProgressBar mProgressBar;
    @InjectView(R.id.empty_view)
    TextView emptyView;

    private UserProfileDetail mUserProfileDetail;
    private EventListAdapter eventListAdapter;

    public static Fragment getInstance() {
        return new EventFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserProfileDetail = UserProfileDetail.getUserProfileDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getEvents();
    }


    private void getEvents() {
        mProgressBar.setVisibility(View.VISIBLE);
        getApiService().getEvents(new HitigerRequest(mUserProfileDetail.getFbId(), mUserProfileDetail.getId()), new Callback<EventResponse>() {
            @Override
            public void success(EventResponse eventResponse, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (eventResponse.isSuccesfull()) {
                    addingDataInListView(eventResponse.getEvents());
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private static final Comparator<OpportunityUiData> comparator = new Comparator<OpportunityUiData>() {
        @Override
        public int compare(OpportunityUiData lhs, OpportunityUiData rhs) {
            if (lhs.getDate() != rhs.getDate()) {
                return Long.compare(lhs.getDate(), rhs.getDate());
            }
            return Long.compare(lhs.getTime(), rhs.getTime());
        }
    };

    private static final Comparator<OpportunityUiData> comparatorForReversingList = new Comparator<OpportunityUiData>() {
        @Override
        public int compare(OpportunityUiData lhs, OpportunityUiData rhs) {
            if (lhs.getDate() != rhs.getDate()) {
                return Long.compare(rhs.getDate(), lhs.getDate());
            }
            return Long.compare(lhs.getTime(), rhs.getTime());
        }
    };

    private void addingDataInListView(List<OpportunityApiResponse> opportunityApiResponseList) {
        if (opportunityApiResponseList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            List<OpportunityUiData> opportunitiesDataList = (OpportunityUiData.getOpportunityOrEventFullListData(opportunityApiResponseList));
            Collections.sort(opportunitiesDataList, comparator);
            int i;
            for (i = 0; i < opportunitiesDataList.size(); i++) {
                if (opportunitiesDataList.get(i).getDate() == getCurrentDate()) {
                    break;
                }
            }

            List<OpportunityUiData> list = opportunitiesDataList.subList(i, opportunitiesDataList.size());
            List<OpportunityUiData> finalList = new ArrayList<>();
            finalList.addAll(list);
            list.clear();
            list = opportunitiesDataList.subList(0, i);
            Collections.sort(list, comparatorForReversingList);
            finalList.addAll(list);

            if (eventListAdapter == null) {
                eventListAdapter = new EventListAdapter(getContext(), 0, mUserProfileDetail.getId());
                eventListView.setAdapter(eventListAdapter);
            }
            int index = eventListView.getFirstVisiblePosition();
            View v = eventListView.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - eventListView.getPaddingTop());
            eventListAdapter.setData(finalList);
            eventListAdapter.notifyDataSetChanged();
            eventListView.setSelectionFromTop(index, top);
        }
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }


    public void requestToRefresh() {
        getEvents();
    }

    public boolean canBackPress() {
        return false;
    }

    @Override
    public void backPressed() {

    }


    long getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

}
