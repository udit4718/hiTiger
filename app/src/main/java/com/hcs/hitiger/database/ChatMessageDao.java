package com.hcs.hitiger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.api.model.pubnub.TextChatMessageApi;
import com.hcs.hitiger.database.model.ChatMessageDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hot cocoa on 17/05/16.
 */
public class ChatMessageDao {

    private static ChatMessageDao Instance = new ChatMessageDao();

    public static final String CHAT_HISTORY_LAST_LOADED = "chat_history_last_loaded";

    public static ChatMessageDao getInstance() {
        return Instance;
    }

    private ContentValues getContentValues(ChatMessageDb chatMessage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.ChatMessgae.EVENT_ID, chatMessage.getEventId());
        contentValues.put(Tables.ChatMessgae.SENDER_ID, chatMessage.getSenderId());
        contentValues.put(Tables.ChatMessgae.RECEIVER_ID, chatMessage.getRecieverId());
        contentValues.put(Tables.ChatMessgae.UNIQUE_MESSAGE_ID, chatMessage.getUniqueMessageId());
        contentValues.put(Tables.ChatMessgae.MESSAGE, chatMessage.getMessage());
        contentValues.put(Tables.ChatMessgae.PUBLISH_STATUS, chatMessage.getPublishStatus());
        contentValues.put(Tables.ChatMessgae.TIMESTAMP, chatMessage.getTimestamp());
        contentValues.put(Tables.ChatMessgae.MESSAGE_TYPE, chatMessage.getType());
        contentValues.put(Tables.ChatMessgae.STATUS, chatMessage.getStatus());
        return contentValues;
    }

    private ChatMessageDb getChatMessage(Cursor cursor) {
        ChatMessageDb chatMessage = new ChatMessageDb();
        chatMessage.setEventId(cursor.getLong(cursor.getColumnIndex(Tables.ChatMessgae.EVENT_ID)));
        chatMessage.setSenderId(cursor.getLong(cursor.getColumnIndex(Tables.ChatMessgae.SENDER_ID)));
        chatMessage.setRecieverId(cursor.getLong(cursor.getColumnIndex(Tables.ChatMessgae.RECEIVER_ID)));
        chatMessage.setUniqueMessageId(cursor.getString(cursor.getColumnIndex(Tables.ChatMessgae.UNIQUE_MESSAGE_ID)));
        chatMessage.setMessage(cursor.getString(cursor.getColumnIndex(Tables.ChatMessgae.MESSAGE)));
        chatMessage.setTimestamp(cursor.getLong(cursor.getColumnIndex(Tables.ChatMessgae.TIMESTAMP)));
        chatMessage.setPublishStatus(cursor.getInt(cursor.getColumnIndex(Tables.ChatMessgae.PUBLISH_STATUS)));
        chatMessage.setType(cursor.getInt(cursor.getColumnIndex(Tables.ChatMessgae.MESSAGE_TYPE)));
        chatMessage.setStatus(cursor.getInt(cursor.getColumnIndex(Tables.ChatMessgae.STATUS)));
        return chatMessage;
    }

    public List<ChatMessageDb> getAll(long eventId, long recipientId) {
        List<ChatMessageDb> messageApiList = new ArrayList<>();
        Cursor cursor = HitigerApplication.getInstance().getmSqLiteDatabase().rawQuery("select * from " + Tables.ChatMessgae.TABLE_NAME + " where " + Tables.ChatMessgae.EVENT_ID + " = " + eventId + " and (" + Tables.ChatMessgae.SENDER_ID + " = " + recipientId + " or " + Tables.ChatMessgae.RECEIVER_ID + " = " + recipientId + ") order by " + Tables.ChatMessgae.ID + " asc;", null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    messageApiList.add(getChatMessage(cursor));
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

    public void updateMessageSentStatusFlag(TextChatMessageApi textChatMessageApi, boolean myChannel) {
        SQLiteDatabase sqLiteDatabase = HitigerApplication.getInstance().getmSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.query(Tables.ChatMessgae.TABLE_NAME, null, Tables.ChatMessgae.UNIQUE_MESSAGE_ID + " = \"" + textChatMessageApi.getUniqueMessageId() + "\";", null, null, null, null);
        cursor.moveToFirst();
        ChatMessageDb chatMessage = getChatMessage(cursor);
        int status;
        if (chatMessage.getPublishStatus() == 0) {
            status = myChannel ? 1 : 2;
        } else {
            status = 3;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.ChatMessgae.PUBLISH_STATUS, status);
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.update(Tables.ChatMessgae.TABLE_NAME, contentValues, Tables.ChatMessgae.UNIQUE_MESSAGE_ID + " = \"" + textChatMessageApi.getUniqueMessageId() + "\"", null);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public boolean insertMessage(ChatMessageDb chatMessage) {
        SQLiteDatabase sqLiteDatabase = HitigerApplication.getInstance().getmSqLiteDatabase();
        try {
            return sqLiteDatabase.insert(Tables.ChatMessgae.TABLE_NAME, null, getContentValues(chatMessage)) != -1;
        } catch (SQLiteConstraintException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<ChatMessageDb> getUnPublishedMessageList() {
        List<ChatMessageDb> messageApiList = new ArrayList<>();
        Cursor cursor = HitigerApplication.getInstance().getmSqLiteDatabase().rawQuery("select * from " + Tables.ChatMessgae.TABLE_NAME + " where " + Tables.ChatMessgae.PUBLISH_STATUS + " >= " + ChatMessageDb.MessageStatus.NOT_SENT.getValue() + " and " + Tables.ChatMessgae.PUBLISH_STATUS + " < " + ChatMessageDb.MessageStatus.SENT_TO_BOTH_CHANNEL.getValue(), null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    messageApiList.add(getChatMessage(cursor));
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

    public long getLatestUpdatedTime() {
        return HitigerApplication.getInstance().getSharedPreferences().getLong(CHAT_HISTORY_LAST_LOADED, 0);
    }


    public void updateChatHistoryLastUpdatedTime(long time) {
        HitigerApplication.getInstance().getSharedPreferences().edit().putLong(CHAT_HISTORY_LAST_LOADED, time).commit();
    }

    public void updateMessageReadSatus(ChatMessageDb chatMessageDb) {
        SQLiteDatabase sqLiteDatabase = HitigerApplication.getInstance().getmSqLiteDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.ChatMessgae.STATUS, 1);
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.update(Tables.ChatMessgae.TABLE_NAME, contentValues, Tables.ChatMessgae.UNIQUE_MESSAGE_ID + " = \"" + chatMessageDb.getUniqueMessageId() + "\"", null);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }
}
