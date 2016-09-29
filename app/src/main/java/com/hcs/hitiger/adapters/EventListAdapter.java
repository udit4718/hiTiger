package com.hcs.hitiger.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.ChatActivity;
import com.hcs.hitiger.activities.EventDetailActivity;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.opportunity.DeclineEventRequest;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.util.NotificationHelper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anuj gupta on 5/20/16.
 */
public class EventListAdapter extends ArrayAdapter<OpportunityUiData> {
    private long userId;
    private int padding16dp;
    private List<OpportunityUiData> opportunitiesDataList;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1221;

    public EventListAdapter(Context context, int resource, long userId) {
        super(context, resource);
        this.userId = userId;
        padding16dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
    }

    public void setData(List<OpportunityUiData> opportunitiesDataList) {
        this.opportunitiesDataList = opportunitiesDataList;
    }

    @Override
    public OpportunityUiData getItem(int position) {
        return opportunitiesDataList.get(position);
    }

    @Override
    public int getCount() {
        return opportunitiesDataList == null ? 0 : opportunitiesDataList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        EventViewHolder eventViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.event_list_layout, parent, false);
            eventViewHolder = new EventViewHolder(convertView);
            convertView.setTag(eventViewHolder);
        } else {
            eventViewHolder = (EventViewHolder) convertView.getTag();
        }

        setDataToViews(eventViewHolder, position);

        eventViewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(ChatActivity.OPPORTUNITY, getItem(position));
                if (getItem(position).getVersus() != null) {
                    intent.putExtra(ChatActivity.RECEIPENT_USER_ID, getItem(position).getVersus().getId());
                    intent.putExtra(ChatActivity.RECEIPENT_USER_NAME, getItem(position).getVersus().getName());
                    intent.putExtra(ChatActivity.RECEIPENT_USER_IMAGEURL, getItem(position).getVersus().getImageUrl());
                } else {
                    intent.putExtra(ChatActivity.RECEIPENT_USER_ID, getItem(position).getUser().getId());
                    intent.putExtra(ChatActivity.RECEIPENT_USER_NAME, getItem(position).getUser().getName());
                    intent.putExtra(ChatActivity.RECEIPENT_USER_IMAGEURL, getItem(position).getUser().getImageUrl());
                }
                getContext().startActivity(intent);
            }
        });

        eventViewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + getItem(position).getVersus().getMobile()));
                    getContext().startActivity(callIntent);
                }
            }
        });


        eventViewHolder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HitigerApplication.getInstance().getRestClient().getApiService().declineEventRequest(new DeclineEventRequest(getItem(position).getUser().getFbId()
                        , getItem(position).getUser().getId(), getItem(position).getId()), new Callback<HitigerResponse>() {
                    @Override
                    public void success(HitigerResponse hitigerResponse, Response response) {
                        if (hitigerResponse.isSuccesfull()) {
                            Toast.makeText(getContext(), "successful", Toast.LENGTH_SHORT).show();

                            Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                            getContext().getApplicationContext().sendBroadcast(broadcastIntent);
                        } else {
                            HitigerApplication.getInstance().showServerError();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HitigerApplication.getInstance().showNetworkErrorMessage();
                    }
                });
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EventDetailActivity.class);
                intent.putExtra(EventDetailActivity.OPPORTUNITY_ID, getItem(position).getId());
                getContext().startActivity(intent);
            }
        });
        if (position == 0) {
            convertView.setPadding(padding16dp, padding16dp, padding16dp, 0);
        } else if (position == (getCount() - 1)) {
            convertView.setPadding(padding16dp, 0, padding16dp, padding16dp);
        } else {
            convertView.setPadding(padding16dp, 0, padding16dp, 0);
        }

        return convertView;
    }

    private void setDataToViews(EventViewHolder eventViewHolder, final int position) {
        if (position > 0) {
            Calendar previous = Calendar.getInstance();
            previous.setTimeInMillis(getItem(position - 1).getDate());
            previous.set(Calendar.HOUR_OF_DAY, 0);
            previous.set(Calendar.MINUTE, 0);
            previous.set(Calendar.SECOND, 0);
            previous.set(Calendar.MILLISECOND, 0);

            Calendar current = Calendar.getInstance();
            current.setTimeInMillis(getItem(position).getDate());
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);

            if (!previous.equals(current)) {
                eventViewHolder.dateLayout.setVisibility(View.VISIBLE);
                setDate(eventViewHolder.newDate, position);
            } else {
                eventViewHolder.dateLayout.setVisibility(View.GONE);
            }
        } else {
            eventViewHolder.dateLayout.setVisibility(View.VISIBLE);
            setDate(eventViewHolder.newDate, position);
        }

        if (getItem(position).getVersus() != null && !getItem(position).isReqPending()) {
            eventViewHolder.userDetailLayout.setVisibility(View.VISIBLE);
            eventViewHolder.fixedWithView.setVisibility(View.VISIBLE);
            eventViewHolder.singleLine.setVisibility(View.VISIBLE);

            Spannable gamePlayedSpan = new SpannableString(getItem(position).getVersus().getGamesPlayed() + " Game Played");
            gamePlayedSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)),
                    0, gamePlayedSpan.length() - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            eventViewHolder.numberOfGamePlayedByUser.setText(gamePlayedSpan);

            eventViewHolder.userName.setText(getItem(position).getVersus().getName());
            if (getItem(position).getVersus().getImageUrl() != null) {
                Picasso.with(getContext()).load(getItem(position).getVersus().getImageUrl())
                        .into(eventViewHolder.userProfileImage);
            } else {
                eventViewHolder.userProfileImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default_image));
            }
        } else {
            eventViewHolder.userDetailLayout.setVisibility(View.GONE);
            eventViewHolder.fixedWithView.setVisibility(View.GONE);
            eventViewHolder.singleLine.setVisibility(View.GONE);
        }

        if (getItem(position).getPrice() != 0) {
            eventViewHolder.gamePrice.setText(getItem(position).getPrice() + "");
            eventViewHolder.gamePrice.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), R.drawable.rupee), null, null, null);
            eventViewHolder.gamePrice.setCompoundDrawablePadding(4);
        } else {
            eventViewHolder.gamePrice.setText(getContext().getString(R.string.free));
            eventViewHolder.gamePrice.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        Picasso.with(getContext()).load(getItem(position).getClub().getImageUrl()).into(eventViewHolder.gameImage);
        eventViewHolder.gameName.setText(getItem(position).getClub().getName());
        int defaultColor = ContextCompat.getColor(getContext(), R.color.blue);
        eventViewHolder.gameName.setTextColor(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{android.R.attr.state_pressed}, new int[]{}},
                new int[]{defaultColor, defaultColor, Color.parseColor(getItem(position).getClub().getColor())}));

        if (getItem(position).getTime() != -1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
            String timeInString = timeFormat.format(getItem(position).getTime()).replace("a.m.", "AM").replace("p.m.", "PM");
            eventViewHolder.gameTiming.setText(timeInString);
        } else {
            eventViewHolder.gameTiming.setText(getContext().getResources().getString(R.string.all_day));
        }

        setDate(eventViewHolder.gameDate, position);

        if (getItem(position).getVenue() != null)
            eventViewHolder.gameAddress.setText(getItem(position).getVenue().getAddress());
        else {
            eventViewHolder.gameAddress.setText("");
        }

        if ((getItem(position).isReqPending() && getItem(position).getUser() != null) || (getItem(position).getReqNames() != null && getItem(position).getReqNames().size() > 0)) {
            eventViewHolder.requestersLayout.setVisibility(View.VISIBLE);
            if (getItem(position).isReqPending()) {
                eventViewHolder.requestersLayout.setVisibility(View.VISIBLE);
                eventViewHolder.requestersTextView.setText("You Request " + getItem(position).getUser().getName());
            } else if (getItem(position).getReqNames().size() > 1) {
                eventViewHolder.requestersTextView.setText(getItem(position).getReqNames().get(0).split(" ")[0]
                        + " & " + (getItem(position).getReqNames().size() - 1) + " " +
                        getContext().getResources().getString(R.string.players_request_to_play));
            } else {
                eventViewHolder.requestersTextView.setText(getItem(position).getReqNames().get(0)
                        + " " + getContext().getResources().getString(R.string.request_to_play));
            }
        } else {
            eventViewHolder.requestersLayout.setVisibility(View.GONE);
        }
    }

    private void setDate(TextView dateText, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM");
        String date = dateFormat.format(getItem(position).getDate());
        dateText.setText(date);
    }

    static class EventViewHolder {
        @InjectView(R.id.new_date)
        TextView newDate;
        @InjectView(R.id.date_layout)
        LinearLayout dateLayout;
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
        @InjectView(R.id.user_detail_layout)
        LinearLayout userDetailLayout;
        @InjectView(R.id.decline_button)
        TextView declineButton;
        @InjectView(R.id.call_button)
        TextView callButton;
        @InjectView(R.id.fixed_with)
        TextView fixedWithView;
        @InjectView(R.id.single_line_view)
        View singleLine;
        @InjectView(R.id.requesters_layout)
        RelativeLayout requestersLayout;
        @InjectView(R.id.requesters_text)
        TextView requestersTextView;
        @InjectView(R.id.message_image)
        ImageView messageImage;

        public EventViewHolder(View view) {
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
            declineButton.setTypeface(HitigerApplication.REGULAR);
            callButton.setTypeface(HitigerApplication.SEMI_BOLD);
            requestersTextView.setTypeface(HitigerApplication.REGULAR);
            fixedWithView.setTypeface(HitigerApplication.REGULAR);
            newDate.setTypeface(HitigerApplication.BOLD);
        }
    }
}
