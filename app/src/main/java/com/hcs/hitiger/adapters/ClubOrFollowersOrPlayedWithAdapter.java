package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.follow.FollowRequest;
import com.hcs.hitiger.api.model.follow.UnFollowRequest;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anuj gupta on 5/3/16.
 */
public class ClubOrFollowersOrPlayedWithAdapter extends ArrayAdapter<UserProfileDetail> {
    private List<UserProfileDetail> profileDetailArrayList = new ArrayList<>();
    private UserProfileDetail currentUserProfileDetail;

    public ClubOrFollowersOrPlayedWithAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void addingList(List<UserProfileDetail> userProfileDetailList, UserProfileDetail currentUserProfileDetail) {
        this.profileDetailArrayList = userProfileDetailList;
        this.currentUserProfileDetail = currentUserProfileDetail;
    }

    @Override
    public int getCount() {
        return profileDetailArrayList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FollowersViewHolder followersViewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.followers_list, parent, false);
            followersViewHolder = new FollowersViewHolder(convertView);
            convertView.setTag(followersViewHolder);
        } else {
            followersViewHolder = (FollowersViewHolder) convertView.getTag();
        }
        followersViewHolder.userNameTextView.setText(profileDetailArrayList.get(position).getName());


        Spannable wordtoSpan1 = new SpannableString(profileDetailArrayList.get(position).getGamesPlayed() + " Game Played");
        wordtoSpan1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)),
                0, wordtoSpan1.length() - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        followersViewHolder.numberOfGamePlayedTExtView.setText(wordtoSpan1);

        if (profileDetailArrayList.get(position).getImageUrl() != null) {
            Picasso.with(getContext()).load(profileDetailArrayList.get(position).getImageUrl()).into(followersViewHolder.userImageImageView);
        }

        setChangesForFolloewrsButton(position, followersViewHolder.followingOrFollowingTextView);
        followersViewHolder.followingOrFollowingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFollowOrFollowingTextView(position, followersViewHolder.followingOrFollowingTextView);
//                profileDetailArrayList.get(position).setFollowing(!profileDetailArrayList.get(position).isFollowing());
            }
        });

        return convertView;
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private void setFollowOrFollowingTextView(final int position, final TextView followingOrFollowingTextView) {
        if (profileDetailArrayList.get(position).isFollowing()) {
            ((Progressable) getContext()).showProgress("", "Unfollowing...");
            getApiService().unfollowUser(new UnFollowRequest(currentUserProfileDetail.getFbId(), currentUserProfileDetail.getId(),
                    profileDetailArrayList.get(position).getId()), new Callback<HitigerResponse>() {
                @Override
                public void success(HitigerResponse hitigerResponse, Response response) {
                    ((Progressable) getContext()).hideProgress();
                    if (hitigerResponse.isSuccesfull()) {
                        HitigerApplication.getInstance().updateProfile();
                        profileDetailArrayList.get(position).setFollowing(!profileDetailArrayList.get(position).isFollowing());
                        setChangesForFolloewrsButton(position, followingOrFollowingTextView);
                    } else
                        HitigerApplication.getInstance().showServerError();
                }

                @Override
                public void failure(RetrofitError error) {
                    ((Progressable) getContext()).hideProgress();
                    HitigerApplication.getInstance().showNetworkErrorMessage();
                }
            });
        } else {
            ((Progressable) getContext()).showProgress("", "Following...");
            getApiService().follow(new FollowRequest(currentUserProfileDetail.getFbId(), currentUserProfileDetail.getId(),
                    profileDetailArrayList.get(position).getId()), new Callback<HitigerResponse>() {
                @Override
                public void success(HitigerResponse hitigerResponse, Response response) {
                    ((Progressable) getContext()).hideProgress();
                    if (hitigerResponse.isSuccesfull()) {
                        HitigerApplication.getInstance().updateProfile();
                        profileDetailArrayList.get(position).setFollowing(!profileDetailArrayList.get(position).isFollowing());
                        setChangesForFolloewrsButton(position, followingOrFollowingTextView);
                    } else
                        HitigerApplication.getInstance().showServerError();
                }

                @Override
                public void failure(RetrofitError error) {
                    ((Progressable) getContext()).hideProgress();
                    HitigerApplication.getInstance().showNetworkErrorMessage();
                }
            });
        }
    }

    private void setChangesForFolloewrsButton(int position, TextView followingOrFollowingTextView) {
        if (currentUserProfileDetail.getId() == profileDetailArrayList.get(position).getId()) {
            followingOrFollowingTextView.setVisibility(View.GONE);
        } else {
            if (profileDetailArrayList.get(position).isFollowing()) {
                followingOrFollowingTextView.setTypeface(HitigerApplication.SEMI_BOLD);
                followingOrFollowingTextView.setBackgroundDrawable(
                        ContextCompat.getDrawable(getContext(), R.drawable.yellow_rounded_rectangular_background));
                followingOrFollowingTextView.setText(getContext().getResources().getString(R.string.following));
                followingOrFollowingTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else {
                followingOrFollowingTextView.setBackgroundDrawable(
                        ContextCompat.getDrawable(getContext(), R.drawable.white_background_with_rounded_rectangular_border));
                followingOrFollowingTextView.setTypeface(HitigerApplication.REGULAR);
                followingOrFollowingTextView.setText(getContext().getResources().getString(R.string.follow));
                followingOrFollowingTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(getContext(), R.drawable.create_add), null, null, null);
                followingOrFollowingTextView.setCompoundDrawablePadding(25);
            }
        }
    }

    static class FollowersViewHolder {
        @InjectView(R.id.user_image)
        ImageView userImageImageView;
        @InjectView(R.id.user_name)
        TextView userNameTextView;
        @InjectView(R.id.number_of_game_played_by_user)
        TextView numberOfGamePlayedTExtView;
        @InjectView(R.id.follow_or_following)
        TextView followingOrFollowingTextView;

        public FollowersViewHolder(View view) {
            ButterKnife.inject(this, view);
            setStyles();
        }

        private void setStyles() {
            userNameTextView.setTypeface(HitigerApplication.SEMI_BOLD);
            numberOfGamePlayedTExtView.setTypeface(HitigerApplication.REGULAR);
            followingOrFollowingTextView.setTypeface(HitigerApplication.REGULAR);
        }
    }
}
