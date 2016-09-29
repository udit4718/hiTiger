package com.hcs.hitiger.database;

/**
 * Created by hot cocoa on 17/05/16.
 */
public abstract class Tables {

    public static class ChatMessgae {

        public static final String TABLE_NAME = "messages";

        public static final String ID = "_id";
        public static final String SENDER_ID = "sender_id";
        public static final String RECEIVER_ID = "receiver_id";
        public static final String EVENT_ID = "event_id";
        public static final String UNIQUE_MESSAGE_ID = "unique_message_id";
        public static final String MESSAGE = "message";
        public static final String TIMESTAMP = "timestamp";
        public static final String PUBLISH_STATUS = "publish_status";
        public static final String STATUS = "status";
        public static final String MESSAGE_TYPE = "message_type";

        public static final String CREATE_TABLE =
                "create table " + TABLE_NAME +
                        " ( " + ID + " INTEGER PRIMARY KEY autoincrement, " +
                        UNIQUE_MESSAGE_ID + " TEXT unique," +
                        SENDER_ID + " INTEGER, " +
                        RECEIVER_ID + " INTEGER, " +
                        EVENT_ID + " INTEGER, " +
                        TIMESTAMP + " INTEGER, " +
                        PUBLISH_STATUS + " INTEGER, " +
                        STATUS + " INTEGER, " +
                        MESSAGE_TYPE + " INTEGER, " +
                        MESSAGE + " TEXT )";
    }

    public static class Notification {

        public static final String TABLE_NAME = "notification";

        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String OPPORTUNITY_ID = "oppId";
        public static final String USER_ID = "userId";
        public static final String USER_NAME = "userName";
        public static final String IMAGE_URL = "image";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME +
                " ( " + TITLE + " TEXT," + TEXT + " TEXT," + OPPORTUNITY_ID + " INTEGER," + USER_ID + " TEXT,"
                + USER_NAME + " TEXT," + IMAGE_URL + " TEXT )";
    }

}
