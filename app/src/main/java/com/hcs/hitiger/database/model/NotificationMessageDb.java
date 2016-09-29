package com.hcs.hitiger.database.model;

/**
 * Created by anuj gupta on 5/26/16.
 */
public class NotificationMessageDb {
    private String title;
    private String text;
    private long oppId;
    private String userId;
    private String userName;
    private String image;


    public NotificationMessageDb() {
    }

    public NotificationMessageDb(String title, String text, long oppId, String userId, String userName, String image) {
        this.title = title;
        this.text = text;
        this.oppId = oppId;
        this.userId = userId;
        this.userName = userName;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getOppId() {
        return oppId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOppId(long oppId) {
        this.oppId = oppId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
