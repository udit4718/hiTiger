package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by anuj gupta on 5/11/16.
 */
public class OpportunityRequest {
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("sportId")
    private long sportId;
    @SerializedName("price")
    private long price;
    @SerializedName("date")
    private long date;
    @SerializedName("time")
    private long time;
    @SerializedName("venueId")
    private long venueId;
    @SerializedName("creatorId")
    private long creatorId;
    @SerializedName("instructions")
    private List<String> instructions;

    public OpportunityRequest(String uuid, long sportId, long price, long date, long time, long venueId, long creatorId, List<String> instructions) {
        this.uuid = uuid;
        this.sportId = sportId;
        this.price = price;
        this.date = date;
        this.time = time;
        this.venueId = venueId;
        this.creatorId = creatorId;
        this.instructions = instructions;
    }

    public String getUuid() {
        return uuid;
    }

    public long getSportId() {
        return sportId;
    }

    public long getPrice() {
        return price;
    }

    public long getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public long getVenueId() {
        return venueId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public List<String> getInstructions() {
        return instructions;
    }
}
