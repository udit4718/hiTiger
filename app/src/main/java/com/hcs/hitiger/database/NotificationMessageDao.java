package com.hcs.hitiger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.database.model.NotificationMessageDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/26/16.
 */
public class NotificationMessageDao {

    private static NotificationMessageDao Instance = new NotificationMessageDao();

    private ContentValues getContentValues(NotificationMessageDb notificationMessageDb) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.Notification.IMAGE_URL, notificationMessageDb.getImage());
        contentValues.put(Tables.Notification.OPPORTUNITY_ID, notificationMessageDb.getOppId());
        contentValues.put(Tables.Notification.TEXT, notificationMessageDb.getText());
        contentValues.put(Tables.Notification.TITLE, notificationMessageDb.getTitle());
        contentValues.put(Tables.Notification.USER_ID, notificationMessageDb.getUserId());
        contentValues.put(Tables.Notification.USER_NAME, notificationMessageDb.getUserName());
        return contentValues;
    }

    private NotificationMessageDb getNotificationMessage(Cursor cursor) {
        NotificationMessageDb chatMessage = new NotificationMessageDb();
        chatMessage.setImage(cursor.getString(cursor.getColumnIndex(Tables.Notification.IMAGE_URL)));
        chatMessage.setOppId(cursor.getLong(cursor.getColumnIndex(Tables.Notification.OPPORTUNITY_ID)));
        chatMessage.setText(cursor.getString(cursor.getColumnIndex(Tables.Notification.TEXT)));
        chatMessage.setTitle(cursor.getString(cursor.getColumnIndex(Tables.Notification.TITLE)));
        chatMessage.setUserId(cursor.getString(cursor.getColumnIndex(Tables.Notification.USER_ID)));
        chatMessage.setUserName(cursor.getString(cursor.getColumnIndex(Tables.Notification.USER_NAME)));
        return chatMessage;
    }

    public List<NotificationMessageDb> getAll() {
        List<NotificationMessageDb> messageApiList = new ArrayList<>();
        Cursor cursor = HitigerApplication.getInstance().getmSqLiteDatabase().rawQuery("select * from " + Tables.Notification.TABLE_NAME+" order by rowid desc", null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    messageApiList.add(getNotificationMessage(cursor));
                } while (cursor.moveToNext());
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        } catch (CursorIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        } finally {
            cursor.close();
        }
        return messageApiList;
    }

    public boolean insert(NotificationMessageDb notificationMessage) {
        SQLiteDatabase sqLiteDatabase = HitigerApplication.getInstance().getmSqLiteDatabase();
        try {
            return sqLiteDatabase.insert(Tables.Notification.TABLE_NAME, null, getContentValues(notificationMessage)) != -1;
        } catch (SQLiteConstraintException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static NotificationMessageDao getInstance() {
        return Instance;
    }
}
