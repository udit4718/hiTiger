package com.hcs.hitiger;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcs.hitiger.activities.ChatActivity;
import com.hcs.hitiger.api.RestClient;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.notification.SubscribeNotification;
import com.hcs.hitiger.api.model.pubnub.PubnubEnvelope;
import com.hcs.hitiger.api.model.pubnub.PubnubMessageHelper;
import com.hcs.hitiger.api.model.pubnub.TextChatMessageApi;
import com.hcs.hitiger.background.PublishChatMessageService;
import com.hcs.hitiger.background.RegistrationIntentService;
import com.hcs.hitiger.database.ChatMessageDao;
import com.hcs.hitiger.database.DatabaseHelper;
import com.hcs.hitiger.model.UserProfileDetail;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anuj gupta on 4/29/16.
 */
public class HitigerApplication extends Application {

    private static final String PUBLISH_KEY = "pub-c-1f764fca-457f-44bc-bb29-a09db8be1e9d";
    private static final java.lang.String SUBSCRIBE_KEY = "sub-c-f3f123ac-1c23-11e6-8bc8-0619f8945a4f";
    public static final String GCM_REGISTERATION_ID = "gcm_reg_id";
    public static final String GCM_REGISTERATION_ID_SENT_TO_PUBNUB_SERVER = "gcm_reg_id_sent_to_pubnub";
    public static final String GCM_REGISTERATION_ID_SENT_TO_HITIGER_SERVER = "gcm_reg_id_sent_to_hitiger_server";
    public static final String UPDATE_PROFILE ="com.hcs.hitiger";
    private static final String TAG = HitigerApplication.class.getCanonicalName();
    public static final String EVENT_LOAD_KEY = "load_event_fragment";
    private static HitigerApplication instance;

    public static Typeface BOLD;
    public static Typeface REGULAR;
    public static Typeface SEMI_BOLD;
    public static Typeface REGULAR_ITALIC;
    public static Typeface EXTRA_BOLD;
    public static Typeface LIGHT;

    private GsonBuilder gsonBuilder;
    private RestClient restClient;
    private SQLiteDatabase mSqLiteDatabase;
    private Pubnub pubnub;
    private Gson gson;
    private UserProfileDetail userProfileDetail;
    private long mChatPageEventId;

    public static HitigerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initializeFonts();
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }

