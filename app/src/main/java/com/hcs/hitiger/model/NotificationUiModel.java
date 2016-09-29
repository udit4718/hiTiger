package com.hcs.hitiger.model;

/**
 * Created by anuj gupta on 5/13/16.
 */
public class NotificationUiModel {
    private String userId;
    private String name;
    private String imageUrl;
    private String requestDaysAgo;
    private String text;

    public NotificationUiModel(String userId, String name, String imageUrl, String text, String requestDaysAgo) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.requestDaysAgo = requestDaysAgo;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRequestDaysAgo() {
        return requestDaysAgo;
    }

    public String getText() {
        return text;
    }
}
