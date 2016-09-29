package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.ChatMessageListAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.opportunity.CancelRequestToPlayRequest;
import com.hcs.hitiger.api.model.opportunity.DeclineEventRequest;
import com.hcs.hitiger.api.model.opportunity.FinaliseRequest;
import com.hcs.hitiger.api.model.pubnub.ChatMessage;
import com.hcs.hitiger.api.model.pubnub.PubnubEnvelope;
import com.hcs.hitiger.api.model.pubnub.PubnubMessageHelper;
import com.hcs.hitiger.api.model.pubnub.TextChatMessageApi;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.database.ChatMessageDao;
import com.hcs.hitiger.database.model.ChatMessageDb;
import com.hcs.hitiger.model.OpportunityUiData;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;
import com.hcs.hitiger.util.NotificationHelper;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChatActivity extends AppCompatActivity implements Progressable {
    private static final String TAG = ChatActivity.class.getCanonicalName();

    public static final String OPPORTUNITY = "event_id";
    public static final String RECEIPENT_USER_ID = "recipient_user_id";
    public static final String RECEIPENT_USER_NAME = "recipient_user_name";
    public static final String RECEIPENT_USER_IMAGEURL = "recipient_user_imageUrl";
    public static final String NEW_MESSAGE_FETCHED = "new message_fetched";

    @InjectView(R.id.other_user_profile_image)
    ImageView userProfileImageView;
    @InjectView(R.id.other_user_name)
    TextView otherUserNameTextView;
    @InjectView(R.id.decline_request_button)
    TextView declineRequestButton;
    @InjectView(R.id.finalize_button)
    TextView finalizeButton;
    @InjectView(R.id.chat_list)
    ListView chatListView;
    @InjectView(R.id.send_message_edit_text)
    EditText sendMessageEditTextView;

    private List<ChatMessageDb> chatDataList;
    private ChatMessageListAdapter chatMessageListAdapter;
    private OpportunityUiData mOpportunityUiData;
    private long recipeintId;
    private String recipeintName;
    private String recipeintImageUrl;

    private UserProfileDetail mCurrentUserDetail;
    private ProgressDialog mProgressDialog;
    private BroadcastReceiver messageListUpdateReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            chatDataList = ChatMessageDao.getInstance().getAll(getOpportunityId(), getRecipientId());
//            chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
//            // save index and top position
//            int index = chatListView.getFirstVisiblePosition();
//            View v = chatListView.getChildAt(0);
//            int top = (v == null) ? 0 : (v.getTop() - chatListView.getPaddingTop());
            chatMessageListAdapter.setDataList(chatDataList);
//            chatListView.setSelectionFromTop(index, top);
        }
    };

    @OnClick(R.id.finalize_button)
    public void finnalize() {
        acceptRequest();
    }

    @OnClick(R.id.decline_request_button)
    public void cancelOrDeclineRequest() {
        if (declineRequestButton.getText().toString().equals("Decline")) {
            declineRequest();
        } else {
            cancelRequest();
        }
    }

    private void cancelRequest() {
        showProgress("", getResources().getString(R.string.cancelling_request));
        long creatorId = mOpportunityUiData.getCreatorId() != mCurrentUserDetail.getId() ? mCurrentUserDetail.getId() : recipeintId;
        getApiService().cancelRequest(new CancelRequestToPlayRequest(mCurrentUserDetail.getFbId(), mCurrentUserDetail.getId(), mOpportunityUiData.getId(),
                creatorId), new retrofit.Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                    broadcastIntent.putExtra(NotificationHelper.OPPORTUNITY_ID, mOpportunityUiData.getId());
                    getApplicationContext().sendBroadcast(broadcastIntent);
                    onBackPressed();
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void declineRequest() {
        showProgress("", "Declining Match");
        getApiService().declineEventRequest(new DeclineEventRequest(mCurrentUserDetail.getFbId(), mCurrentUserDetail.getId(), mOpportunityUiData.getId()), new retrofit.Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    Toast.makeText(ChatActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                    broadcastIntent.putExtra(NotificationHelper.OPPORTUNITY_ID, mOpportunityUiData.getId());
                    getApplicationContext().sendBroadcast(broadcastIntent);
                    onBackPressed();
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    @Override
    public void showProgress(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(this, title, message, true);
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    private void acceptRequest() {
        showProgress("", getResources().getString(R.string.accepting_request));
        HitigerApplication.getInstance().getRestClient().getApiService().acceptRequest(new FinaliseRequest(
                mCurrentUserDetail.getFbId(), getOpportunityId(), recipeintId
        ), new retrofit.Callback<HitigerResponse>() {
            @Override
            public void success(HitigerResponse hitigerResponse, Response response) {
                hideProgress();
                if (hitigerResponse.isSuccesfull()) {
                    Intent broadcastIntent = new Intent(NotificationHelper.COM_HCS_HITIGER_RELOAD_DATA_BROADCAST);
                    broadcastIntent.putExtra(NotificationHelper.OPPORTUNITY_ID,mOpportunityUiData.getId());
                    getApplicationContext().sendBroadcast(broadcastIntent);
                    finalizeButton.setVisibility(View.GONE);
                    declineRequestButton.setText("Decline");
                    showCongractualationDialog();
                } else {
                    HitigerApplication.getInstance().showServerError();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private void showCongractualationDialog() {
        com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog confirmOpportunityCustomDialog = new com.hcs.hitiger.view.customdialogs.ConfirmOpportunityCustomDialog(
                this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, mOpportunityUiData, new UserApiResponse(recipeintName, recipeintId, recipeintImageUrl));
        confirmOpportunityCustomDialog.show();
    }

    @OnClick(R.id.send_message_botton)
    public void sendMessage() {
        chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        String messageContent = sendMessageEditTextView.getText().toString().trim();
        if (!messageContent.isEmpty()) {
            ChatMessageDb message = createTextMessage(messageContent);
            chatDataList.add(message);
            chatMessageListAdapter.setDataList(chatDataList);
            chatMessageListAdapter.notifyDataSetChanged();
            sendMessageEditTextView.setText("");
            ChatMessageDao.getInstance().insertMessage(message);
            PubnubMessageHelper.publishToChannel(message.createChatMeesageApi(), getUserId() + "");
            PubnubMessageHelper.publishToChannel(message.createChatMeesageApi(), getRecipientId() + "");
        }
    }

    private ChatMessageDb createTextMessage(String messageContent) {
        return new ChatMessageDb(HitigerApplication.getInstance().getPubnub().uuid(), getUserId(), getRecipientId(), getOpportunityId(), messageContent, Calendar.getInstance().getTimeInMillis(), 0, ChatMessage.MessageType.TEXT.getValue(), 1);
    }

    @OnClick(R.id.backpress_button)
    public void backpress() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrentUserDetail = HitigerApplication.getInstance().getUserDetail();

        mOpportunityUiData = getIntent().getParcelableExtra(OPPORTUNITY);
        recipeintName = getIntent().getStringExtra(RECEIPENT_USER_NAME);
        recipeintId = getIntent().getLongExtra(RECEIPENT_USER_ID, 0);
        recipeintImageUrl = getIntent().getStringExtra(RECEIPENT_USER_IMAGEURL);

        if (mOpportunityUiData.getCreatorId() != mCurrentUserDetail.getId()) {
            finalizeButton.setVisibility(View.GONE);
        }

        otherUserNameTextView.setText(recipeintName);
        sendMessageEditTextView.setHint("Write message to " + recipeintName);
        if (recipeintImageUrl != null)
            Picasso.with(this).load(recipeintImageUrl).into(userProfileImageView);
        else {
            userProfileImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile_default_image));
        }

        if (mOpportunityUiData.getVersus() != null) {
            finalizeButton.setVisibility(View.GONE);
            if (mOpportunityUiData.getVersus().getId() == recipeintId) {
                declineRequestButton.setText("Decline");
            } else {
                declineRequestButton.setVisibility(View.GONE);
            }

        } else {
            if (mCurrentUserDetail.getId() == mOpportunityUiData.getCreatorId()) {
                finalizeButton.setVisibility(View.VISIBLE);
            }
            declineRequestButton.setText("Cancel Request");
        }

//        if (mOpportunityUiData.getAcceptorId() == 0) {
//            declineRequestButton.setText("Cancel Request");
//        }

        ButterKnife.inject(this);
        setFonts();
        chatMessageListAdapter = new ChatMessageListAdapter(this, 0, new ArrayList<ChatMessageDb>());
        chatListView.setAdapter(chatMessageListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            HitigerApplication.getInstance().getPubnub().subscribe(getUserId() + "", new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                }

                @Override
                public void successCallback(String channel, Object message, String timetoken) {
                    Log.d(TAG, "subsribe successCallback() returned: " + message);
                    ChatMessageDao.getInstance().updateChatHistoryLastUpdatedTime(Long.parseLong(timetoken));
                    try {
                        PubnubEnvelope pubnubEnvelope = PubnubMessageHelper.getPubNubEnvelope(message);
                        if (pubnubEnvelope != null) {
                            new AddMessageFromPubnub(
                                    HitigerApplication.getInstance().getGson().fromJson(pubnubEnvelope.getData(), TextChatMessageApi.class)).execute();
                        }
                    } catch (JsonSyntaxException ex) {
                        ex.printStackTrace();
                        return;
                    }
                }

                @Override
                public void successCallbackV2(String channel, Object message, JSONObject envelope) {
                    super.successCallbackV2(channel, message, envelope);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    super.errorCallback(channel, error);
                }

                @Override
                public void reconnectCallback(String channel, Object message) {
                    super.reconnectCallback(channel, message);
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    super.connectCallback(channel, message);
                    HitigerApplication.getInstance().loadPubnubHistory();
                    HitigerApplication.getInstance().publishUnsentMessages();
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    super.disconnectCallback(channel, message);
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(messageListUpdateReciever, new IntentFilter(NEW_MESSAGE_FETCHED));
        HitigerApplication.getInstance().setmChatPageEventId(getOpportunityId());
        chatDataList = ChatMessageDao.getInstance().getAll(getOpportunityId(), getRecipientId());
        chatMessageListAdapter.setDataList(chatDataList);
        NotificationHelper.removeNotificationWithId((int) (getOpportunityId() + NotificationHelper.CHAT_MESSAGES_BASE_NOTIFICATION_ID), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HitigerApplication.getInstance().getPubnub().unsubscribe(getUserId() + "");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageListUpdateReciever);
        HitigerApplication.getInstance().setmChatPageEventId(0);
    }

    private void setFonts() {
        finalizeButton.setTypeface(HitigerApplication.SEMI_BOLD);
        declineRequestButton.setTypeface(HitigerApplication.REGULAR);
        otherUserNameTextView.setTypeface(HitigerApplication.SEMI_BOLD);
        sendMessageEditTextView.setTypeface(HitigerApplication.REGULAR);
    }

    public long getUserId() {
        return HitigerApplication.getInstance().getUserDetail().getId();
    }

    public long getRecipientId() {
        return recipeintId;
    }

    public long getOpportunityId() {
        return mOpportunityUiData.getId();
    }

    class AddMessageFromPubnub extends AsyncTask<Void, Void, Boolean> {

        private ChatMessageDb chatMessage;

        public AddMessageFromPubnub(TextChatMessageApi textChatMessageApi) {
            this.chatMessage = textChatMessageApi.createChatMessageDb();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return ChatMessageDao.getInstance().insertMessage(chatMessage);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b && chatMessage.getSenderId() == getRecipientId()) {
                if (chatMessage.getEventId() == getOpportunityId()) {
                    chatMessageListAdapter.add(chatMessage);
                    chatMessageListAdapter.notifyDataSetChanged();
                } else {
                    NotificationHelper.showChatMessagesNotification(ChatActivity.this, chatMessage);
                }
            }
        }
    }
}
