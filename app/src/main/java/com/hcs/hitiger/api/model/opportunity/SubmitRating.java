package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerRequest;

/**
 * Created by sanyamtyagi on 06/06/16.
 */
public class SubmitRating extends HitigerRequest {
    @SerializedName("oppId")
    private Long oppId;
    @SerializedName("rating")
    private int rating;

    public SubmitRating(String uniqueId, long userId, Long oppId, int rating) {
        super(uniqueId, userId);
        this.oppId = oppId;
        this.rating = rating;
    }
}
