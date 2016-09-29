package com.hcs.hitiger.api;

import com.hcs.hitiger.api.model.HitigerRequest;
import com.hcs.hitiger.api.model.HitigerResponse;
import com.hcs.hitiger.api.model.categories.CategoryRequest;
import com.hcs.hitiger.api.model.categories.CategoryResponse;
import com.hcs.hitiger.api.model.club.CategoriesResponse;
import com.hcs.hitiger.api.model.club.CreateCategoryResponse;
import com.hcs.hitiger.api.model.club.CreateClubRequest;
import com.hcs.hitiger.api.model.follow.FollowRequest;
import com.hcs.hitiger.api.model.follow.FollowersResponse;
import com.hcs.hitiger.api.model.follow.FollowingList;
import com.hcs.hitiger.api.model.follow.FollowingResponse;
import com.hcs.hitiger.api.model.follow.UnFollowRequest;
import com.hcs.hitiger.api.model.notification.SubscribeNotification;
import com.hcs.hitiger.api.model.opportunity.CancelRequestToPlayRequest;
import com.hcs.hitiger.api.model.opportunity.CloseEvent;
import com.hcs.hitiger.api.model.opportunity.CreateOpportynityRequest;
import com.hcs.hitiger.api.model.opportunity.DeclineEventRequest;
import com.hcs.hitiger.api.model.opportunity.EventResponse;
import com.hcs.hitiger.api.model.opportunity.FinaliseRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunity;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailRequest;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityDetailResponse;
import com.hcs.hitiger.api.model.opportunity.GetOpportunityRequest;
import com.hcs.hitiger.api.model.opportunity.OpportunityResponse;
import com.hcs.hitiger.api.model.opportunity.SendRequestToPlayRequest;
import com.hcs.hitiger.api.model.opportunity.SubmitRating;
import com.hcs.hitiger.api.model.otp.GenerateOtpResponse;
import com.hcs.hitiger.api.model.otp.OtpRequest;
import com.hcs.hitiger.api.model.sports.GetAllSportsResponse;
import com.hcs.hitiger.api.model.sports.UpdateUserSportsRequest;
import com.hcs.hitiger.api.model.user.AnotherUserProfileRequest;
import com.hcs.hitiger.api.model.user.AnotherUserProfileResponse;
import com.hcs.hitiger.api.model.user.ClubRequest;
import com.hcs.hitiger.api.model.user.ClubResponse;
import com.hcs.hitiger.api.model.user.CreateUserVenueRequest;
import com.hcs.hitiger.api.model.user.LoginRequest;
import com.hcs.hitiger.api.model.user.LoginResponse;
import com.hcs.hitiger.api.model.user.PlayedWithResponse;
import com.hcs.hitiger.api.model.user.UpdateUserAddress;
import com.hcs.hitiger.api.model.user.User;
import com.hcs.hitiger.api.model.user.UserFullProfile;
import com.hcs.hitiger.api.model.user.UserResponseWithCode;
import com.hcs.hitiger.api.model.user.UserUniqueIdRequest;
import com.hcs.hitiger.api.model.user.UserUpdateRequest;
import com.hcs.hitiger.api.model.user.UserVenueResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ApiService {
    @POST("/generateOtp")
    public void generateOtp(@Body OtpRequest otpRequest, Callback<HitigerResponse> createOtpResponseCallback);

    @POST("/verifyOtp")
    public void verifyOtp(@Body OtpRequest otpRequest, Callback<GenerateOtpResponse> callback);

    @POST("/createUser")
    public void createUser(@Body User user, Callback<UserResponseWithCode> createUserResponseCallback);

    @POST("/updateUser")
    void updateUserProfile(@Body UserUpdateRequest userUpdateRequest, Callback<UserResponseWithCode> userResponceCallback);

    @POST("/getUserVenues")
    void getUserVenues(@Body UserUniqueIdRequest userVenueResponse, Callback<UserVenueResponse> userVenueResponseCallback);

    @POST("/createVenue")
    void createVenue(@Body CreateUserVenueRequest createUSerVenueRequest, Callback<UserVenueResponse> userVenueResponseCallback);

    @POST("/getFullProfile")
    void getUserProfile(@Body UserUniqueIdRequest userUniqueIdRequest, Callback<UserFullProfile> userFullProfileCallback);

    @POST("/getClubDetails")
    void getClubDetails(@Body ClubRequest clubRequest, Callback<ClubResponse> clubResponseCallback);

    @POST("/addUsersClub")
    void addUsersClub(@Body ClubRequest clubRequest, Callback<ClubResponse> clubResponseCallback);

    @POST("/removeUsersClub")
    void removeUsersClub(@Body ClubRequest clubRequest, Callback<ClubResponse> responseCallback);

    @POST("/getUserClubs")
    void getUsersClub(@Body UserUniqueIdRequest userUniqueIdRequest, Callback<CategoriesResponse> categoriesApiModelCallback);

    @POST("/getCategories")
    void getCategories(@Body UserUniqueIdRequest userUniqueIdRequest, Callback<CategoriesResponse> categoriesResponseCallback);

    @POST("/createOpportunity")
    void createOpportunity(@Body CreateOpportynityRequest createOpportynityRequest, Callback<OpportunityResponse> callback);

    @POST("/getCategoryClubs")
    void getCategoryClubs(@Body CategoryRequest categoryRequest, Callback<CategoryResponse> categoryRespunseCallback);

    @POST("/updateUserCatClubs")
    void updateUserCatClubs(@Body CategoryRequest categoryRequest, Callback<CategoryResponse> responseCodeCallback);

    @POST("/createClub")
    void createClub(@Body CreateClubRequest createClubRequest, Callback<CreateCategoryResponse> categoriesResponseCallback);

    @POST("/getSportClubs")
    void getAllSports(@Body HitigerRequest hitigerRequest, Callback<GetAllSportsResponse> getAllSportsResponseCallback);

    @POST("/updateUserSportClubs")
    void updateUserSportsClub(@Body UpdateUserSportsRequest updateUserSportsRequest, Callback<HitigerResponse> hitigerResponseCallback);

    @POST("/getUserFollowersList")
    void getUserFollowersList(@Body FollowingList hitigerRequest, Callback<FollowersResponse> followersResponseCallback);

    @POST("/getAnotherUserProfile")
    void getAnotherUserProfile(@Body AnotherUserProfileRequest anotherUserProfileRequest, Callback<AnotherUserProfileResponse> userFullProfileCallback);

    @POST("/getAnotherUserProfile")
    AnotherUserProfileResponse getAnotherUserProfile(@Body AnotherUserProfileRequest anotherUserProfileRequest);

    @POST("/unfollowUser")
    void unfollowUser(@Body UnFollowRequest followingRequest, Callback<HitigerResponse> hitigerResponseCallback);

    @POST("/follow")
    void follow(@Body FollowRequest followingRequest, Callback<HitigerResponse> hitigerResponseCallback);

    @POST("/getOpportunities")
    void getOpportunities(@Body GetOpportunityRequest getOpportunityRequest, Callback<GetOpportunity> opportunityResponseCallback);

    @POST("/sendRequest")
    void sendRequest(@Body SendRequestToPlayRequest sendRequestToPlayRequest, Callback<HitigerResponse> hitigerResponseCallback);

    @POST("/cancelRequest")
    void cancelRequest(@Body CancelRequestToPlayRequest cancelRequestToPlayRequest, Callback<HitigerResponse> hitigerResponseCallback);

    @POST("/declineEventRequest")
    void declineEventRequest(@Body DeclineEventRequest declineEventRequest, Callback<HitigerResponse> hitigerResponseCallback);

    @POST("/loginUser")
    void loginUser(@Body LoginRequest hitigerRequest, Callback<LoginResponse> loginResponseCallback);

    @POST("/getOppDetails")
    void getOppDetails(@Body GetOpportunityDetailRequest getOpportunityDetailRequest, Callback<GetOpportunityDetailResponse> callback);

    @POST("/getOppDetails")
    GetOpportunityDetailResponse getOppDetails(@Body GetOpportunityDetailRequest getOpportunityDetailRequest);

    @POST("/getEvents")
    void getEvents(@Body HitigerRequest hitigerRequest, Callback<EventResponse> eventResponseCallback);

    @POST("/getPlayedWith")
    void getPlayedWith(@Body FollowingList hitigerRequest, Callback<PlayedWithResponse> callback);

    @POST("/acceptRequest")
    void acceptRequest(@Body FinaliseRequest finaliseRequest, Callback<HitigerResponse> responseCallback);

    @POST("/getFollowingList")
    void getFollowingList(@Body FollowingList hitigerRequest, Callback<FollowingResponse> callback);

    @POST("/subscribeNotification")
    void subscribeNotification(@Body SubscribeNotification subscribeNotification, Callback<HitigerResponse> callback);

    @POST("/getOppRatingDialog")
    void checkForRating(@Body HitigerRequest request, Callback<GetOpportunityDetailResponse> callback);

    @POST("/rateEvent")
    void rateEvent(@Body SubmitRating request, Callback<HitigerResponse> callback);

    @POST("/updateUserAddress")
    void updateUserAddress(@Body UpdateUserAddress request, Callback<HitigerResponse> callback);

    @POST("/closeEvent")
    void closeEvent(@Body CloseEvent request, Callback<HitigerResponse> callback);

}