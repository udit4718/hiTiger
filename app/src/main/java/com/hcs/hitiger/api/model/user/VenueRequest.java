package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class VenueRequest {
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("access")
    private int access;
    @SerializedName("creatorId")
    private long creatorId;

    public VenueRequest(String address, double lat, double lng, int access, long creatorId) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.access = access;
        this.creatorId = creatorId;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getAccess() {
        return access;
    }

    public long getCreatorId() {
        return creatorId;
    }
}
