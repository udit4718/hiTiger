package com.hcs.hitiger.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.ChatActivity;
import com.hcs.hitiger.activities.EventDetailActivity;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailRequest;
import com.hcs.hitiger.api.model.opportunity.OpportunityApiResponse;
import com.hcs.hitiger.api.model.user.AnotherUserProfileRequest;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.database.model.ChatMessageDb;
import com.hcs.hitiger.database.model.NotificationMessageDb;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.UserProfileDetail;

import retrofit.RetrofitError;

/**
 * Created by hot cocoa on 24/05/16.
 */
public class NotificationHelper {
    public static final int CHAT_MESSAGES_BASE_NOTIFICATION_ID = 10000;
    public static final String OPPORTUNITY_ID = "OPPORTUNITY_ID";
    public static final String COM_HCS_HITIGER_RELOAD_DATA_BROADCAST = "com.hcs.hitiger.reloadDataBroadcast";
    public static void showChatMessagesNotification(final Context context, final ChatMessageDb chatMessageDb) {

        new AsyncTask<Void, Void, Void>() {
            private UserApiResponse userApiResponse;
            private OpportunityApiResponse opportunityApiResponse;

            @Override
            protected Void doInBackground(Void... params) {
                UserProfileDetail mCurrentUserProfileDetail = HitigerApplication.getInstance().getUserDetail();
                try {
                    userApiResponse = HitigerApplication.getInstance().getRestClient().getApiService().getAnotherUserProfile(new AnotherUserProfileRequest(mCurrentUserProfileDetail.getFbId(),
                            mCurrentUserProfileDetail.getId(), chatMessageDb.getSenderId())).getUser();
                    opportunityApiResponse = HitigerApplication.getInstance().getRestClient().getApiService().getOppDetails(new GetOpportunityDetailRequest(mCurrentUserProfileDetail.getFbId(), mCurrentUserProfileDetail.getId(),
                            chatMessageDb.getEventId())).getOpportunity();
                } catch (RetrofitError error) {
                    error.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (userApiResponse != null && opportunityApiResponse != null) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ChatActivity.OPPORTUNITY, OpportunityUiData.getOpportunityOrEventData(opportunityApiResponse));
                    intent.putExtra(ChatActivity.RECEIPENT_USER_ID, userApiResponse.getId());
                    intent.putExtra(ChatActivity.RECEIPENT_USER_NAME, userApiResponse.getName());
                    intent.putExtra(ChatActivity.RECEIPENT_USER_IMAGEURL, userApiResponse.getImageUrl());
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.messenger_bubble_small_white)
                            .setContentTitle(opportunityApiResponse.getClub().getName())
                            .setContentText(userApiResponse.getName() + " commented on " + opportunityApiResponse.getClub().getName())
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify((int) (CHAT_MESSAGES_BASE_NOTIFICATION_ID + chatMessageDb.getEventId()), notificationBuilder.build());
                }
            }
        }.execute();
    }

    public static void removeNotificationWithId(int id, Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public static void showUserActivityNotification(Context context, NotificationMessageDb notificationMessageDb) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(OPPORTUNITY_ID, notificationMessageDb.getOppId());
        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.messenger_bubble_small_white)
                .setContentTitle(notificationMessageDb.getTitle())
                .setContentText(notificationMessageDb.getText())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Intent broadcastIntent = new Intent(COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);;
        broadcastIntent.putExtra(OPPORTUNITY_ID,notificationMessageDb.getOppId());
        context.sendBroadcast(broadcastIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((CHAT_MESSAGES_BASE_NOTIFICATION_ID), notificationBuilder.build());
    }
}
