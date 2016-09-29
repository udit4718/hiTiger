package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.OthersProfileActivity;
import com.hcs.hitiger.model.NotificationUiModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 5/13/16.
 */
public class NotificationListAdapter extends ArrayAdapter<NotificationUiModel> {

    private int padding16dp;

    public NotificationListAdapter(Context context, int resource, List<NotificationUiModel> notificationUiModelList) {
        super(context, resource, notificationUiModelList);
        padding16dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NotificationViewHolder notificationViewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.notification_list, parent, false);
            notificationViewHolder = new NotificationViewHolder(convertView);
            convertView.setTag(notificationViewHolder);
        } else {
            notificationViewHolder = (NotificationViewHolder) convertView.getTag();
        }

        notificationViewHolder.userName.setText(getItem(position).getName());
        notificationViewHolder.requestedToPlayView.setText(getItem(position).getText());

        if (getItem(position).getImageUrl() != null) {
            Picasso.with(getContext()).load(getItem(position).getImageUrl())
                    .into(notificationViewHolder.userImageView);
        } else {
            notificationViewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_profile_default_image));
        }

        if (position == 0) {
            convertView.setPadding(padding16dp, padding16dp, padding16dp, 0);
        } else if (position == (getCount() - 1)) {
            convertView.setPadding(padding16dp, 0, padding16dp, padding16dp);
        } else {
            convertView.setPadding(padding16dp, 0, padding16dp, 0);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OthersProfileActivity.class);
                intent.putExtra(OthersProfileActivity.SELECTED_USER_ID, getItem(position).getUserId());
                intent.putExtra(OthersProfileActivity.SELECTED_USER_NAME, getItem(position).getName());
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    static class NotificationViewHolder {
        @InjectView(R.id.user_name)
        TextView userName;
        @InjectView(R.id.requested_to_play)
        TextView requestedToPlayView;
        @InjectView(R.id.number_of_days_ago)
        TextView numberOfDaysAgoView;
        @InjectView(R.id.user_profile_image)
        ImageView userImageView;


        NotificationViewHolder(View view) {
            ButterKnife.inject(this, view);
            setFonts();
        }

        private void setFonts() {
            userName.setTypeface(HitigerApplication.SEMI_BOLD);
            requestedToPlayView.setTypeface(HitigerApplication.REGULAR);
            numberOfDaysAgoView.setTypeface(HitigerApplication.REGULAR);
        }
    }
}
