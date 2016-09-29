package com.hcs.hitiger.api.model.user;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/16/16.
 */
public class AnotherUserProfileResponse extends HitigerResponse {
    @SerializedName("user")
    private UserApiResponse user;
    @SerializedName("sportClubs")
    private List<Sport> sports;
    @SerializedName("following")
    private boolean following;
    @SerializedName("commonClubs")
    private List<ClubApiModel> commonClubs;
    @SerializedName("more")
    private List<ClubApiModel> more;

    public UserApiResponse getUser() {
        return user;
    }

    public List<Sport> getSports() {
        return sports;
    }

    public boolean isFollowing() {
        return following;
    }

    public List<ClubApiModel> getCommonClubs() {
        return commonClubs;
    }

    public List<ClubApiModel> getMore() {
        return more;
    }
}
