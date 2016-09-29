package com.hcs.hitiger.api.model.sports;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.user.Sport;

import java.util.List;

/**
 * Created by hot cocoa on 13/05/16.
 */
public class GetAllSportsResponse extends HitigerResponse {
    @SerializedName("clubs")
    private List<Sport> sportList;

    public List<Sport> getSportList() {
        return sportList;
    }
}
