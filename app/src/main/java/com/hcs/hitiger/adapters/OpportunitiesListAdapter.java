package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.EventDetailActivity;
import com.hcs.hitiger.fragments.OpportunityFragment;
import com.hcs.hitiger.model.OpportunityUiData;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 4/28/16.
 */
public class OpportunitiesListAdapter extends ArrayAdapter<OpportunityUiData> {
    private List<OpportunityUiData> mOpportunityUiDataList = new ArrayList<>();
    private OpportunityFragment opportunityFragment;
    private OpportunitiesViewHolder opportunitiesViewHolder;

    public OpportunitiesListAdapter(Activity activity, int resource, OpportunityFragment opportunityFragment) {
        super(activity, resource);
        this.opportunityFragment = opportunityFragment;
    }

    @Override
    public int getCount() {
        return mOpportunityUiDataList == null ? 0 : mOpportunityUiDataList.size();
    }

    public void setData(List<OpportunityUiData> opportunityUiDataList) {
        this.mOpportunityUiDataList = opportunityUiDataList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.opportunities_list_layout, parent, false);
            opportunitiesViewHolder = new OpportunitiesViewHolder(convertView);
            convertView.setTag(opportunitiesViewHolder);
        } else {
            opportunitiesViewHolder = (OpportunitiesViewHolder) convertView.getTag();
        }

        setDataToViews(opportunitiesViewHolder, position);
        setChangesForButtons(position);

        opportunitiesViewHolder.cancelRequestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opportunityFragment.cancelRequest(position);
            }
        });

        opportunitiesViewHolder.letsPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opportunityFragment.sendRequest(position);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EventDetailActivity.class);
                intent.putExtra(EventDetailActivity.OPPORTUNITY_ID, mOpportunityUiDataList.get(position).getId());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    public void sendRequestChanges(int position) {
        mOpportunityUiDataList.get(position).setCancelRequest(true);
        setChangesForButtons(position);
    }

    private void setChangesForButtons(int position) {
        if (mOpportunityUiDataList.get(position).isCancelRequest()) {
            opportunitiesViewHolder.letsPlayButton.setVisibility(View.GONE);
            opportunitiesViewHolder.requestingLayout.setVisibility(View.VISIBLE);
        } else {
            opportunitiesViewHolder.letsPlayButton.setVisibility(View.VISIBLE);
            opportunitiesViewHolder.requestingLayout.setVisibility(View.GONE);
        }
    }

    public void cancelRequestChanges(int position) {
        mOpportunityUiDataList.get(position).setCancelRequest(false);
        setChangesForButtons(position);
    }

    private void setDataToViews(OpportunitiesViewHolder opportunitiesViewHolder, int position) {
        opportunitiesViewHolder.userName.setText(mOpportunityUiDataList.get(position).getUser().getName());

        if (mOpportunityUiDataList.get(position).getPrice() != 0) {
            opportunitiesViewHolder.gamePrice.setText(mOpportunityUiDataList.get(position).getPrice() + "");
            opportunitiesViewHolder.gamePrice.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), R.drawable.rupee), null, null, null);
            opportunitiesViewHolder.gamePrice.setCompoundDrawablePadding(4);
        } else {
            opportunitiesViewHolder.gamePrice.setText(getContext().getString(R.string.free));
            opportunitiesViewHolder.gamePrice.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        Picasso.with(getContext()).load(mOpportunityUiDataList.get(position).getClub().getImageUrl()).into(opportunitiesViewHolder.gameImage);
        opportunitiesViewHolder.gameName.setText(mOpportunityUiDataList.get(position).getClub().getName());
        int defaultColor = ContextCompat.getColor(getContext(), R.color.blue);
        opportunitiesViewHolder.gameName.setTextColor(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{android.R.attr.state_pressed}, new int[]{}},
                new int[]{defaultColor, defaultColor, Color.parseColor(mOpportunityUiDataList.get(position).getClub().getColor())}));

        if (mOpportunityUiDataList.get(position).getTime() != -1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
            String timeInString = timeFormat.format(mOpportunityUiDataList.get(position).getTime()).replace("a.m.", "AM").replace("p.m.", "PM");
            opportunitiesViewHolder.gameTiming.setText(timeInString);
        } else {
            opportunitiesViewHolder.gameTiming.setText(getContext().getResources().getString(R.string.all_day));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM");
        String date = dateFormat.format(mOpportunityUiDataList.get(position).getDate());
        opportunitiesViewHolder.gameDate.setText(date);

        if (mOpportunityUiDataList.get(position).getVenue() != null)
            opportunitiesViewHolder.gameAddress.setText(mOpportunityUiDataList.get(position).getVenue().getAddress());
        else
            opportunitiesViewHolder.gameAddress.setText("");


        if (mOpportunityUiDataList.get(position).getUser().getImageUrl() != null) {
            Picasso.with(getContext()).load(mOpportunityUiDataList.get(position).getUser().getImageUrl())
                    .into(opportunitiesViewHolder.userProfileImage);
        } else {
            opportunitiesViewHolder.userProfileImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default_image));
        }

        Spannable gamePlayedSpan = new SpannableString(mOpportunityUiDataList.get(position).getGamesPlayed() + " Game Played");
        gamePlayedSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)),
                0, gamePlayedSpan.length() - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        opportunitiesViewHolder.numberOfGamePlayedByUser.setText(gamePlayedSpan);
    }

    static class OpportunitiesViewHolder {
        @InjectView(R.id.game_image)
        ImageView gameImage;
        @InjectView(R.id.game_name)
        TextView gameName;
        @InjectView(R.id.game_price)
        TextView gamePrice;
        @InjectView(R.id.game_timing)
        TextView gameTiming;
        @InjectView(R.id.game_date)
        TextView gameDate;
        @InjectView(R.id.game_address)
        TextView gameAddress;
        @InjectView(R.id.user_name)
        TextView userName;
        @InjectView(R.id.number_of_game_played_by_user)
        TextView numberOfGamePlayedByUser;
        @InjectView(R.id.user_profile_image)
        ImageView userProfileImage;
        @InjectView(R.id.lets_play_button)
        TextView letsPlayButton;
        @InjectView(R.id.requesting_layout)
        RelativeLayout requestingLayout;
        @InjectView(R.id.cancel_request)
        TextView cancelRequestView;

        public OpportunitiesViewHolder(View view) {
            ButterKnife.inject(this, view);
            setFontType();
        }

        private void setFontType() {
            gameName.setTypeface(HitigerApplication.SEMI_BOLD);
            gameTiming.setTypeface(HitigerApplication.SEMI_BOLD);
            gameDate.setTypeface(HitigerApplication.SEMI_BOLD);
            gameAddress.setTypeface(HitigerApplication.SEMI_BOLD);
            gamePrice.setTypeface(HitigerApplication.REGULAR);
            userName.setTypeface(HitigerApplication.SEMI_BOLD);
            numberOfGamePlayedByUser.setTypeface(HitigerApplication.REGULAR);
            letsPlayButton.setTypeface(HitigerApplication.BOLD);
        }
    }
}
