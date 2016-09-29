package com.hcs.hitiger.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InviteFriendsActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar_title)
    TextView title;
    @InjectView(R.id.invite_friend_button)
    TextView inviteButton;
    @InjectView(R.id.invite_friend_message)
    TextView inviteFriendMEssage;
    @InjectView(R.id.invite_friend_text)
    TextView inviteFriendText;
    @InjectView(R.id.whistle_image)
    ImageView inviteFriendImage;

    @OnClick(R.id.backpress_button)
    public void backpress() {
        onBackPressed();
    }

    @OnClick(R.id.invite_friend_button)
    public void invite() {
        List<Intent> targetShareIntents = new ArrayList<>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                if (packageName.contains("com.whatsapp") || packageName.contains("facebook") || packageName.contains("com.google.android.gm")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Text");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose an app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            } else {
                Toast.makeText(this, "No apps to share", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends_activity);

        ButterKnife.inject(this);
        setStyles();
        title.setText("INVITE FRIENDS");
    }

    private void setStyles() {
        title.setTypeface(HitigerApplication.BOLD);
        inviteButton.setTypeface(HitigerApplication.BOLD);
        inviteFriendMEssage.setTypeface(HitigerApplication.REGULAR);
        inviteFriendText.setTypeface(HitigerApplication.BOLD);
    }
}
