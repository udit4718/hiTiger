package com.hcs.hitiger.model;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.api.model.user.UserApiResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/9/16.
 */
public class UserProfileDetail implements Parcelable {
    private static final String USER_PROFILE = "USER_PROFILE";
    private static final String SHARED_PREFERENCE = "SHARED_PREFERENCE";

    private long id;
    private String name;
    private long createTime;
    private String role;
    private String status;
    private String password;
    private String email;
    private String mobile;
    private String fbId;
    private String imageUrl;
    private boolean following;
    private String address;
    private double lat;
    private double lng;
    private boolean supportUser;
    private long followersCount;
    private long followingCount;
    private long gamesPlayed;

    public UserProfileDetail(long id, String name, long createTime, String role, String status, String password, String email, String mobile, String fbId, String imageUrl, boolean following, String address, double lat, double lng, boolean supportUser, long followersCount, long followingCount, long gamesPlayed) {
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.role = role;
        this.status = status;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.fbId = fbId;
        this.imageUrl = imageUrl;
        this.following = following;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.supportUser = supportUser;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.gamesPlayed = gamesPlayed;
    }

    protected UserProfileDetail(Parcel in) {
        id = in.readLong();
        name = in.readString();
        createTime = in.readLong();
        role = in.readString();
        status = in.readString();
        password = in.readString();
        email = in.readString();
        mobile = in.readString();
        fbId = in.readString();
        imageUrl = in.readString();
        following = in.readByte() != 0;
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        supportUser = in.readByte() != 0;
        followersCount = in.readLong();
        followingCount = in.readLong();
        gamesPlayed = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(createTime);
        dest.writeString(role);
        dest.writeString(status);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(fbId);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (following ? 1 : 0));
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeByte((byte) (supportUser ? 1 : 0));
        dest.writeLong(followersCount);
        dest.writeLong(followingCount);
        dest.writeLong(gamesPlayed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserProfileDetail> CREATOR = new Creator<UserProfileDetail>() {
        @Override
        public UserProfileDetail createFromParcel(Parcel in) {
            return new UserProfileDetail(in);
        }

        @Override
        public UserProfileDetail[] newArray(int size) {
            return new UserProfileDetail[size];
        }
    };

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public long getId() {
        return id;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
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

    public boolean isFollowing() {
        return following;
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

    public boolean isSupportUser() {
        return supportUser;
    }

    public static UserProfileDetail createUser(UserApiResponse userApiResponse) {
        UserProfileDetail userProfileDetail = new UserProfileDetail(userApiResponse.getId(), userApiResponse.getName(), userApiResponse.getCreateTime(),
                userApiResponse.getRole(), userApiResponse.getStatus(), userApiResponse.getPassword(), userApiResponse.getEmail(),
                userApiResponse.getMobile(), userApiResponse.getFbId(), userApiResponse.getImageUrl(), userApiResponse.isFollowing(),
                userApiResponse.getAddress(), userApiResponse.getLat(), userApiResponse.getLng(), userApiResponse.isSupportUser(),
                userApiResponse.getFollowersCount(), userApiResponse.getFollowingCount(), userApiResponse.getGamesPlayed());
        return userProfileDetail;
    }

    public static List<UserProfileDetail> createUserList(List<UserApiResponse> userApiResponse) {
        List<UserProfileDetail> userProfileDetail = new ArrayList<>();
        if (userApiResponse != null) {
            for (int i = 0; i < userApiResponse.size(); i++) {
                userProfileDetail.add(new UserProfileDetail(userApiResponse.get(i).getId(), userApiResponse.get(i).getName(),
                        userApiResponse.get(i).getCreateTime(), userApiResponse.get(i).getRole(), userApiResponse.get(i).getStatus(),
                        userApiResponse.get(i).getPassword(), userApiResponse.get(i).getEmail(), userApiResponse.get(i).getMobile(),
                        userApiResponse.get(i).getFbId(), userApiResponse.get(i).getImageUrl(), userApiResponse.get(i).isFollowing(),
                        userApiResponse.get(i).getAddress(), userApiResponse.get(i).getLat(), userApiResponse.get(i).getLng(),
                        userApiResponse.get(i).isSupportUser(), userApiResponse.get(i).getFollowersCount(),
                        userApiResponse.get(i).getFollowingCount(), userApiResponse.get(i).getGamesPlayed()));
            }
        }
        return userProfileDetail;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public static UserProfileDetail getUserProfileDetail() {
        SharedPreferences mPrefs = HitigerApplication.getInstance().getSharedPreferences();
        String json = mPrefs.getString(USER_PROFILE, null);
        if (json != null) {
            Gson gson = new Gson();
            UserProfileDetail mUserProfile = gson.fromJson(json, UserProfileDetail.class);
            return mUserProfile;
        } else {
            return null;
        }
    }

    public static void setUserProfileDetail(UserProfileDetail userProfile) {
        HitigerApplication.getInstance().setUserProfileDetail(userProfile);
        SharedPreferences mPrefs = HitigerApplication.getInstance().getSharedPreferences();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userProfile);
        prefsEditor.putString(USER_PROFILE, json);
        prefsEditor.commit();
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
