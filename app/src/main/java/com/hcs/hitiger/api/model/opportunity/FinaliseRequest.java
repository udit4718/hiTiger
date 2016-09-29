package com.hcs.hitiger.api.model.opportunity;

/**
 * Created by sanyamtyagi on 31/05/16.
 */
public class FinaliseRequest {

    String uniqueId;
    Long oppId;
    Long requesterId;

    public FinaliseRequest(String uniqueId, Long oppId, Long requesterId) {
        this.uniqueId = uniqueId;
        this.oppId = oppId;
        this.requesterId = requesterId;
    }
}
