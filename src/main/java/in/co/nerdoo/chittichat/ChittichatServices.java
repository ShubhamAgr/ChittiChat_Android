package in.co.nerdoo.chittichat;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by shubham on 8/11/16.
 */


public interface ChittichatServices {

    //Login Related Urls...

    @POST("loginWithFacebook")
    Observable<ResponseMessage> getResponseOnLoginWithFacebook(@Body LoginInformation loginInformation);

    @POST("loginWithEmail")
    Observable<ResponseMessage> getResponseOnLoginWithEmail(@Body LoginWithEmailInformation loginWithEmailInformation);

    @POST("loginWithUsername")
    Observable<ResponseMessage> getResponseOnLoginWithUsername(@Body LoginWithUserNameInformation loginWithUserNameInformation);

    //Signup Related Urls

    @POST("signupWithChittiChat")
    Observable<ResponseMessage> getResponseOnSignupWithChittiChat(@Body SignupWithChittichatInformation signupWithChittichatInformation );

    @POST("signupWithFacebook")
    Observable<ResponseMessage> getResponseOnSignupWithFacebook(@Body SignupWithFacebookInformation signupWithFacebookInformation);


    //Groups Related Urls

    @POST("/newGroup")
    Observable<ResponseOnNewGroup> getResponseOnNewGroup(@Body NewGroupInformation newGroupInformation);

    @GET("/groups/{token}")
    Observable<List<GroupsList>> getResponseOnGroups(@Path("token") String token);

    @GET("/groupDetail/{token}/{groupId}")
    Observable<GroupDetail> getResponseOnGroupDetail(@Path("token")String token,@Path("groupId") String groupId);


    @POST("/newRequest")
    Observable<ResponseMessage> getResponseOnNewRequest(@Body NewRequestInformation newRequestInformation);

    @GET("/requests/{groupid}")
    Observable<List<groupRequestsNotification>> getGroupRequests(@Path("groupid") String groupid);

    @POST("/accept_request")
    Observable<ResponseMessage>  getResponseOnAcceptRequest(@Body ResponseRequestInformation responseRequestInformation);

    @POST("/deny_request")
    Observable<ResponseMessage> getResponseOnDenyRequest(@Body ResponseRequestInformation responseRequestInformation);
    @GET("/followGroup/{token}/{groupid}")
    Observable<ResponseMessage> getResponseOnFollowingGroup(@Path("token") String token,@Path("groupid") String groupId);

    @GET("/unfollowGroup/{token}/{groupid}")
    Observable<ResponseMessage> getResponseOnUnFollowingGroup(@Path("token") String token, @Path("groupid") String groupId);
    // groupId

    @GET("/members/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnMembers(@Path("token") String token,@Path("groupId") String groupId);

    @GET("/join/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnJoin(@Path("token") String token,@Path("groupId") String groupId);

    @GET("/leave/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnLeave(@Path("token") String token, @Path("groupId") String groupId);//pass token and groupId Param



   //Topics Related urls


    @POST("newTopic")
    Observable<ResponseMessage> getResponseOnNewTopic(@Body NewTopicInformation newTopicInformation);

    @GET("allTopics/{token}/{groupId}")
    Observable<List<Topics>> getResponseOnAllTopics(@Path("token") String token, @Path("groupId") String groupId);

    @GET("topicsWithArticle/{token}/{groupId}")
    Observable<List<TopicsWithArticle>> getResponseOnTopicsWithArticle(@Path("token") String token,@Path("groupId")String groupId); //pass token and groupId
    // Param
    @GET("topicByTopicId/{token}/{topicid}")
    Observable<List<Topics>> getReponseOnNewTopic(@Path("token") String token,@Path("topicid")String topicid);

    @GET("articles/{token}/{topicId}/{range}")
    Observable<List<Articles>> getResponseOnArticles(@Path("token") String token,@Path("topicId") String topicId,@Path("range") String range);



    //pass token, topicId and
    // range Param

    @POST("article")
    Observable<ResponseMessage> getResponseOnPostArticle(@Body ArticleInformation articleInformation);

    @Multipart
    @POST("image")
    Observable<ResponseMessage> getResponseOnPostImage(@Part("file\"; filename=\"pp.png\" ") RequestBody file, @Part("token") RequestBody token,
                                                 @Part("topicId") RequestBody id,@Part("username") RequestBody username);


    @Multipart
    @POST("updateGroupProfilepic")
    Observable<ResponseMessage> getResponseOnGroupImage(@Part("file\"; filename=\"pp.png\" ") RequestBody file, @Part("token") RequestBody token,
                                                       @Part("groupId") RequestBody id);
    @Multipart
    @POST("groupImage")
    Observable<ResponseMessage> getResponseOnPostGroupImage(@Part("image") MultipartBody.Part image, @Part("description") RequestBody description);


    @Multipart
    @POST("audio")
    Observable<ResponseMessage> getResponseOnPostAudio(@Part("audio")MultipartBody.Part audio, @Part("description")RequestBody description);

    @Multipart
    @POST("video")
    Observable<ResponseMessage> getResponseOnPostVideo(@Part("video")MultipartBody.Part video, @Part("description") RequestBody description);


    //Search Url...

    @POST("search")
    Observable<ResponseMessage> getResponseOnSearch(@Body String query);

    @POST("searchGroups")
    Observable<List<GroupSearchResult>> getResponseOnSearchGroups(@Body SearchRequest searchRequest);

    //Administrative controls urls
           
    @POST("addMember")
    Observable<ResponseMessage>  getResponseOnAddMember();

    @POST("removeMember")
    Observable<ResponseMessage> getResponseOnRemoveMember();

    @POST("deleteContent")
    Observable<ResponseMessage> getResponseOnDeleteContent();

    ///
    @GET("getArticleByArticleId/{article_id}")
    Observable<List<Articles>>  getArticles(@Path("article_id") String article_id);

    @GET("getUsernameByUserId/{user_id}")
    Observable<Username> getUsername(@Path("user_id") String user_id);

}
