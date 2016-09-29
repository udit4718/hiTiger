package com.hcs.hitiger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.SportsViewHelper;


import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SportsFragment extends android.app.Fragment {
    @InjectView(R.id.all_sports_list)
    FlexboxLayout sportsViewContainer;

    private static final String USER_PROFILE_DETAIL = "USER_PROFILE_DETAIL";
    private UserProfileDetail userProfileDetail;
    private TextView previousSelectedSports;
    private GetOpportunityInterface getOpportunityInterface;
    private List<Sport> sportsList;

    public static SportsFragment getInstance(UserProfileDetail userProfileDetail) {
        SportsFragment fragment = new SportsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_PROFILE_DETAIL, userProfileDetail);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sports, container, false);
        userProfileDetail = getArguments().getParcelable(USER_PROFILE_DETAIL);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunityInterface = (GetOpportunityInterface) getActivity();
        if (sportsList == null)
            sportsList = Sport.getSportList();
        createUiForSportsSelection();
    }

    private void createUiForSportsSelection() {
        sportsViewContainer.removeAllViews();
        if (sportsList.size() == 0) {
            sportsViewContainer.setVisibility(View.GONE);
            return;
        } else {
            sportsViewContainer.setVisibility(View.VISIBLE);
        }
        for (final Sport sport : sportsList) {
            TextView textView = new TextView(getActivity());
            textView.setText(sport.getName());
            sport.setAdded(false);
            if (getOpportunityInterface.getOpportunity().getSport() != null && getOpportunityInterface.getOpportunity().getSport().getId() == sport.getId()) {
                previousSelectedSports = textView;
                sport.setAdded(true);
            }
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            SportsViewHelper.setStyleToSporstTetView(textView, params, sport);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sport selectedSport = (Sport) v.getTag();
                    if (previousSelectedSports == null) {
                        previousSelectedSports = (TextView) v;
                        previousSelectedSports.setSelected(true);
                    } else {
                        previousSelectedSports.setSelected(false);
                        previousSelectedSports = (TextView) v;
                        previousSelectedSports.setSelected(true);
                    }
                    ((CreateOpportynityActivity) getActivity()).setNextEnabled();
                    getOpportunityInterface.getOpportunity().setSport(selectedSport);
                }
            });
            textView.setTag(sport);
            sportsViewContainer.addView(textView);
        }
    }
}
