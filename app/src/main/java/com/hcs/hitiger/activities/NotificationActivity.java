package com.hcs.hitiger.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.NotificationListAdapter;
import com.hcs.hitiger.database.NotificationMessageDao;
import com.hcs.hitiger.database.model.NotificationMessageDb;
import com.hcs.hitiger.model.NotificationUiModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NotificationActivity extends AppCompatActivity {
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.notification_list)
    ListView notificationListView;
    @InjectView(R.id.empty_view)
    TextView emptyView;

    @OnClick(R.id.back_press_button)
    public void backPress() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.inject(this);
        title.setTypeface(HitigerApplication.BOLD);
        emptyView.setTypeface(HitigerApplication.BOLD);
        setList();
    }

    private void setList() {
        List<NotificationMessageDb> notificationMessageDbList = NotificationMessageDao.getInstance().getAll();
        if (notificationMessageDbList != null && notificationMessageDbList.size() > 0) {
            List<NotificationUiModel> notificationUiModelList = new ArrayList<>();
            for (NotificationMessageDb notificationMessageDb : notificationMessageDbList) {
                notificationUiModelList.add(new NotificationUiModel(notificationMessageDb.getUserId(),
                        notificationMessageDb.getUserName(), notificationMessageDb.getImage(),
                        notificationMessageDb.getText(), notificationMessageDb.getTitle()));
            }
            NotificationListAdapter notificationListAdapter = new NotificationListAdapter(this, 0, notificationUiModelList);
            notificationListView.setAdapter(notificationListAdapter);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

}
