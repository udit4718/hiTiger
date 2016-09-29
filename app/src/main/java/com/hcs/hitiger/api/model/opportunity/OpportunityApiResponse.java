package com.hcs.hitiger.api.model.opportunity;

import com.google.gson.annotations.SerializedName;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.api.model.user.VenueResponse;

import java.util.List;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class OpportunityApiResponse {
    @SerializedName("acceptorId")
    private long acceptorId;
    @SerializedName("id")
    private long id;
    @SerializedName("sportId")
    private long sportId;
    @SerializedName("uuid")
    private String uuid;
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
    @SerializedName("club")
    private Sport club;
    @SerializedName("user")
    private UserApiResponse user;
    @SerializedName("venue")
    private VenueResponse venue;
    @SerializedName("deleted")
    private boolean deleted;
    @SerializedName("gamesPlayed")
    private int gamesPlayed;
    @SerializedName("acceptor")
    private UserApiResponse acceptor;
    @SerializedName("reqNames")
    private List<String> reqNames;
    @SerializedName("versus")
    private UserApiResponse versus;
    @SerializedName("requesters")
    private List<UserApiResponse> requesters;
    @SerializedName("reqPending")
    private boolean reqPending;

    public boolean isReqPending() {
        return reqPending;
    }

    public Sport getClub() {
        return club;
    }

    public UserApiResponse getUser() {
        return user;
    }

    public VenueResponse getVenue() {
        return venue;
    }

    public long getAcceptorId() {
        return acceptorId;
    }

    public long getId() {
        return id;
    }

    public long getSportId() {
        return sportId;
    }

    public String getUuid() {
        return uuid;
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

    public boolean isDeleted() {
        return deleted;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public UserApiResponse getAcceptor() {
        return acceptor;
    }

    public List<String> getReqNames() {
        return reqNames;
    }

    public UserApiResponse getVersus() {
        return versus;
    }

    public List<UserApiResponse> getRequesters() {
        return requesters;
    }
}
