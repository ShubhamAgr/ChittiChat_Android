package in.co.nerdoo.chittichat;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    Observable<ResponseMessage> getResponseOnNewGroup(@Body NewGroupInformation newGroupInformation);

    @GET("/groups/{token}")
    Observable<List<GroupsList>> getResponseOnGroups(@Path("token") String token);

    @GET("/groupDetail/{groupId}")
    Observable<GroupDetail> getResponseOnGroupDetail(@Path("groupId") String groupId);


    @POST("/newRequest")
    Observable<ResponseMessage> getResponseOnNewRequest(@Body String token, @Body String groupId, @Body String knockknockAnswer);

    @GET("/followGroup/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnFollowingGroup(@Path("token") String token,@Path("groupId") String groupId);

    @GET("/unfollowGroup/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnUnFollowingGroup(@Path("token") String token, @Path("groupId") String groupId);
    // groupId

    @GET("/members/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnMembers(@Path("token") String token,@Path("groupId") String groupId);

    @GET("/join/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnJoin(@Path("token") String token,@Path("groupId") String groupId);

    @GET("/leave/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnLeave(@Path("token") String token, @Path("groupId") String groupId);//pass token and groupId Param



   //Topics Related urls


    @POST("newTopic")
    Observable<ResponseMessage> getResponseOnNewTopic(@Body String token,@Body String groupId,@Body String topicTitle,@Body String topicDescription);

    @GET("allTopics/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnAllTopics(@Path("token") String token, @Path("groupId") String groupId);

    @GET("topicsWithArticle/{token}/{groupId}")
    Observable<ResponseMessage> getResponseOnTopicsWithArticle(@Path("token") String token,@Path("groupId")String groupId); //pass token and groupId
    // Param

    @GET("articles/{token}/{topicId}/{range}")
    Observable<Articles> getResponseOnArticles(@Path("token") String token,@Path("topicId") String topicId,@Path("range") String range);
    //pass token, topicId and
    // range Param

    @POST("article")
    Observable<ResponseMessage> getResponseOnPostArticle(@Body String token,@Body String topicId,@Body String article);

    @Multipart
    @POST("image")
    Observable<ResponseMessage> getResponseOnPostImage(@Part("image") MultipartBody.Part image, @Part("description") RequestBody description);

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

    //Administrative controls urls

    @POST("addMember")
    Observable<ResponseMessage>  getResponseOnAddMember();

    @POST("removeMember")
    Observable<ResponseMessage> getResponseOnRemoveMember();

    @POST("deleteContent")
    Observable<ResponseMessage> getResponseOnDeleteContent();

}
