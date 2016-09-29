
package com.hcs.hitiger.background;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            HitigerApplication.getInstance().setGcmRegisterationId(token);
            HitigerApplication.getInstance().getSharedPreferences().edit().putBoolean(HitigerApplication.GCM_REGISTERATION_ID_SENT_TO_PUBNUB_SERVER, false).apply();
            HitigerApplication.getInstance().getSharedPreferences().edit().putBoolean(HitigerApplication.GCM_REGISTERATION_ID_SENT_TO_HITIGER_SERVER, false).apply();
            sendRegistrationToServer(token);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        HitigerApplication.getInstance().enableGcmFromServers();
    }

}
