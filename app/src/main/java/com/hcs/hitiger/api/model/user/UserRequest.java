package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class UserRequest {
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("fbId")
    private String fbId;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("status")
    private int status;
    @SerializedName("role")
    private int role;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    public UserRequest(String name, String email, String mobile, String fbId, String imageUrl, int status, int role, String address, double lat, double lng) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.fbId = fbId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.role = role;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public UserRequest(long id, String name, String email, String mobile, String fbId, String imageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.fbId = fbId;
        this.imageUrl = imageUrl;
    }

    public UserRequest(long id, String address, double lat, double lng, String fbId) {
        this.id=id;
        this.fbId = fbId;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public long getId() {
        return id;
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

    public int getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFbId() {
        return fbId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStatus() {
        return status;
    }
}
