package com.hcs.hitiger.api.model.club;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class CreateClubRequest extends HitigerRequest {
    @SerializedName("club")
    private CreateClub club;


    public CreateClubRequest(String uniqueId,Long userId, CreateClub club) {
        super(uniqueId,userId);

        this.club = club;
    }

    public CreateClub getClub() {
        return club;
    }
}
