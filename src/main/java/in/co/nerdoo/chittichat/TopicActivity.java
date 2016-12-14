package in.co.nerdoo.chittichat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private EditText articleContent;
    private int PICK_IMAGE_REQUEST = 1;
    private List<Articles> articlesList;
    private static RecyclerView recyclerView;
    private static Boolean ShowEdittext;
    private  static LinearLayout sendTextLayout;
    private  static ArticleAdapter articleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        articleContent = (EditText) findViewById(R.id.articleText);
        sendTextLayout = (LinearLayout)findViewById(R.id.linearLayout2) ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_articles);
        toolbar.setTitle("Chats");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            topicId = null;
        }else{
            topicId = extras.getString("TopicId");
            ShowEdittext = extras.getBoolean("ShowEdittext");
            token = sharedPreferences.getString("ChittiChat_token",null);
        }
        if(!ShowEdittext){
            sendTextLayout.setVisibility(View.GONE);
        }
        socket.connect();
        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",topicId);
            socket.emit("joinRoom",joinRoom);
        }catch (JSONException e){
            Log.e("problem",e.getMessage());
        }
        if(socket.connected()){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("authorize",jsonObject);
            }catch (JSONException je){
                Log.e("Exception_Authorization",je.getMessage());
            }
        }
        socket.on("newarticle",onNewArticle);

        chittichatServices = retrofit.create(ChittichatServices.class);
        recyclerView = (RecyclerView) findViewById(R.id.articles_recycler_view);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        getArticle(token,topicId,"0_5");


    }

    @Override
    public  void onPause(){
        super.onPause();
        socket.connect();
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", topicId);
            joinRoomrequest.put("token",sharedPreferences.getString("ChittiChat_token","null"));
            socket.emit("leaveRoom", joinRoomrequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(socket.connected()){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("app_close",jsonObject);
            }catch (JSONException je){
                Log.e("Exception_Authorization",je.getMessage());
            }
        }
    }
    @Override
    public void  onStop(){
        super.onStop();

    }
    @Override
    public void onResume(){
        super.onResume();
        socket.connect();
        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",topicId);
            socket.emit("joinRoom",joinRoom);
        }catch (JSONException e){
            Log.e("problem",e.getMessage());
        }
        if(socket.connected()){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("authorize",jsonObject);
            }catch (JSONException je){
                Log.e("Exception_Authorization",je.getMessage());
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", topicId);
            joinRoomrequest.put("token",sharedPreferences.getString("ChittiChat_token","null"));
            socket.emit("leaveRoom", joinRoomrequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    private void getArticle(String token,String topicId,String range){
        Observable<List<Articles>> getArticles = chittichatServices.getResponseOnArticles(token,topicId,range);
        getArticles.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Articles>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Articles> articles) {
                try {
                    articlesList = articles;
                     articleAdapter = new ArticleAdapter(articlesList);
                    for(Articles article:articlesList){
                        getUsernameByUserId(article);
                    }
                     recyclerView.setAdapter(articleAdapter);
                     recyclerView.scrollToPosition(0);
                }catch (Exception e){
                    Log.e("ArticleEx:",e.getMessage());
                }

//                Log.d("Chittichat_Articles",articles.get(0).get_id());
            }
        });

    }
    private  void getArticleByArticleId(String articleId){
        Observable<List<Articles>> getArticle = chittichatServices.getArticles(articleId);
        getArticle.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Articles>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Articles> articles) {
                Log.d("newArticle",articles.get(0).get_id());
                getUsernameByUserId(articles.get(0));
                articlesList.add(0,articles.get(0));
                articleAdapter.notifyDataSetChanged();

            }
        });
    }

    private void getUsernameByUserId(final Articles article){
        Observable<Username> getUsername = chittichatServices.getUsername(article.getPublishedBy());
        getUsername.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Username>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("err",e.getMessage());

            }

            @Override
            public void onNext(Username username) {
                Log.d("username",username.getUsername());
                article.setUsername(username.getUsername());
                articleAdapter.notifyDataSetChanged();

            }
        });
    }
    public void onaddImage(View view){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try{
            startActivityForResult(intent,PICK_IMAGE_REQUEST);//Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }catch (Exception e){
            Log.d("e",e.getMessage());
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPathFromURI(Uri contentUri) {
        //Will work only in kitkat......... and above...
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.i("ImageURI",uri.toString());
            postImage(uri);
        }
    }

    private void postArticle(ArticleInformation articleInformation ){
        Observable<ResponseMessage> getResponseOnArticleUpload = chittichatServices.getResponseOnPostArticle(articleInformation);
        getResponseOnArticleUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                Log.d("PostArticle","completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {
                    try {
                        Log.d("Article sent",responseMessage.getMessage());
                    }catch (Exception e){
                        Log.e("Article sent Ex:",e.getMessage());
                    }



            }
        });

    }


    private void postImage(Uri imageUri){
        String path = getPathFromURI(imageUri);
        if(path.equals("")){
                Log.d("imageUri",imageUri.toString());
                Log.d("imageUri.path",imageUri.getPath());
                Log.d("file path:","is null");
        }else{
            Log.d("file path",path);
            File file = new File(path);
            RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody mytoken = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody mtopicId = RequestBody.create(MediaType.parse("text/plain"), topicId);

            Observable<ResponseMessage> getResponseOnImageUpload = chittichatServices.getResponseOnPostImage(fbody,mytoken,mtopicId);
             getResponseOnImageUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
                 @Override
                 public void onCompleted() {
                     Log.d("PostImage","completed");
                 }

                 @Override
                 public void onError(Throwable e) {
                    Log.e("PostImage",e.getMessage());
                 }

                 @Override
                 public void onNext(ResponseMessage responseMessage) {
                     Log.d("Image",responseMessage.getMessage());
                 }
             });

        }
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


    private  Emitter.Listener onNewArticle  =  new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

//                        Toast.makeText(getApplicationContext(),"kuch to hua",Toast.LENGTH_SHORT).show();
                        ;
                        Log.i("aabccccdsfds",data.getString("articleId"));
                        getArticleByArticleId(data.getString("articleId"));
                    }catch (Exception e) {
                        Log.e("Socket Exception",e.getMessage());
                    }
                }
            });
        }
    };


    public void onClickSend(View view){
        //upload your articles....
        String text = articleContent.getText().toString();
        articleContent.setText("");
        if(!text.equals("")){
            Log.d("Article",text);
            Log.d("TopicId",topicId+"abbb");
            Log.d("texts",text);
            ArticleInformation articleInformation = new ArticleInformation(token,topicId,text);
            postArticle(articleInformation);


        }

    }

}

class  ArticleInformation{
    String token,topic_id,marticle;

    public ArticleInformation(String token, String topicId, String article_content) {
        this.token = token;
        this.topic_id = topicId;
        this.marticle= article_content;
    }
}
class  MediaInformation{
    String token,topicId,mediatype;

    public MediaInformation(String token, String topicId, String mediatype) {
        this.token = token;
        this.topicId = topicId;
        this.mediatype = mediatype;
    }
}
class Articles{
    private String _id,username,created_on,published_by,content_type,article_content;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedOn() {
        return created_on;
    }

    public void setCreatedOn(String createdOn) {
        this.created_on = created_on;
    }

    public String getPublishedBy() {
        return published_by;
    }

    public void setPublishedBy(String publishedBy) {
        this.published_by = published_by;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getArticle_content() {
        return article_content;
    }

    public void setArticle_content(String article_content) {
        this.article_content = article_content;
    }
}

class Username{
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
}