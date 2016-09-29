package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.ChatActivity;
import com.hcs.hitiger.activities.FollowersOrPlayedWithActivity;
import com.hcs.hitiger.activities.OthersProfileActivity;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.UserProfileDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 5/19/16.
 */
public class RequestersAdapter extends ArrayAdapter<UserApiResponse> {
    private  OpportunityUiData opportunity;

    private List<UserApiResponse> users;

    public RequestersAdapter(Context context, int resource, OpportunityUiData opportunity) {
        super(context, resource);
        this.opportunity = opportunity;
    }

    public void setUsers(List<UserApiResponse> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users != null ? users.size() : 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RequesterViewHolder requesterViewHolder;
        final UserApiResponse user = users.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.opportunity_detail_user_layout, parent, false);
            requesterViewHolder = new RequesterViewHolder(convertView);
            convertView.setTag(requesterViewHolder);
        } else {
            requesterViewHolder = (RequesterViewHolder) convertView.getTag();
        }

        if (user.getImageUrl() == null) {
            requesterViewHolder.userProfileImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default_image));
        } else {
            Picasso.with(getContext()).load(user.getImageUrl())
                    .into(requesterViewHolder.userProfileImageView);
        }

        Spannable gamePlayedSpan = new SpannableString(user.getGamesPlayed() + " Game Played");
        gamePlayedSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)),
                0, gamePlayedSpan.length() - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        requesterViewHolder.numberOfGamePlayedTextView.setText(gamePlayedSpan);

        requesterViewHolder.userNameTextView.setText(user.getName());

        Spannable followersSpan = new SpannableString(user.getFollowersCount() + " Followers");
        followersSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)),
                0, followersSpan.length() - 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        requesterViewHolder.numberOfFollowersView.setText(followersSpan);


        requesterViewHolder.numberOfFollowersView.setVisibility(View.VISIBLE);
        requesterViewHolder.messageImage.setVisibility(View.VISIBLE);

        requesterViewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(ChatActivity.OPPORTUNITY, opportunity);
                intent.putExtra(ChatActivity.RECEIPENT_USER_ID, user.getId());
                intent.putExtra(ChatActivity.RECEIPENT_USER_NAME, user.getName());
                intent.putExtra(ChatActivity.RECEIPENT_USER_IMAGEURL, user.getImageUrl());
                getContext().startActivity(intent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OthersProfileActivity.class);
                intent.putExtra(FollowersOrPlayedWithActivity.SELECTED_USER, UserProfileDetail.createUser(user));
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    static class RequesterViewHolder {
        @InjectView(R.id.user_name)
        TextView userNameTextView;
        @InjectView(R.id.number_of_followers)
        TextView numberOfFollowersView;
        @InjectView(R.id.user_profile_image)
        ImageView userProfileImageView;
        @InjectView(R.id.number_of_game_played_by_user)
        TextView numberOfGamePlayedTextView;
        @InjectView(R.id.message_image)
        ImageView messageImage;

        RequesterViewHolder(View view) {
            ButterKnife.inject(this, view);
            setFonts();
        }

        private void setFonts() {
            userNameTextView.setTypeface(HitigerApplication.SEMI_BOLD);
            numberOfGamePlayedTextView.setTypeface(HitigerApplication.REGULAR);
            numberOfFollowersView.setTypeface(HitigerApplication.REGULAR);
        }
    }

    public void setOpportunity(OpportunityUiData opportunity) {
        this.opportunity = opportunity;
    }
}
