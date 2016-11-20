package in.co.nerdoo.chittichat;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import javax.inject.Inject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopicActivity extends AppCompatActivity {
    @Inject
    Socket socket;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    private static String token;
    private static String topicId;
    private  static ChittichatServices chittichatServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            topicId = null;
        }else{
            topicId = extras.getString("topicId");
            token = sharedPreferences.getString("ChittiChat_token",null);
        }
        chittichatServices = retrofit.create(ChittichatServices.class);
    }

    private  void fetchArticles(final String range){
        Observable<Articles> getArticles = chittichatServices.getResponseOnArticles(token,topicId,range);
        getArticles.subscribeOn(Schedulers.newThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Articles>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Articles articles) {

            }
        });

    }


    private void postArticle(final String article){
        ArticleInformation articleInformation = new ArticleInformation(token,topicId,article);
        Observable<ResponseMessage> getResponseOnArticleUpload = chittichatServices.getResponseOnPostArticle(articleInformation);
        getResponseOnArticleUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });

    }


    private void postImage(Uri imageUri){
        File file = new File(imageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",file.getName(),requestFile);
        String descriptionString = "This is image uploaded by user and user Id is......";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),descriptionString);
        Observable<ResponseMessage> getResponseOnImageUpload = chittichatServices.getResponseOnPostImage(body,description);
        getResponseOnImageUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });

    }

    private void postVideo(Uri videoUri){
        File file = new File(videoUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("video",file.getName(),requestFile);
        String descriptionString = "This is video uploaded by user and user Id is......";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),descriptionString);
        Observable<ResponseMessage> getResponseOnVideoUpload = chittichatServices.getResponseOnPostVideo(body,description);
        getResponseOnVideoUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });

    }
    private  void postAudio(Uri audioUri){
        File file = new File(audioUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio",file.getName(),requestFile);
        String descriptionString = "This is audio uploaded by user and user Id is......";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),descriptionString);
        Observable<ResponseMessage> getResponseOnAudioUpload = chittichatServices.getResponseOnPostAudio(body,description);
        getResponseOnAudioUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });
    }

    private void socketMethods(){
        socket.on("newimage",new Emitter.Listener(){
            @Override
            public void call(Object... args){

            }
        });
        socket.on("newarticle",new Emitter.Listener(){
            @Override
            public void call(Object... args){

            }
        });
        socket.on("newvideo",new Emitter.Listener(){
            @Override
            public void call(Object... args){

            }
        });
        socket.on("newaudio",new Emitter.Listener(){
            @Override
            public void call(Object... args){

            }
        });

        socket.on("newmember",new Emitter.Listener(){
           @Override
            public void call(Object... args){

           }
        });
    }


}

class  ArticleInformation{
    String token,topicId,article;

    public ArticleInformation(String token, String topicId, String article) {
        this.token = token;
        this.topicId = topicId;
        this.article = article;
    }
}
