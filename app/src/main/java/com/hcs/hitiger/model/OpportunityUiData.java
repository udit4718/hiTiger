package com.hcs.hitiger.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.hcs.hitiger.api.model.opportunity.OpportunityApiResponse;
import com.hcs.hitiger.api.model.user.Sport;
import com.hcs.hitiger.api.model.user.UserApiResponse;
import com.hcs.hitiger.api.model.user.VenueResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by anuj gupta on 5/12/16.
 */
public class OpportunityUiData implements Parcelable, Comparator<OpportunityUiData> {
    private long acceptorId;
    private long id;
    private long sportId;
    private String uuid;
    private long price;
    private long date;
    private long time;
    private long venueId;
    private long creatorId;
    private List<String> instructions;
    private Sport club;
    private UserApiResponse user;
    private VenueResponse venue;
    private boolean isCancelRequest;
    private boolean deleted;
    private int gamesPlayed;
    private UserApiResponse acceptor;
    private List<String> reqNames;
    private UserApiResponse versus;
    private List<UserApiResponse> requesters;
    private boolean reqPending;

    public OpportunityUiData(long acceptorId, long id, long sportId, String uuid, long price, long date, long time, long venueId, long creatorId, List<String> instructions, Sport club, UserApiResponse user, VenueResponse venue, boolean deleted, int gamesPlayed, UserApiResponse acceptor, List<String> reqNames, UserApiResponse versus, List<UserApiResponse> requesters, boolean reqPending) {
        this.acceptorId = acceptorId;
        this.id = id;
        this.sportId = sportId;
        this.uuid = uuid;
        this.price = price;
        this.date = date;
        this.time = time;
        this.venueId = venueId;
        this.creatorId = creatorId;
        this.instructions = instructions;
        this.club = club;
        this.user = user;
        this.venue = venue;
        this.deleted = deleted;
        this.gamesPlayed = gamesPlayed;
        this.acceptor = acceptor;
        this.reqNames = reqNames;
        this.versus = versus;
        this.requesters = requesters;
        this.reqPending = reqPending;
    }

    protected OpportunityUiData(Parcel in) {
        acceptorId = in.readLong();
        id = in.readLong();
        sportId = in.readLong();
        uuid = in.readString();
        price = in.readLong();
        date = in.readLong();
        time = in.readLong();
        venueId = in.readLong();
        creatorId = in.readLong();
        instructions = in.createStringArrayList();
        club = in.readParcelable(Sport.class.getClassLoader());
        user = in.readParcelable(UserApiResponse.class.getClassLoader());
        venue = in.readParcelable(VenueResponse.class.getClassLoader());
        versus = in.readParcelable(VenueResponse.class.getClassLoader());
        ;
        isCancelRequest = in.readByte() != 0;
        reqPending = in.readByte() != 0;
    }

    public OpportunityUiData() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(acceptorId);
        dest.writeLong(id);
        dest.writeLong(sportId);
        dest.writeString(uuid);
        dest.writeLong(price);
        dest.writeLong(date);
        dest.writeLong(time);
        dest.writeLong(venueId);
        dest.writeLong(creatorId);
        dest.writeStringList(instructions);
        dest.writeParcelable(club, flags);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(venue, flags);
        dest.writeParcelable(versus, flags);
        dest.writeByte((byte) (isCancelRequest ? 1 : 0));
        dest.writeByte((byte) (reqPending ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OpportunityUiData> CREATOR = new Creator<OpportunityUiData>() {
        @Override
        public OpportunityUiData createFromParcel(Parcel in) {
            return new OpportunityUiData(in);
        }

        @Override
        public OpportunityUiData[] newArray(int size) {
            return new OpportunityUiData[size];
        }
    };

    public static OpportunityUiData getOpportunityOrEventData(OpportunityApiResponse opportunity) {
        return new OpportunityUiData(opportunity.getAcceptorId(), opportunity.getId(), opportunity.getSportId(), opportunity.getUuid(),
                opportunity.getPrice(), opportunity.getDate(), opportunity.getTime(), opportunity.getVenueId(), opportunity.getCreatorId(),
                opportunity.getInstructions(), opportunity.getClub(), opportunity.getUser(), opportunity.getVenue(), opportunity.isDeleted(),
                opportunity.getGamesPlayed(), opportunity.getAcceptor(), opportunity.getReqNames(), opportunity.getVersus(),
                opportunity.getRequesters(), opportunity.isReqPending());
    }

    public static List<OpportunityUiData> getOpportunityOrEventFullListData(List<OpportunityApiResponse> opportunityApiResponseList) {
        List<OpportunityUiData> opportunityUiDataList = new ArrayList<>();
        for (OpportunityApiResponse opportunity : opportunityApiResponseList)
            opportunityUiDataList.add(new OpportunityUiData(opportunity.getAcceptorId(), opportunity.getId(), opportunity.getSportId(),
                    opportunity.getUuid(), opportunity.getPrice(), opportunity.getDate(), opportunity.getTime(), opportunity.getVenueId(),
                    opportunity.getCreatorId(), opportunity.getInstructions(), opportunity.getClub(), opportunity.getUser(),
                    opportunity.getVenue(), opportunity.isDeleted(), opportunity.getGamesPlayed(), opportunity.getAcceptor(),
                    opportunity.getReqNames(), opportunity.getVersus(), opportunity.getRequesters(), opportunity.isReqPending()));
        return opportunityUiDataList;
    }

    public boolean isCancelRequest() {
        return isCancelRequest;
    }

    public void setCancelRequest(boolean cancelRequest) {
        isCancelRequest = cancelRequest;
    }

    public long getAcceptorId() {
        return acceptorId;
    }

    public long getId() {
        return id;
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

    public boolean isReqPending() {
        return reqPending;
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(OpportunityUiData obj1, OpportunityUiData obj2) {
        return Long.compare(obj1.getDate(), obj2.getDate());
    }
}