//        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(!BuildConfig.USE_CRASHLYTICS).build()).build());
        restClient = new RestClient();
        initiateDB();
        initiatePubnub();
        publishUnsentMessages();
        loadPubnubHistory();
        createGCMRegisterationId();
        enableGcmFromServers();
    }

    private void createGCMRegisterationId() {
        if (getGcmRegid() == null) {
            startService(new Intent(this, RegistrationIntentService.class));
        }
    }

    public void enableGcmFromServers() {
        if (getUserDetail() != null && getGcmRegid() != null) {
            if (!getSharedPreferences().getBoolean(GCM_REGISTERATION_ID_SENT_TO_PUBNUB_SERVER, false)) {
                addGcmToPunubChannel(getUserDetail().getId() + "");
            }

            if (!getSharedPreferences().getBoolean(GCM_REGISTERATION_ID_SENT_TO_HITIGER_SERVER, false)) {
                getRestClient().getApiService().subscribeNotification(new SubscribeNotification(getUserDetail().getFbId(), getUserDetail().getId(),
                        Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), getGcmRegid()), new retrofit.Callback<HitigerResponse>() {
                    @Override
                    public void success(HitigerResponse hitigerResponse, Response response) {
                        if (hitigerResponse.isSuccesfull()) {
                            HitigerApplication.getInstance().getSharedPreferences().edit().putBoolean(HitigerApplication.GCM_REGISTERATION_ID_SENT_TO_HITIGER_SERVER, true).apply();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        }
    }

    public void loadPubnubHistory() {
        UserProfileDetail userProfileDetail = UserProfileDetail.getUserProfileDetail();
        if (userProfileDetail != null) {
            // Get the pubnub time to be used as a reference for time. We cannot rely on device time.
            pubnub.time(new Callback() {
                @Override
                public void successCallback(String s, Object o) {
                    try {
                        JSONArray jsonArray = new JSONArray(o.toString());
                        String timeStr = jsonArray.getString(0);
                        Long startTime = ChatMessageDao.getInstance().getLatestUpdatedTime();
                        long timeToken = Long.parseLong(timeStr);

                        if (startTime > 0) {
                            loadHistory(startTime, timeToken);
                        } else {
                            long starttimeToken = timeToken / 10000;
                            starttimeToken = starttimeToken - 2592000000L;
                            starttimeToken = starttimeToken * 10000;
                            // Get history from last 30 days to current pubnub time token
                            loadHistory(starttimeToken, timeToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void errorCallback(String s, PubnubError pubnubError) {
                    if (pubnubError != null)
                        Log.e(TAG, pubnubError.toString());
                }
            });
        }
    }

    private void loadHistory(Long startTime, final long endTime) {
        pubnub.history(getUserDetail().getId() + "", startTime, endTime, 5000, false, true, new Callback() {
            //        pubnub.history(1 + "", startTime, endTime, 100, false, true, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                JSONArray data = null;
                try {
                    data = (JSONArray) ((JSONArray) message).get(0);
                    Log.d(TAG, "successCallback() returned: " + data);

                    if (data != null && data.length() > 0) {
                        for (int i = 0; i < data.length(); i++) {
                            Object jsonObject = data.get(i);
                            PubnubEnvelope pubnubEnvelope = PubnubMessageHelper.getPubNubEnvelope(jsonObject);
                            if (pubnubEnvelope != null) {
                                ChatMessageDao.getInstance().insertMessage(HitigerApplication.getInstance().getGson().fromJson(pubnubEnvelope.getData().toString(), TextChatMessageApi.class).createChatMessageDb());
                            }
                        }

                        long maxTimeOfAllMessagesrecieved = Long.parseLong(((JSONArray) message).get(2).toString());
                        ChatMessageDao.getInstance().updateChatHistoryLastUpdatedTime(maxTimeOfAllMessagesrecieved);
                        if (data.length() == 100) {
                            loadHistory(maxTimeOfAllMessagesrecieved, endTime);
                        }
                        if (data.length() > 0) {
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ChatActivity.NEW_MESSAGE_FETCHED));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void publishUnsentMessages() {
        if (isNetworkAvailable() && ChatMessageDao.getInstance().getUnPublishedMessageList().size() > 0) {
            startService(new Intent(this, PublishChatMessageService.class));
        }
    }

    private void initiatePubnub() {
        pubnub = new Pubnub(
                PUBLISH_KEY,
                SUBSCRIBE_KEY,
                "",
                "",
                true
        );
        pubnub.setCacheBusting(false);
//        pubnub.setOrigin(ORIGIN);
//        pubnub.setAuthKey(AUTH_KEY);
        pubnub.setRetryInterval(5000);
        pubnub.setWindowInterval(5000);
        pubnub.setResumeOnReconnect(true);
        pubnub.setMaxRetries(Integer.MAX_VALUE);
    }


    private void initiateDB() {
        mSqLiteDatabase = new DatabaseHelper(this).getWritableDatabase();
    }

    private void initializeFonts() {
        BOLD = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf");
        REGULAR = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Regular.otf");
        REGULAR_ITALIC = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-RegularItalic.otf");
        SEMI_BOLD = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Semibold.otf");
        EXTRA_BOLD = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Extrabold.otf");
        LIGHT = Typeface.createFromAsset(getAssets(), "fonts/ProximaNovaCond-Light.otf");
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public GsonBuilder getGsonBuilder() {
        if (gsonBuilder == null) {
            gsonBuilder = new GsonBuilder();
        }
        return gsonBuilder;
    }

    public Gson getGson() {
        if (gson == null) {
            gson = getGsonBuilder().create();
        }
        return gson;
    }

    public void showNetworkErrorMessage() {
        Toast.makeText(this, "Some network error occurred, please try again later", Toast.LENGTH_LONG).show();
    }

    public SQLiteDatabase getmSqLiteDatabase() {
        return mSqLiteDatabase;
    }

    public Pubnub getPubnub() {
        return pubnub;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public UserProfileDetail getUserDetail() {
        if (userProfileDetail == null) {
            userProfileDetail = UserProfileDetail.getUserProfileDetail();
        }
        return userProfileDetail;
    }

    public void setUserProfileDetail(UserProfileDetail userProfileDetail) {
        this.userProfileDetail = userProfileDetail;
    }

    public void showServerError() {
        Toast.makeText(this, "Server error", Toast.LENGTH_LONG).show();
    }

    public void addGcmToPunubChannel(String channel) {
        if (getGcmRegid() != null) {
            pubnub.enablePushNotificationsOnChannel(channel, getGcmRegid(), new Callback() {
                @Override
                public void successCallback(String channel,
                                            Object message) {
                    Log.d(TAG, "GCM Added on channel " + channel);
                    HitigerApplication.getInstance().getSharedPreferences().edit().putBoolean(HitigerApplication.GCM_REGISTERATION_ID_SENT_TO_PUBNUB_SERVER, true).apply();
                }

                @Override
                public void errorCallback(String channel,
                                          PubnubError error) {
                    Log.d(TAG, "GCM error on channel " + channel + " error " + error);
                }
            });
        }
    }

    private String getGcmRegid() {
        return getSharedPreferences().getString(GCM_REGISTERATION_ID, null);
    }

    public void setGcmRegisterationId(String id) {
        getSharedPreferences().edit().putString(GCM_REGISTERATION_ID, id).commit();
    }

    public long getmChatPageEventId() {
        return mChatPageEventId;
    }

    public void setmChatPageEventId(long mChatPageEventId) {
        this.mChatPageEventId = mChatPageEventId;
    }

    public void updateProfile() {
        Intent i = new Intent();
        i.setAction(UPDATE_PROFILE);
        getApplicationContext().sendBroadcast(i);
    }

}
