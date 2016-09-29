package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hcs.hitiger.R;
import com.hcs.hitiger.database.ChatMessageDao;
import com.hcs.hitiger.database.model.ChatMessageDb;

import java.util.List;

/**
 * Created by anuj gupta on 5/17/16.
 */
public class ChatMessageListAdapter extends ArrayAdapter<ChatMessageDb> {
    private int padding8dp;
    private int padding20dp;

    public ChatMessageListAdapter(Context context, int resource, List<ChatMessageDb> chatMessageApiList) {
        super(context, resource, chatMessageApiList);
        padding8dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        padding20dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public void setDataList(List<ChatMessageDb> chatMessageApiList) {
        setNotifyOnChange(false); // Prevents 'clear()' from clearing/resetting the listview
        clear();
        addAll(chatMessageApiList);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
        ChatMessageDb chatMessageDb = getItem(position);
        if (chatMessageDb.isSender()) {
            convertView = layoutInflater.inflate(R.layout.chat_self, parent, false);
        } else {
            convertView = layoutInflater.inflate(R.layout.chat_other, parent, false);
            if (chatMessageDb.getStatus() == 0) {
                ChatMessageDao.getInstance().updateMessageReadSatus(chatMessageDb);
            }
        }
        TextView textMessage = (TextView) convertView.findViewById(R.id.text_message);
        textMessage.setText(chatMessageDb.getMessage());

        if (position == 0) {
            convertView.setPadding(padding8dp, padding20dp, padding8dp, 0);
        } else if (position == (getCount() - 1)) {
            convertView.setPadding(padding8dp, 0, padding8dp, padding8dp);
        } else {
            convertView.setPadding(padding8dp, 0, padding8dp, 0);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
