package com.hcs.hitiger.model;

/**
 * Created by hot cocoa on 3/16/16.
 */
public class ListModel {
    private String data;
    private int imageId;

    public ListModel(String data, int imageId) {
        this.data = data;
        this.imageId = imageId;
    }

    public String getData() {
        return data;
    }

    public int getImageId() {
        return imageId;
    }
}
